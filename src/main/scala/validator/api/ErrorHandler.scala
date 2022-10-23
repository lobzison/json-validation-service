package validator.api

import cats.Monad
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxFlatMapOps, toFunctorOps}
import org.http4s.Response
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import validator.model.errors.{ServiceError, UnexpectedError}

class ErrorHandler[F[_]: Logger: Monad] extends Http4sDsl[F] {

  val handleErrors: Throwable => F[Response[F]] = err => {
    val processedError =
      err match {
        case e: ServiceError => e.pure[F]
        case _               => Logger[F].error(err)("") >> UnexpectedError().pure[F]
      }
    processedError.map((e: ServiceError) => Response[F](status = e.httpCode).withEntity(e))
  }
}

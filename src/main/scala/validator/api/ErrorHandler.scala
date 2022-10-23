package validator.api

import cats.Monad
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxFlatMapOps}
import org.http4s.Response
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import validator.model.errors.{ServiceError, UnexpectedError}

class ErrorHandler[F[_]: Logger: Monad] extends Http4sDsl[F] {

  val handleErrors: Throwable => F[Response[F]] = err =>
    Logger[F].warn(err)("") >> {
      val processedError =
        err match {
          case e: ServiceError => e
          case _               => UnexpectedError()
        }
      Response[F](status = processedError.httpCode).withEntity(processedError).pure[F]
    }
}

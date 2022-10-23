package validator.api

import cats.Monad
import cats.effect.std.Console
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxFlatMapOps}
import org.http4s.Response
import org.http4s.dsl.Http4sDsl
import validator.model.errors.{ServiceError, UnexpectedError}

class ErrorHandler[F[_]: Console: Monad] extends Http4sDsl[F] {

  val handleErrors: Throwable => F[Response[F]] = err =>
    Console[F].error(err) >> {
      val processedError =
        err match {
          case e: ServiceError => e
          case _               => UnexpectedError()
        }
      Response[F](status = processedError.httpCode).withEntity(processedError).pure[F]
    }
}

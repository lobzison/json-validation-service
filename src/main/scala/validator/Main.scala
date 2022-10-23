package validator

import cats.effect.{IO, IOApp, Resource}
import com.comcast.ip4s.Port
import doobie.Transactor
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.{Router, Server}
import pureconfig.ConfigSource
import validator.api.{ErrorHandler, Routes, RoutesImpl}
import validator.persistence._
import validator.persistence.db.DBImpl
import validator.service._
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._
import pureconfig.ConfigConvert.fromReaderAndWriter
import validator.model.config.Config

object Main extends IOApp.Simple {
  override def run: IO[Unit] = {
    (for {
      config <- Resource.eval(ConfigSource.default.loadF[IO, Config]())
      xa     <- new DBImpl[IO](config.db).transactor
      res    <- program(xa, config)
    } yield res).useForever
  }

  def program(xa: Transactor[IO], config: Config): Resource[IO, Server] = {
    val persistence: SchemaPersistence[IO]      = new SchemaPersistenceImpl[IO](xa)
    val validation: SchemaValidationService[IO] = new SchemaValidationServiceImpl[IO]
    val parsing: JsonParserService[IO]          = new JsonParserServiceImpl[IO]

    val service: Service[IO] = new ServiceImpl[IO](persistence, parsing, validation)

    val errorHandler: ErrorHandler[IO] = new ErrorHandler[IO]
    val routes: Routes[IO]             = new RoutesImpl[IO](service, errorHandler)

    EmberServerBuilder
      .default[IO]
      .withPort(Port.fromInt(config.httpPort).get)
      .withHttpApp(
        Router("/" -> routes.routes)
          .mapF(_.getOrElse(routes.notFound))
      )
      .build
      .evalTap(_ => IO.println("Server ready"))
  }
}

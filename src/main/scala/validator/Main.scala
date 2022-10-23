package validator

import cats.effect.{IO, IOApp}
import doobie.Transactor
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import validator.api.{Routes, RoutesImpl}
import validator.persistence._
import validator.persistence.db.DBImpl
import validator.service._

object Main extends IOApp.Simple {
  override def run: IO[Unit] = {
    new DBImpl[IO].transactor.use { xa =>
      program(xa)
    }
  }

  def program(xa: Transactor[IO]): IO[Unit] = {
    val persistence: SchemaPersistence[IO]      = new SchemaPersistenceImpl[IO](xa)
    val validation: SchemaValidationService[IO] = new SchemaValidationServiceImpl[IO]
    val parsing: JsonParserService[IO]          = new JsonParserServiceImpl[IO]

    val service: Service[IO] = new ServiceImpl[IO](persistence, parsing, validation)

    val routes: Routes[IO] = new RoutesImpl[IO](service)

    EmberServerBuilder
      .default[IO]
      .withHttpApp(Router("/" -> routes.routes).orNotFound)
      .build
      .useForever
  }
}

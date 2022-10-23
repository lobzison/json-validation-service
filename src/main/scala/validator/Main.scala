package validator

import cats.effect.{IO, IOApp}
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import validator.api.RoutesImpl
import validator.persistence.SchemaPersistenceImpl
import validator.persistence.db.DBImpl
import validator.service.{JsonParserServiceImpl, SchemaValidationServiceImpl, ServiceImpl}

object Main extends IOApp.Simple {
  override def run: IO[Unit] = {
    val db = new DBImpl[IO]
    db.transactor.use { xa =>
      val persistence = new SchemaPersistenceImpl[IO](xa)
      val validation  = new SchemaValidationServiceImpl[IO]
      val parsing     = new JsonParserServiceImpl[IO]

      val service = new ServiceImpl[IO](persistence, parsing, validation)

      val routes = new RoutesImpl[IO](service)

      EmberServerBuilder
        .default[IO]
        .withHttpApp(Router("/" -> routes.routes).orNotFound)
        .build
        .useForever
    }
  }
}

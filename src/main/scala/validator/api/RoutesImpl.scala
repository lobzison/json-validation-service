package validator.api
import cats.effect.kernel.Concurrent
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import validator.model.{JsonSchemaRaw, SchemaId}
import validator.service.Service

class RoutesImpl[F[_]: Concurrent](service: Service[F]) extends Routes[F] with Http4sDsl[F] {

  override def routes: HttpRoutes[F] =
    HttpRoutes.of[F] { case r @ POST -> Root / "schema" / schemaIdStr =>
      val schemaId = SchemaId(schemaIdStr)
      for {
        schemaString <- r.as[String]
        schemaRaw = JsonSchemaRaw(schemaString)
        response <- service.createSchema(schemaId, schemaRaw)
        res      <- Created(response)
      } yield res
    }
}

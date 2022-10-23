package validator.api
import cats.effect.kernel.Concurrent
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import validator.model.{JsonRaw, JsonSchemaRaw, SchemaId}
import validator.service.Service
import org.http4s.circe._

class RoutesImpl[F[_]: Concurrent](service: Service[F]) extends Routes[F] with Http4sDsl[F] {

  // TODO: Add correct code matches on EndpointResponse
  // TODO: Add error handling or the error channel
  // TODO: Add custom 404

  override def routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case r @ POST -> Root / "schema" / schemaIdStr =>
        val schemaId = SchemaId(schemaIdStr)
        for {
          schemaString <- r.as[String]
          schemaRaw = JsonSchemaRaw(schemaString)
          response <- service.createSchema(schemaId, schemaRaw)
          res      <- Created(response)
        } yield res
      case GET -> Root / "schema" / schemaIdStr =>
        val schemaId = SchemaId(schemaIdStr)
        for {
          schema <- service.getJsonSchema(schemaId)
          res    <- Ok(schema.value)
        } yield res
      case r @ POST -> Root / "validate" / schemaIdStr =>
        val schemaId = SchemaId(schemaIdStr)
        for {
          jsonString <- r.as[String]
          jsonRaw = JsonRaw(jsonString)
          validationReport <- service.validateJsonAgainstSchema(jsonRaw, schemaId)
          res              <- Ok(validationReport)
        } yield res
    }
}

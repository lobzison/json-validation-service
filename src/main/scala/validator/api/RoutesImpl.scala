package validator.api
import cats.effect.kernel.Concurrent
import cats.implicits._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Response}
import validator.model.errors.ServiceError
import validator.model._
import validator.service.Service

class RoutesImpl[F[_]: Concurrent](service: Service[F], errorHandler: ErrorHandler[F])
    extends Routes[F]
    with Http4sDsl[F] {

  // TODO: Add correct code matches on EndpointResponse
  // TODO: Add error handling or the error channel
  // TODO: Add custom 404

  override def routes: HttpRoutes[F] =
    HttpRoutes.of[F] {
      case r @ POST -> Root / "schema" / schemaIdStr =>
        val schemaId = SchemaId(schemaIdStr)
        (for {
          schemaString <- r.as[String]
          schemaRaw = JsonSchemaRaw(schemaString)
          response <- service.createSchema(schemaId, schemaRaw)
          res      <- calculateResponseCode(response)
        } yield res)
          .handleErrorWith(errorHandler.handleErrors)
      case GET -> Root / "schema" / schemaIdStr =>
        val schemaId = SchemaId(schemaIdStr)
        (for {
          schema <- service.getJsonSchema(schemaId)
          res    <- Ok(schema.value)
        } yield res)
          .handleErrorWith(errorHandler.handleErrors)
      case r @ POST -> Root / "validate" / schemaIdStr =>
        val schemaId = SchemaId(schemaIdStr)
        (for {
          jsonString <- r.as[String]
          jsonRaw = JsonRaw(jsonString)
          validationReport <- service.validateJsonAgainstSchema(jsonRaw, schemaId)
          res              <- calculateResponseCode(validationReport)
        } yield res)
          .handleErrorWith(errorHandler.handleErrors)
    }

  private def calculateResponseCode(result: EndpointResponse): F[Response[F]] =
    (result.message, result.action) match {
      case (Some(e: ServiceError), _) =>
        Response[F](status = e.httpCode).withEntity(e).pure[F]
      case (None, ActionType.UploadSchema) =>
        Created(result)
      case (None, ActionType.ValidateDocument) =>
        Ok(result)
      case _ =>
        InternalServerError()
    }
}

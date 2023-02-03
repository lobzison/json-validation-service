package validator.api
import cats.effect.kernel.Concurrent
import cats.implicits._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpRoutes, Response}
import validator.model.errors.{NotFoundError, ServiceError}
import validator.model._
import validator.service.Service

class RoutesImpl[F[_]: Concurrent](service: Service[F], errorHandler: ErrorHandler[F])
    extends Routes[F]
    with Http4sDsl[F] {

  override def routes: HttpRoutes[F] =
    HttpRoutes.of[F] {

      case r @ POST -> Root / "schema" / schemaIdStr =>
        val schemaId = SchemaId(schemaIdStr)
        (for {
          schemaString <- r.as[String]
          schemaRaw = JsonSchemaRaw(schemaString)
          response <- service.createSchema(schemaId, schemaRaw)
          res      <- Created(response)
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
          res              <- Ok(validationReport)
        } yield res)
          .handleErrorWith(errorHandler.handleErrors)

      case _ => notFound.pure[F]
    }

  override val notFound: Response[F] =
    Response[F](status = NotFound).withEntity(NotFoundError(): ServiceError)
}

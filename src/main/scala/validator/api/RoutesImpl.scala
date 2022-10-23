package validator.api
import sttp.tapir.server.ServerEndpoint
import sttp.tapir._
import sttp.tapir.json.circe.jsonBody
import validator.model.{EndpointResponse, JsonSchemaRaw, SchemaId}
import sttp.tapir.generic.auto._
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import validator.model.errors.ServiceError
import validator.service.Service

class RoutesImpl[F[_]](service: Service[F]) extends Routes[F] {

  private val baseEndpoint = endpoint.errorOut(jsonBody[ServiceError])

  private val uploadSchema
      : Endpoint[Unit, (SchemaId, JsonSchemaRaw), ServiceError, EndpointResponse, Any] =
    baseEndpoint.post
      .in("schema" / path[SchemaId])
      .in(stringJsonBody.map(x => JsonSchemaRaw(x))(_.value))
      .out(jsonBody[EndpointResponse])
      .description("Create a new schema")

  private val uploadSchemaServerEndpoint =
    uploadSchema.serverLogicSuccess { case (id, schema) =>
      service.createSchema(id, schema)
    }

  private val apiEndpoints: List[ServerEndpoint[Any, F]] = List(uploadSchemaServerEndpoint)

  val docEndpoints: List[ServerEndpoint[Any, F]] =
    SwaggerInterpreter()
      .fromServerEndpoints[F](apiEndpoints, "JSON Validation Service", "1.0.0")

  override def routes: List[ServerEndpoint[Any, F]] =
    apiEndpoints ++ docEndpoints
}

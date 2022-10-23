package validator.model

import io.circe.Codec
import validator.model.errors.ServiceError

final case class EndpointResponse(
    action: ActionType,
    schemaId: SchemaId,
    status: Status,
    message: Option[ServiceError]
)

object EndpointResponse {

  implicit val endpointResponseCodec =
    Codec.forProduct4("action", "id", "status", "message")(EndpointResponse.apply) { er =>
      (er.action, er.schemaId, er.status, er.message)
    }
}

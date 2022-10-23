package validator.model

import io.circe.Encoder
import validator.model.errors.ServiceError

final case class EndpointResponse(
    action: ActionType,
    schemaId: SchemaId,
    status: Status,
    message: Option[ServiceError]
)

object EndpointResponse {
  implicit val enddpointResponseEncoder: Encoder[EndpointResponse] =
    Encoder.forProduct4(
      "action",
      "id",
      "status",
      "message"
    ) { er => (er.action, er.schemaId, er.status, er.message) }
}

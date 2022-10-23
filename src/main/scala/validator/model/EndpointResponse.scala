package validator.model

import io.circe.Encoder
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf
import validator.model.errors.ServiceError

final case class EndpointResponse(
    action: ActionType,
    schemaId: SchemaId,
    status: Status,
    message: Option[ServiceError]
)

object EndpointResponse {
  implicit val endpointResponseEncoder: Encoder[EndpointResponse] = {
    val encoder: Encoder[EndpointResponse] =
      Encoder
        .forProduct4("action", "id", "status", "message") { er =>
          (er.action, er.schemaId, er.status, er.message.map(_.description))
        }
    encoder.mapJson(_.deepDropNullValues)
  }

  implicit def endpointResponseEntityEncoder[F[_]]: EntityEncoder[F, EndpointResponse] =
    jsonEncoderOf[EndpointResponse]
}

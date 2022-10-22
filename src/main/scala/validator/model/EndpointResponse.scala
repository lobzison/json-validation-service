package validator.model

import validator.model.errors.ServiceError

final case class EndpointResponse(
    action: ActionType,
    schemaId: SchemaId, // TODO: rename to `id` in encoder
    status: Status,
    message: Option[ServiceError]
)

package validator.model

final case class EndpointResponse(
    action: ActionType,
    schemaId: SchemaId, // TODO: rename to `id` in encoder
    status: Status,
    message: ResponseMessage
)

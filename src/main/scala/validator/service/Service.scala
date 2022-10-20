package validator.service

import validator.model.{EndpointResponse, JsonSchema, JsonSchemaRaw, SchemaId}

trait Service[F[_]] {
  def createSchema(schemaId: SchemaId, schema: JsonSchemaRaw): F[EndpointResponse]
  def getJsonSchema(schemaId: SchemaId): F[JsonSchema]
  def validateJsonAgainstSchema(json: String, schemaId: SchemaId): F[EndpointResponse]
}

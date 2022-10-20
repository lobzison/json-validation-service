package validator.persistence

import validator.model.{JsonSchema, JsonSchemaRaw, SchemaId}

trait SchemaPersistence[F[_]] {
  def get(schemaId: SchemaId): F[Option[JsonSchemaRaw]]
  def create(schemaId: SchemaId, schemaBody: JsonSchema): F[Unit]
}

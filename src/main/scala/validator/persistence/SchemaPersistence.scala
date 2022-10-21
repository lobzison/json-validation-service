package validator.persistence

import validator.model.{JsonSchemaRaw, SchemaId}

trait SchemaPersistence[F[_]] {
  def get(schemaId: SchemaId): F[Option[JsonSchemaRaw]]
  def create(schemaId: SchemaId, schemaBody: JsonSchemaRaw): F[Unit]
}

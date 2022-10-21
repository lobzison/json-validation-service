package validator.persistence
import doobie.util.transactor.Transactor
import validator.model.{JsonSchemaRaw, SchemaId}

class SchemaPersistenceImpl[F[_]](xa: Transactor[F]) extends SchemaPersistence[F] {
  override def get(schemaId: SchemaId): F[Option[JsonSchemaRaw]] = ???

  override def create(schemaId: SchemaId, schemaBody: JsonSchemaRaw): F[Unit] = ???
}

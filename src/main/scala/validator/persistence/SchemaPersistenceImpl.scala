package validator.persistence
import cats.effect.MonadCancelThrow
import cats.implicits._
import doobie.free.connection
import doobie.implicits._
import doobie.util.transactor.Transactor
import validator.model.errors.SchemaAlreadyExists
import validator.model.{JsonSchemaRaw, SchemaId}

class SchemaPersistenceImpl[F[_]: MonadCancelThrow](xa: Transactor[F])
    extends SchemaPersistence[F] {

  override def get(schemaId: SchemaId): F[Option[JsonSchemaRaw]] =
    getQuery(schemaId).query[JsonSchemaRaw].option.transact(xa)

  override def create(schemaId: SchemaId, schemaBody: JsonSchemaRaw): F[Unit] = {
    (for {
      existing <- getQuery(schemaId).query[JsonSchemaRaw].option
      _ <- existing.fold(insertQuery(schemaId, schemaBody).update.run.void)(_ =>
        connection.raiseError[Unit](SchemaAlreadyExists())
      )
    } yield ()).transact(xa)
  }

  private def getQuery(schemaId: SchemaId) =
    fr"""
         select body from json_schema where schema_id = ${schemaId.value}
        """.stripMargin

  private def insertQuery(schemaId: SchemaId, schemaBody: JsonSchemaRaw) =
    fr"""
         insert into json_schema(schema_id, body)
         values (${schemaId.value}, ${schemaBody.value})
         """
}

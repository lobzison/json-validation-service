package validator.persistence
import cats.effect.MonadCancelThrow
import cats.implicits._
import doobie.implicits._
import doobie.util.transactor.Transactor
import org.sqlite.SQLiteException
import validator.model.errors.SchemaAlreadyExists
import validator.model.{JsonSchemaRaw, SchemaId}

class SchemaPersistenceImpl[F[_]: MonadCancelThrow](xa: Transactor[F])
    extends SchemaPersistence[F] {

  override def get(schemaId: SchemaId): F[Option[JsonSchemaRaw]] =
    getQuery(schemaId).query[JsonSchemaRaw].option.transact(xa)

  override def create(schemaId: SchemaId, schemaBody: JsonSchemaRaw): F[Unit] =
    insertQuery(schemaId, schemaBody).update.run
      .transact(xa)
      .void
      .adaptError {
        // Adapting SQLite unique constraint violation into domain error
        // https://www.sqlite.org/rescode.html#constraint_unique for the error code
        case e: SQLiteException if e.getResultCode.code === 2067 =>
          SchemaAlreadyExists()
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

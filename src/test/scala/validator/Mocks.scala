package validator

import cats.effect.IO
import cats.implicits._
import io.circe.Json
import validator.model.errors.{InvalidJson, SchemaAlreadyExists}
import validator.model.{JsonRaw, JsonSchema, JsonSchemaRaw, SchemaId, Status, ValidationReport}
import validator.persistence.SchemaPersistence
import validator.service.{JsonParserService, SchemaValidationService}

object Mocks {

  object Persistence {
    def successfulPersistence(getResult: Option[JsonSchemaRaw]): SchemaPersistence[IO] =
      new SchemaPersistence[IO] {
        override def get(schemaId: SchemaId): IO[Option[JsonSchemaRaw]] = getResult.pure[IO]
        override def create(schemaId: SchemaId, schemaBody: JsonSchemaRaw): IO[Unit] = IO.unit
      }

    val duplicatePersistence: SchemaPersistence[IO] =
      new SchemaPersistence[IO] {
        override def get(schemaId: SchemaId): IO[Option[JsonSchemaRaw]] = None.pure[IO]
        override def create(schemaId: SchemaId, schemaBody: JsonSchemaRaw): IO[Unit] =
          IO.raiseError(SchemaAlreadyExists())
      }
  }

  object Parsing {
    def successfulParsing(res: Json): JsonParserService[IO] = new JsonParserService[IO] {
      override def parseRawJson(raw: JsonRaw): IO[Json]               = res.pure[IO]
      override def parseRawSchema(raw: JsonSchemaRaw): IO[JsonSchema] = JsonSchema(res).pure[IO]
    }
    val failingParsing: JsonParserService[IO] = new JsonParserService[IO] {
      override def parseRawJson(raw: JsonRaw): IO[Json] =
        IO.raiseError(InvalidJson(new Throwable()))
      override def parseRawSchema(raw: JsonSchemaRaw): IO[JsonSchema] =
        IO.raiseError(InvalidJson(new Throwable()))
    }
  }

  object Validation {
    val default = ValidationReport(Status.Success, None)
    def successfulParsing(res: ValidationReport = default): SchemaValidationService[IO] =
      new SchemaValidationService[IO] {
        override def validateJsonAgainstSchema(
            json: Json,
            schema: JsonSchema
        ): IO[ValidationReport] =
          res.pure[IO]
      }
  }

}

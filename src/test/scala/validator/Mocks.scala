package validator

import cats.effect.IO
import cats.syntax.applicative._
import cats.syntax.applicativeError._
import io.circe.Json
import validator.model._
import validator.model.errors.{InvalidJson, SchemaAlreadyExists}
import validator.persistence.SchemaPersistence
import validator.service.{JsonParserService, SchemaValidationService, Service}

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
          SchemaAlreadyExists().raiseError[IO, Unit]
      }
  }

  object Parsing {
    def successfulParsing(res: Json): JsonParserService[IO] = new JsonParserService[IO] {
      override def parseRawJson(raw: JsonRaw): IO[Json]               = res.pure[IO]
      override def parseRawSchema(raw: JsonSchemaRaw): IO[JsonSchema] = JsonSchema(res).pure[IO]
    }
    val failingParsing: JsonParserService[IO] = new JsonParserService[IO] {
      override def parseRawJson(raw: JsonRaw): IO[Json] =
        InvalidJson(new Throwable()).raiseError[IO, Json]
      override def parseRawSchema(raw: JsonSchemaRaw): IO[JsonSchema] =
        InvalidJson(new Throwable()).raiseError[IO, JsonSchema]
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

  object Service {
    def successful(
        create: EndpointResponse,
        validate: EndpointResponse,
        schema: JsonSchema
    ): Service[IO] =
      new Service[IO] {
        override def createSchema(schemaId: SchemaId, schema: JsonSchemaRaw): IO[EndpointResponse] =
          create.pure[IO]

        override def getJsonSchema(schemaId: SchemaId): IO[JsonSchema] = schema.pure[IO]

        override def validateJsonAgainstSchema(
            json: JsonRaw,
            schemaId: SchemaId
        ): IO[EndpointResponse] = validate.pure[IO]
      }

    def failing(err: Throwable): Service[IO] = new Service[IO] {
      override def createSchema(schemaId: SchemaId, schema: JsonSchemaRaw): IO[EndpointResponse] =
        IO.raiseError(err)

      override def getJsonSchema(schemaId: SchemaId): IO[JsonSchema] =
        IO.raiseError(err)

      override def validateJsonAgainstSchema(
          json: JsonRaw,
          schemaId: SchemaId
      ): IO[EndpointResponse] =
        IO.raiseError(err)
    }
  }

}

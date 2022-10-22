package validator.service
import cats.MonadThrow
import cats.implicits._
import validator.model.errors.{SchemaNotFound, ServiceError, ValidationDidntPass}
import validator.model._
import validator.persistence.SchemaPersistence

class ServiceImpl[F[_]: MonadThrow](
    persistence: SchemaPersistence[F],
    parsing: JsonParserService[F],
    validation: SchemaValidationService[F]
) extends Service[F] {
  override def createSchema(schemaId: SchemaId, schema: JsonSchemaRaw): F[EndpointResponse] = {
    val actionType = ActionType.UploadSchema
    val result = for {
      // we parse the schema to validate that it's a valid json
      _ <- parsing.parseRawSchema(schema)
      _ <- persistence.create(schemaId, schema)
    } yield EndpointResponse(actionType, schemaId, Status.Success, None)

    handleErrors(actionType, schemaId, result)
  }

  override def getJsonSchema(schemaId: SchemaId): F[JsonSchema] =
    for {
      maybeSchema <- persistence.get(schemaId)
      schemaRaw   <- MonadThrow[F].fromOption(maybeSchema, SchemaNotFound())
      schema      <- parsing.parseRawSchema(schemaRaw)
    } yield schema

  override def validateJsonAgainstSchema(
      jsonRaw: JsonRaw,
      schemaId: SchemaId
  ): F[EndpointResponse] = {
    val actionType = ActionType.ValidateDocument
    val result =
      for {
        json   <- parsing.parseRawJson(jsonRaw)
        schema <- getJsonSchema(schemaId)
        report <- validation.validateJsonAgainstSchema(json, schema)
      } yield translateValidationReportToEndpointResponse(actionType, schemaId, report)

    handleErrors(actionType, schemaId, result)
  }

  private def translateValidationReportToEndpointResponse(
      actionType: ActionType,
      schemaId: SchemaId,
      report: ValidationReport
  ): EndpointResponse =
    EndpointResponse(
      actionType,
      schemaId,
      report.status,
      report.message.map(ValidationDidntPass.apply)
    )

  private def handleErrors(
      actionType: ActionType,
      schemaId: SchemaId,
      result: F[EndpointResponse]
  ): F[EndpointResponse] =
    result
      .recover { case e: ServiceError =>
        EndpointResponse(actionType, schemaId, Status.Error, Some(e))
      }

}

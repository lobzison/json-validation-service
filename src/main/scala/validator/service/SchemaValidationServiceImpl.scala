package validator.service
import io.circe.Json
import validator.model.{JsonSchema, ValidationReport}

class SchemaValidationServiceImpl[F[_]] extends SchemaValidationService[F] {
  override def validateJsonAgainstSchema(json: Json, schema: JsonSchema): F[ValidationReport] =
    ???
}

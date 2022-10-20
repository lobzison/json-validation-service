package validator.service

import io.circe.Json
import validator.model.{JsonSchema, ValidationReport}

trait SchemaValidationService[F[_]] {
  def validateJsonAgainstSchema(json: Json, schema: JsonSchema): F[ValidationReport]
}

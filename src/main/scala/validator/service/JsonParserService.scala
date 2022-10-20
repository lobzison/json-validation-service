package validator.service

import io.circe.Json
import validator.model.{JsonRaw, JsonSchema, JsonSchemaRaw}

trait JsonParserService[F[_]] {
  def parseRaw(raw: JsonRaw): Json
  def parseRaw(raw: JsonSchemaRaw): JsonSchema
}

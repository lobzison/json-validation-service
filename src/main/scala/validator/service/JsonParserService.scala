package validator.service

import io.circe.Json
import validator.model.{JsonRaw, JsonSchema, JsonSchemaRaw}

trait JsonParserService[F[_]] {
  def parseRawJson(raw: JsonRaw): F[Json]
  def parseRawSchema(raw: JsonSchemaRaw): F[JsonSchema]
}

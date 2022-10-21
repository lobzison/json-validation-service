package validator.service
import cats.ApplicativeThrow
import io.circe.Json
import io.circe.parser.parse
import validator.model.{JsonRaw, JsonSchema, JsonSchemaRaw}
import cats.implicits._
import validator.model.errors.InvalidJson

class JsonParserServiceImpl[F[_]: ApplicativeThrow] extends JsonParserService[F] {
  override def parseRawJson(raw: JsonRaw): F[Json] =
    parseJson(raw.value)

  override def parseRawSchema(raw: JsonSchemaRaw): F[JsonSchema] =
    parseJson(raw.value).map(JsonSchema.apply)

  private def parseJson(jsonRaw: String): F[Json] =
    ApplicativeThrow[F]
      .fromEither(
        parse(jsonRaw).leftMap(InvalidJson.apply)
      )
}

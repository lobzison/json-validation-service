package validator

import cats.effect.IO
import io.circe.Json
import validator.model._
import io.circe.parser.parse

import scala.io.Source

object Fixtures {

  case class NotValidJsonInTest(value: String)
      extends Throwable(s"$value is not a valid json, please fix the test data ☠️")

  val testSchemaId1: SchemaId                = SchemaId("test123")
  val testSchemaId2: SchemaId                = SchemaId("test444")
  val testSchemaRaw1: JsonSchemaRaw          = JsonSchemaRaw("""{"test": "ok"}""")
  val testSchemaRaw2: JsonSchemaRaw          = JsonSchemaRaw("""{"status": "👌👌👌"}""")
  val testBrokenJsonSchemaRaw: JsonSchemaRaw = JsonSchemaRaw("""{"broken":}""")
  val testBrokenJsonRaw: JsonRaw             = JsonRaw(testBrokenJsonSchemaRaw.value)
  val testJsonRaw1: JsonRaw                  = JsonRaw("""{"www": "ccc"}""")

  val testJson1: Json = parse(testJsonRaw1.value)
    .getOrElse(throw NotValidJsonInTest("testJsonRaw1"))

  val testSchema1: JsonSchema = JsonSchema(
    parse(testSchemaRaw1.value)
      .getOrElse(throw NotValidJsonInTest("testSchemaRaw1"))
  )
  val testSchema2: JsonSchema = JsonSchema(
    parse(testSchemaRaw2.value)
      .getOrElse(throw NotValidJsonInTest("testSchemaRaw2"))
  )

  val configJsonSchema: IO[JsonSchema] =
    getJsonFromResource("config-schema").map(JsonSchema.apply)
  val correctConfigJson: IO[Json] =
    getJsonFromResource("correct-config")
  val incorrectConfigJson: IO[Json] =
    getJsonFromResource("incorrect-config")

  private def getJsonFromResource(name: String): IO[Json] = {
    for {
      raw <- IO(Source.fromResource(s"$name.json").getLines().mkString("\n"))
      res <- IO.fromEither(parse(raw))
    } yield res
  }
}

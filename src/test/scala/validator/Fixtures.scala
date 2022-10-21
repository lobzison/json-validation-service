package validator

import validator.model._
import io.circe.parser.parse

object Fixtures {
  val testSchemaId1: SchemaId       = SchemaId("test123")
  val testSchemaId2: SchemaId       = SchemaId("test444")
  val testSchemaRaw1: JsonSchemaRaw = JsonSchemaRaw("""{"asd": "dsa"}""")
  val testSchemaRaw2: JsonSchemaRaw = JsonSchemaRaw("""{"ddd": "👌👌👌"}""")
  val testSchema1: JsonSchema = JsonSchema(
    parse(testSchemaRaw1.value)
      .getOrElse(throw new Throwable("testSchemaRaw1 is not a valid json"))
  )
  val testSchema2: JsonSchema = JsonSchema(
    parse(testSchemaRaw2.value)
      .getOrElse(throw new Throwable("testSchemaRaw2 is not a valid json"))
  )

}

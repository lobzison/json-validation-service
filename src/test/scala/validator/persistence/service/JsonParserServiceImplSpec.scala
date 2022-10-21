package validator.persistence.service

import cats.effect.IO
import munit.CatsEffectSuite
import validator.Fixtures._
import validator.model.errors.InvalidJson
import validator.service.JsonParserServiceImpl

class JsonParserServiceImplSpec extends CatsEffectSuite {
  val service = new JsonParserServiceImpl[IO]

  test("parseRawJson must parse a valid json") {
    val res = service.parseRawJson(testJsonRaw1)
    assertIO(res, testJson1)
  }

  test("parseRawJson must raise InvalidJson exception on invalid json") {
    val res = service.parseRawJson(testBrokenJsonRaw)
    interceptIO[InvalidJson](res)
  }

  test("parseRawSchema must parse a valid json") {
    val res = service.parseRawSchema(testSchemaRaw1)
    assertIO(res, testSchema1)
  }

  test("parseRawSchema must raise InvalidJson exception on invalid json") {
    val res = service.parseRawSchema(testBrokenJsonSchemaRaw)
    interceptIO[InvalidJson](res)
  }
}

package validator.persistence.service

import cats.effect.IO
import munit.CatsEffectSuite
import validator.service.SchemaValidationServiceImpl
import validator.Fixtures._
import validator.model.Status

class SchemaValidationServiceImplSpec extends CatsEffectSuite {

  val service = new SchemaValidationServiceImpl[IO]()
  test(
    "validateJsonAgainstSchema should return success " +
      "on json that validates against the schema"
  ) {
    val result = for {
      schema        <- configJsonSchema
      correctConfig <- correctConfigJson
      res           <- service.validateJsonAgainstSchema(correctConfig, schema)
    } yield res.status
    assertIO(result, Status.Success)
  }

  test(
    "validateJsonAgainstSchema should return error " +
      "on json that does not validate against the schema"
  ) {
    val result = for {
      schema          <- configJsonSchema
      incorrectConfig <- incorrectConfigJson
      res             <- service.validateJsonAgainstSchema(incorrectConfig, schema)
    } yield res.status
    assertIO(result, Status.Error)
  }
}

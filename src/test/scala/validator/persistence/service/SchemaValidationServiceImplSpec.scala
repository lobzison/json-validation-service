package validator.persistence.service

import cats.effect.IO
import munit.CatsEffectSuite
import validator.service.SchemaValidationServiceImpl
import validator.Fixtures._
import validator.model.{Status, ValidationReport}

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
    } yield res
    assertIO(result, ValidationReport(Status.Success, None))
  }

  test(
    "validateJsonAgainstSchema should return error " +
      "on json that does not validate against the schema"
  ) {
    for {
      schema          <- configJsonSchema
      incorrectConfig <- incorrectConfigJson
      res             <- service.validateJsonAgainstSchema(incorrectConfig, schema)
      _ = assertEquals(res.status, Status.Error)
      // error message should mention that source property is missing
      _ = assert(res.message.forall(_.value.contains("source")))
    } yield ()
  }
}

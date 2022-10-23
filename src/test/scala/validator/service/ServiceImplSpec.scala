package validator.service

import cats.effect.IO
import munit.CatsEffectSuite
import validator.Fixtures._
import validator.Mocks
import validator.model.{ActionType, JsonSchema, Status, ValidationMessage, ValidationReport}
import validator.model.errors.{
  InvalidJson,
  SchemaAlreadyExists,
  SchemaNotFound,
  ValidationDidntPass
}

class ServiceImplSpec extends CatsEffectSuite {
  test(
    "createSchema must return Reply marked as Error " +
      "with InvalidJson message if schema parsing fails"
  ) {
    val service = new ServiceImpl[IO](
      Mocks.Persistence.successfulPersistence(getResult = None),
      Mocks.Parsing.failingParsing,
      Mocks.Validation.successfulParsing()
    )

    interceptIO[InvalidJson](service.createSchema(testSchemaId1, testSchemaRaw1))
  }

  test(
    "createSchema must return Reply marked as Error " +
      "with SchemaAlreadyExists message if schema already exists"
  ) {
    val service = new ServiceImpl[IO](
      Mocks.Persistence.duplicatePersistence,
      Mocks.Parsing.successfulParsing(testJson1),
      Mocks.Validation.successfulParsing()
    )

    interceptIO[SchemaAlreadyExists](service.createSchema(testSchemaId1, testSchemaRaw1))
  }

  test(
    "createSchema must return Reply marked as Error " +
      "with SchemaAlreadyExists message if schema already exists"
  ) {
    val service = new ServiceImpl[IO](
      Mocks.Persistence.successfulPersistence(getResult = None),
      Mocks.Parsing.successfulParsing(testJson1),
      Mocks.Validation.successfulParsing()
    )

    for {
      res <- service.createSchema(testSchemaId1, testSchemaRaw1)
      _ = assert(res.status == Status.Success)
      _ = assert(res.message.isEmpty)
      _ = assert(res.action == ActionType.UploadSchema)
    } yield ()
  }

  test(
    "getJsonSchema must fail, if schema does not exist"
  ) {
    val service = new ServiceImpl[IO](
      Mocks.Persistence.successfulPersistence(getResult = None),
      Mocks.Parsing.successfulParsing(testJson1),
      Mocks.Validation.successfulParsing()
    )
    interceptIO[SchemaNotFound](service.getJsonSchema(testSchemaId1))
  }

  test(
    "getJsonSchema must fail, if schema exists, but it's not valid"
  ) {
    val service = new ServiceImpl[IO](
      Mocks.Persistence.successfulPersistence(getResult = Some(testBrokenJsonSchemaRaw)),
      Mocks.Parsing.failingParsing,
      Mocks.Validation.successfulParsing()
    )
    interceptIO[InvalidJson](service.getJsonSchema(testSchemaId1))
  }

  test(
    "getJsonSchema must return schema"
  ) {
    val service = new ServiceImpl[IO](
      Mocks.Persistence.successfulPersistence(getResult = Some(testBrokenJsonSchemaRaw)),
      Mocks.Parsing.successfulParsing(testJson1),
      Mocks.Validation.successfulParsing()
    )
    assertIO(service.getJsonSchema(testSchemaId1), JsonSchema(testJson1))
  }

  test(
    "validateJsonAgainstSchema must return Reply marked as Error " +
      "with SchemaNotFound message if schema does not exist"
  ) {
    val service = new ServiceImpl[IO](
      Mocks.Persistence.successfulPersistence(getResult = None),
      Mocks.Parsing.successfulParsing(testJson1),
      Mocks.Validation.successfulParsing()
    )
    interceptIO[SchemaNotFound](service.validateJsonAgainstSchema(testJsonRaw1, testSchemaId1))
  }

  test(
    "validateJsonAgainstSchema must return Reply marked as Error " +
      "with InvalidJson message if schema is not valid"
  ) {
    val service = new ServiceImpl[IO](
      Mocks.Persistence.successfulPersistence(getResult = Some(testSchemaRaw1)),
      Mocks.Parsing.failingParsing,
      Mocks.Validation.successfulParsing()
    )
    interceptIO[InvalidJson](service.validateJsonAgainstSchema(testJsonRaw1, testSchemaId1))
  }

  test(
    "validateJsonAgainstSchema must return Reply marked as Error " +
      "with ValidationDidntPass json does not pass the validation"
  ) {
    val service = new ServiceImpl[IO](
      Mocks.Persistence.successfulPersistence(getResult = Some(testSchemaRaw1)),
      Mocks.Parsing.successfulParsing(testJson1),
      Mocks.Validation.successfulParsing(
        ValidationReport(Status.Error, Some(ValidationMessage("bad json 🫠")))
      )
    )
    for {
      res <- service.validateJsonAgainstSchema(testJsonRaw1, testSchemaId1)
      _ = assert(res.status == Status.Error)
      _ = assert(res.message match {
        case Some(_: ValidationDidntPass) => true
        case _                            => false
      })
      _ = assert(res.action == ActionType.ValidateDocument)
    } yield ()
  }

}

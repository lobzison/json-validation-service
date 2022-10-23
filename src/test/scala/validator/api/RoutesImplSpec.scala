package validator.api

import cats.effect.IO
import io.circe.syntax.EncoderOps
import munit.CatsEffectSuite
import org.http4s.Method._
import org.http4s.Request
import org.http4s.Status.{BadRequest, Conflict, Created, InternalServerError, NotFound, Ok}
import org.http4s.implicits.http4sLiteralsSyntax
import validator.Mocks
import validator.model._
import validator.Fixtures._
import validator.model.errors.{
  InvalidJson,
  SchemaAlreadyExists,
  SchemaNotFound,
  ServiceError,
  UnexpectedError,
  ValidationDidntPass
}

class RoutesImplSpec extends CatsEffectSuite {

  case class MissingResponse() extends Throwable

  val baseUrl      = uri"http://localhost"
  val errorHandler = new ErrorHandler[IO]

  test("POST /schema/:id must return 201 and body on success") {
    val expectedResponse =
      EndpointResponse(ActionType.UploadSchema, testSchemaId1, Status.Success, None)
    val routes =
      new RoutesImpl[IO](
        Mocks.Service.successful(expectedResponse, expectedResponse, testSchema1),
        errorHandler
      )

    val request = Request[IO](method = POST, uri = baseUrl / "schema" / testSchemaId1.value)

    for {
      res <- routes.routes.run(request).value
      _        = assert(res.isDefined)
      response = res.getOrElse(throw MissingResponse())
      _        = assertEquals(response.status, Created)
      _        = assertIO(response.as[String], expectedResponse.asJson.noSpaces)
    } yield ()
  }

  test("POST /schema/:id must return 409 and body on schema already exists") {
    val expectedResponse =
      EndpointResponse(
        ActionType.UploadSchema,
        testSchemaId1,
        Status.Error,
        Some(SchemaAlreadyExists())
      )
    val routes =
      new RoutesImpl[IO](Mocks.Service.failing(SchemaAlreadyExists()), errorHandler)

    val request = Request[IO](method = POST, uri = baseUrl / "schema" / testSchemaId1.value)

    for {
      res <- routes.routes.run(request).value
      _        = assert(res.isDefined)
      response = res.getOrElse(throw MissingResponse())
      _        = assertEquals(response.status, Conflict)
      _        = assertIO(response.as[String], expectedResponse.asJson.noSpaces)
    } yield ()
  }

  test("POST /schema/:id must return 500 and body in case something unexpected happened") {
    val expectedResponse: ServiceError =
      UnexpectedError()
    val error = new Throwable("DB gone 🔥🔥🔥")
    val routes =
      new RoutesImpl[IO](Mocks.Service.failingHard(error), errorHandler)

    val request = Request[IO](method = POST, uri = baseUrl / "schema" / testSchemaId1.value)

    for {
      res <- routes.routes.run(request).value
      _        = assert(res.isDefined)
      response = res.getOrElse(throw MissingResponse())
      _        = assertEquals(response.status, InternalServerError)
      _        = assertIO(response.as[String], expectedResponse.asJson.noSpaces)
    } yield ()
  }

  test("GET /schema/:id must return 200 and body on success") {
    val dummy =
      EndpointResponse(ActionType.UploadSchema, testSchemaId1, Status.Success, None)
    val routes =
      new RoutesImpl[IO](Mocks.Service.successful(dummy, dummy, testSchema1), errorHandler)

    val request = Request[IO](method = GET, uri = baseUrl / "schema" / testSchemaId1.value)

    for {
      res <- routes.routes.run(request).value
      _        = assert(res.isDefined)
      response = res.getOrElse(throw MissingResponse())
      _        = assertEquals(response.status, Ok)
      _        = assertIO(response.as[String], testSchema1.value.noSpaces)
    } yield ()
  }

  test("GET /schema/:id must return 404 and a message when schema is not found") {
    val expectedResponse: ServiceError = SchemaNotFound()
    val routes =
      new RoutesImpl[IO](Mocks.Service.failing(SchemaNotFound()), errorHandler)

    val request = Request[IO](method = GET, uri = baseUrl / "schema" / testSchemaId1.value)

    for {
      res <- routes.routes.run(request).value
      _        = assert(res.isDefined)
      response = res.getOrElse(throw MissingResponse())
      _        = assertEquals(response.status, NotFound)
      _        = assertIO(response.as[String], expectedResponse.asJson.noSpaces)
    } yield ()
  }

  test("GET /schema/:id must return 400 and a message when schema is not valid") {
    val expectedResponse: ServiceError = InvalidJson(new Throwable())
    val routes =
      new RoutesImpl[IO](Mocks.Service.failing(expectedResponse), errorHandler)

    val request = Request[IO](method = GET, uri = baseUrl / "schema" / testSchemaId1.value)

    for {
      res <- routes.routes.run(request).value
      _        = assert(res.isDefined)
      response = res.getOrElse(throw MissingResponse())
      _        = assertEquals(response.status, BadRequest)
      _        = assertIO(response.as[String], expectedResponse.asJson.noSpaces)
    } yield ()
  }

  test("POST /validate/:id must return 200 and a message when json validates") {
    val expectedResponse =
      EndpointResponse(ActionType.ValidateDocument, testSchemaId1, Status.Success, None)
    val routes =
      new RoutesImpl[IO](
        Mocks.Service.successful(expectedResponse, expectedResponse, testSchema1),
        errorHandler
      )

    val request = Request[IO](method = POST, uri = baseUrl / "validate" / testSchemaId1.value)

    for {
      res <- routes.routes.run(request).value
      _        = assert(res.isDefined)
      response = res.getOrElse(throw MissingResponse())
      _        = assertEquals(response.status, Ok)
      _        = assertIO(response.as[String], expectedResponse.asJson.noSpaces)
    } yield ()
  }

  test("POST /validate/:id must return 200 and a message when json does not validate") {
    val expectedResponse =
      EndpointResponse(
        ActionType.ValidateDocument,
        testSchemaId1,
        Status.Error,
        Some(ValidationDidntPass(ValidationMessage("")))
      )
    val routes =
      new RoutesImpl[IO](
        Mocks.Service.successful(expectedResponse, expectedResponse, testSchema1),
        errorHandler
      )

    val request = Request[IO](method = POST, uri = baseUrl / "validate" / testSchemaId1.value)

    for {
      res <- routes.routes.run(request).value
      _        = assert(res.isDefined)
      response = res.getOrElse(throw MissingResponse())
      _        = assertEquals(response.status, Ok)
      _        = assertIO(response.as[String], expectedResponse.asJson.noSpaces)
    } yield ()
  }

  test("POST /validate/:id must return 404 and a message when schema does not exist") {
    val expectedResponse =
      EndpointResponse(
        ActionType.ValidateDocument,
        testSchemaId1,
        Status.Error,
        Some(SchemaNotFound())
      )
    val routes =
      new RoutesImpl[IO](
        Mocks.Service.successful(expectedResponse, expectedResponse, testSchema1),
        errorHandler
      )

    val request = Request[IO](method = POST, uri = baseUrl / "validate" / testSchemaId1.value)

    for {
      res <- routes.routes.run(request).value
      _        = assert(res.isDefined)
      response = res.getOrElse(throw MissingResponse())
      _        = assertEquals(response.status, NotFound)
      _        = assertIO(response.as[String], expectedResponse.asJson.noSpaces)
    } yield ()
  }

  test("POST /validate/:id must return 400 and a message if json passed is invalid") {
    val expectedResponse =
      EndpointResponse(
        ActionType.ValidateDocument,
        testSchemaId1,
        Status.Error,
        Some(InvalidJson(new Throwable()))
      )
    val routes =
      new RoutesImpl[IO](
        Mocks.Service.successful(expectedResponse, expectedResponse, testSchema1),
        errorHandler
      )

    val request = Request[IO](method = POST, uri = baseUrl / "validate" / testSchemaId1.value)

    for {
      res <- routes.routes.run(request).value
      _        = assert(res.isDefined)
      response = res.getOrElse(throw MissingResponse())
      _        = assertEquals(response.status, BadRequest)
      _        = assertIO(response.as[String], expectedResponse.asJson.noSpaces)
    } yield ()
  }

  test("Routes that does not match should return a valid json") {
    val expectedResponse: ServiceError = errors.NotFoundError()

    val routes =
      new RoutesImpl[IO](Mocks.Service.failing(expectedResponse), errorHandler)

    val request = Request[IO](method = POST, uri = baseUrl / "asdasd" / testSchemaId1.value)

    for {
      res <- routes.routes.run(request).value
      _        = assert(res.isDefined)
      response = res.getOrElse(throw MissingResponse())
      _        = assertEquals(response.status, NotFound)
      _        = assertIO(response.as[String], expectedResponse.asJson.noSpaces)
    } yield ()
  }
}

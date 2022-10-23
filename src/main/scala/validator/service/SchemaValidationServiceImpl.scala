package validator.service
import cats.Applicative
import cats.implicits.catsSyntaxApplicativeId
import com.github.fge.jsonschema.core.report.ProcessingReport
import com.github.fge.jsonschema.main.{JsonSchemaFactory, JsonSchema => LibraryJsonSchema}
import io.circe.Json
import io.circe.jackson.circeToJackson
import validator.model.{Status, ValidationMessage, ValidationReport, JsonSchema => LocalJsonSchema}

import scala.jdk.CollectionConverters._

class SchemaValidationServiceImpl[F[_]: Applicative] extends SchemaValidationService[F] {
  override def validateJsonAgainstSchema(
      json: Json,
      schema: LocalJsonSchema
  ): F[ValidationReport] = {
    val validator = buildValidator(schema)
    val result    = validator.validate(circeToJackson(json.deepDropNullValues))
    convertReport(result).pure[F]
  }

  private def buildValidator(schema: LocalJsonSchema): LibraryJsonSchema =
    JsonSchemaFactory.byDefault.getJsonSchema(circeToJackson(schema.value))

  private def convertReport(report: ProcessingReport): ValidationReport = {
    val status = Status.fromBoolean(isSuccess = report.isSuccess)

    val message =
      status match {
        case Status.Success => None
        case Status.Error =>
          Some(
            ValidationMessage(
              report
                .iterator()
                .asScala
                .map(_.getMessage)
                .mkString("\n")
            )
          )
      }
    ValidationReport(status, message)
  }

}

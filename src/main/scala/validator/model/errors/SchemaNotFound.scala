package validator.model.errors
import org.http4s.{Status => HttpStatus}

final case class SchemaNotFound() extends ServiceError {
  override val description: String  = "Schema with given id does not exist"
  override val httpCode: HttpStatus = HttpStatus.NotFound
}

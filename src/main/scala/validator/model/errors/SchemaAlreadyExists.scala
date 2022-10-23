package validator.model.errors
import org.http4s.{Status => HttpStatus}

final case class SchemaAlreadyExists() extends ServiceError {
  override val description: String  = "Schema with given id already exists"
  override val httpCode: HttpStatus = HttpStatus.Conflict
}

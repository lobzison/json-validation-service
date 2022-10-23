package validator.model.errors
import org.http4s.{Status => HttpStatus}

final case class InvalidJson(error: Throwable) extends ServiceError {
  override val description: String = s"Invalid JSON: ${error.getMessage}"
  override val httpCode: HttpStatus    = HttpStatus.BadRequest
}

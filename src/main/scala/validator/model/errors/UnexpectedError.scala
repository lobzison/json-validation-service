package validator.model.errors
import org.http4s.{Status => HttpStatus}

final case class UnexpectedError() extends ServiceError {
  override val description: String  = "Unexpected server error"
  override val httpCode: HttpStatus = HttpStatus.InternalServerError
}

package validator.model.errors
import org.http4s.{Status => HttpStatus}

final case class NotFound() extends ServiceError {
  override val description: String = "Route not found"
  override val httpCode: HttpStatus    = HttpStatus.NotFound
}

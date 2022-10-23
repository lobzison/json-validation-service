package validator.model.errors

import org.http4s.{Status => HttpStatus}
import validator.model.ValidationMessage

final case class ValidationDidntPass(message: ValidationMessage) extends ServiceError {
  override val description: String  = message.value
  override val httpCode: HttpStatus = HttpStatus.Ok
}

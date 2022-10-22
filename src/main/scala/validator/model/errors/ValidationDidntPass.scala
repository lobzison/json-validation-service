package validator.model.errors

import validator.model.ValidationMessage

final case class ValidationDidntPass(message: ValidationMessage) extends ServiceError {
  override val description: String = message.value
}

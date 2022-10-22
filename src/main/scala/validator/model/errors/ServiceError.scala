package validator.model.errors

trait ServiceError extends Throwable {
  val description: String
}

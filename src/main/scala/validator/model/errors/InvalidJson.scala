package validator.model.errors

final case class InvalidJson(error: Throwable) extends ServiceError {
  override val description: String = s"Invalid JSON: $error"
}

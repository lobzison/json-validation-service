package validator.model.errors

final case class InvalidJson(error: Throwable) extends Error {
  override val description: String = s"Invalid JSON: $error"
}

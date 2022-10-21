package validator.model.errors

final case class UnexpectedError() extends Error {
  override val description: String = "Unexpected server error"
}

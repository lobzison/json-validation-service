package validator.model.errors

object UnexpectedError extends Error {
  override val description: String = "Unexpected server error"
}

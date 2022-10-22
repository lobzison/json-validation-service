package validator.model.errors

final case class UnexpectedError() extends ServiceError {
  override val description: String = "Unexpected server error"
}

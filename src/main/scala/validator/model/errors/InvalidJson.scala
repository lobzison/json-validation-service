package validator.model.errors

final case class InvalidJson() extends Error {
  override val description: String = "Invalid JSON"
}

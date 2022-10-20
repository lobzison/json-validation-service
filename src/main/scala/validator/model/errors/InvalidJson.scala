package validator.model.errors

case object InvalidJson extends Error {
  override val description: String = "Invalid JSON"
}

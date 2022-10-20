package validator.model.errors

case object SchemaNotFound extends Error {
  override val description: String = "Schema with given id does not exist"
}

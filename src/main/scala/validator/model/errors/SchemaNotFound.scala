package validator.model.errors

final case class SchemaNotFound() extends Error {
  override val description: String = "Schema with given id does not exist"
}

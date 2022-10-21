package validator.model.errors

final case class SchemaAlreadyExists() extends Error {
  override val description: String = "Schema with given id already exists"
}

package validator.model.errors

object SchemaAlreadyExists extends Error {
  override val description: String = "Schema with given id already exists"
}

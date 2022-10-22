package validator.model.errors

final case class SchemaNotFound() extends ServiceError {
  override val description: String = "Schema with given id does not exist"
}

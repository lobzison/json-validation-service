package validator.model

sealed trait ActionType {
  case object UploadSchema extends ActionType

  case object ValidateDocument extends ActionType
}

package validator.model

sealed trait ActionType

object ActionType {
  case object UploadSchema extends ActionType

  case object ValidateDocument extends ActionType
}

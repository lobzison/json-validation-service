package validator.model

import io.circe.Encoder

sealed trait ActionType

object ActionType {
  case object UploadSchema extends ActionType

  case object ValidateDocument extends ActionType

  implicit val actionTypeEncoder: Encoder[ActionType] =
    Encoder.encodeString.contramap {
      case UploadSchema     => "uploadSchema"
      case ValidateDocument => "validateDocument"
    }
}

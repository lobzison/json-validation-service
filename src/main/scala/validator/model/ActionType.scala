package validator.model

import enumeratum.EnumEntry.LowerCamelcase
import enumeratum._

sealed trait ActionType extends EnumEntry with LowerCamelcase

object ActionType extends Enum[ActionType] with CirceEnum[ActionType] {

  val values = findValues

  case object UploadSchema extends ActionType

  case object ValidateDocument extends ActionType

}

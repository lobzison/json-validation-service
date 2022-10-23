package validator.model

import enumeratum.EnumEntry.LowerCamelcase
import enumeratum.{CirceEnum, Enum, EnumEntry}

sealed trait Status extends EnumEntry

object Status extends Enum[Status] with CirceEnum[Status] with LowerCamelcase {

  val values = findValues

  final case object Success extends Status
  final case object Error   extends Status

  def fromBoolean(isSuccess: Boolean): Status =
    if (isSuccess) Success else Error
}

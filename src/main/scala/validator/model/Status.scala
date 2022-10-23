package validator.model

import io.circe.Encoder

sealed trait Status

object Status {
  final case object Success extends Status
  final case object Error   extends Status

  def fromBoolean(isSuccess: Boolean): Status =
    if (isSuccess) Success else Error

  implicit val statusEncoder: Encoder[Status] =
    Encoder.encodeString.contramap {
      case Success => "success"
      case Error   => "error"
    }
}

package validator.model

sealed trait Status

object Status {
  final case object Success extends Status
  final case object Error   extends Status
}

package validator.model

final case class ValidationReport(status: Status, message: Option[ValidationMessage])

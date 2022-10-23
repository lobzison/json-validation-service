package validator.model.errors

import io.circe.Encoder

trait ServiceError extends Throwable {
  val description: String
}

object ServiceError {
  implicit val serviceSerrorEncoder: Encoder[ServiceError] =
    Encoder.forProduct1("message")(_.description)
}

package validator.model.errors

import io.circe.Codec

trait ServiceError extends Throwable {
  val description: String
}

object ServiceError {
  implicit val serviceErrorCodec: Codec[ServiceError] =
    Codec.forProduct1[ServiceError, String]("message")(str =>
      // This is not really correct, but we don't use the decoder for service error, so it's fine for now
      new ServiceError {
        override val description: String = str
      }
    )(_.description)
}

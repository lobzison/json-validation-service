package validator.model.errors

import io.circe.Encoder
import org.http4s.circe.jsonEncoderOf
import org.http4s.{EntityEncoder, Status}

trait ServiceError extends Throwable {
  val description: String
  val httpCode: Status
}

object ServiceError {
  implicit val serviceErrorEncoder: Encoder[ServiceError] =
    Encoder.forProduct1("message")(_.description)

  implicit def serviceErrorEntityEncoder[F[_]]: EntityEncoder[F, ServiceError] =
    jsonEncoderOf[ServiceError]
}

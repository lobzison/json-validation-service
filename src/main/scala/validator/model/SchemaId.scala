package validator.model

import io.circe.Encoder

final case class SchemaId(value: String) extends AnyVal

object SchemaId {
  implicit val schemaIdEncoder: Encoder[SchemaId] =
    Encoder.encodeString.contramap(_.value)
}

package validator.model

import io.circe.{Decoder, Encoder}

final case class SchemaId(value: String) extends AnyVal

object SchemaId {
  implicit val schemaIdEncoder: Encoder[SchemaId] =
    Encoder.encodeString.contramap(_.value)

  implicit val schemaIdDecoder: Decoder[SchemaId] =
    Decoder.decodeString.map(SchemaId.apply)
}

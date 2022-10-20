package validator.model

import io.circe.Json

final case class JsonSchema(value: Json) extends AnyVal

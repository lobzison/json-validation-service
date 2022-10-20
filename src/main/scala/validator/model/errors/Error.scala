package validator.model.errors

trait Error extends Throwable {
  val description: String
}

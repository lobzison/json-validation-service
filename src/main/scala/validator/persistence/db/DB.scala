package validator.persistence.db

import cats.effect.Resource
import doobie.Transactor

trait DB[F[_]] {
  val transactor: Resource[F, Transactor[F]]
}

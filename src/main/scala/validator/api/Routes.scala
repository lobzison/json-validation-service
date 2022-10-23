package validator.api

import org.http4s.{HttpRoutes, Response}

trait Routes[F[_]] {
  def routes: HttpRoutes[F]
  val notFound: Response[F]
}

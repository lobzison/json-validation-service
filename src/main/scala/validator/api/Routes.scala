package validator.api

import sttp.tapir.server.ServerEndpoint

trait Routes[F[_]] {
  def routes: List[ServerEndpoint[Any, F]]
}

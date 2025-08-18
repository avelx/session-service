package avel.session.service.routes

import avel.session.service.Counter
import avel.session.service.session.SessionService
import cats.effect.kernel.Sync
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router


final case class SessionRoutes[F[_]: Sync ](
                                           session: SessionService[F],
                                             state: Counter[F]
                                         ) extends Http4sDsl[F] {

  private[routes] val prefixPath = "/session"

  private def httpRoutes(state: Counter[F]): HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "inc" =>
      val s = state.inc
      Ok(s)
    }

  def routes(): HttpRoutes[F] = Router(
    prefixPath -> httpRoutes(state)
  )

}
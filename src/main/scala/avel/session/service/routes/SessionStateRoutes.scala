package avel.session.service.routes

import avel.session.service.models.SessionStateCounter
import cats.effect.kernel.Sync
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router


final case class SessionStateRoutes[F[_]: Sync ](
                                              state: SessionStateCounter[F]
                                         ) extends Http4sDsl[F] {

  private[routes] val prefixPath = "/session"

  private def httpRoutes(state: SessionStateCounter[F]): HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "inc" =>
      val none = state.inc
      Ok(none)

    case GET -> Root  =>
      val st = state.get
      Ok(st)

    }

  def routes(): HttpRoutes[F] = Router(
    prefixPath -> httpRoutes(state)
  )

}
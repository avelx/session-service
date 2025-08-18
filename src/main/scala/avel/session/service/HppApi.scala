package avel.session.service

import avel.session.service.models.SessionStateCounter
import avel.session.service.routes.SessionStateRoutes
import cats.effect.Sync
import org.http4s.server.Router
import org.http4s.{HttpApp, HttpRoutes}

object HttpApi {
  def make[F[_]: Sync](
                         state: SessionStateCounter[F]
                       ): HttpApi[F] =
    new HttpApi[F](state) {}
}

sealed abstract class HttpApi[F[_]: Sync] private (
                                                     state: SessionStateCounter[F]
                                                   ) {

  private val sessionRoute    = SessionStateRoutes[F](state).routes()

  private val openRoutes: HttpRoutes[F] = sessionRoute

  private val routes: HttpRoutes[F] = Router(
    "v1"            -> openRoutes,
  )

  val httpApp: HttpApp[F] = routes.orNotFound
}
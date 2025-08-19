package avel.session.service

import avel.session.service.routes.SessionStateRoutes
import avel.session.service.services.SessionStateCounterService
import cats.effect.Sync
import org.http4s.server.Router
import org.http4s.{HttpApp, HttpRoutes}

object HttpApi {
  def make[F[_]: Sync](
                         state: SessionStateCounterService[F]
                       ): HttpApi[F] =
    new HttpApi[F](state) {}
}

sealed abstract class HttpApi[F[_]: Sync] private (
                                                     state: SessionStateCounterService[F]
                                                   ) {

  private val sessionRoute    = SessionStateRoutes[F](state).routes()

  private val openRoutes: HttpRoutes[F] = sessionRoute

  private val routes: HttpRoutes[F] = Router(
    "v1"            -> openRoutes,
  )

  val httpApp: HttpApp[F] = routes.orNotFound
}
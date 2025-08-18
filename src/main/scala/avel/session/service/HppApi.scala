package avel.session.service

import avel.session.service.routes.SessionRoutes
import avel.session.service.server.Services
import cats.effect.Sync
import org.http4s.server.Router
import org.http4s.{HttpApp, HttpRoutes}

object HttpApi {
  def make[F[_]: Sync](
                         services: Services[F],
                         state: F[Counter[F]]
                       ): HttpApi[F] =
    new HttpApi[F](services, state) {}
}

sealed abstract class HttpApi[F[_]: Sync] private (
                                                     services: Services[F],
                                                     state: F[Counter[F]]
                                                   ) {

  private val sessionRoute    = SessionRoutes[F](services.session, state).routes()

  // Combining all the http routes
  private val openRoutes: HttpRoutes[F] =
    sessionRoute

  private val routes: HttpRoutes[F] = Router(
    "v1"            -> openRoutes,
  )

  // TODO: find out what this for
//  private val middleware: HttpRoutes[F] => HttpRoutes[F] = {
//    { http: HttpRoutes[F] =>
//      AutoSlash(http)
//    }
//  }

  val httpApp: HttpApp[F] = routes.orNotFound
}
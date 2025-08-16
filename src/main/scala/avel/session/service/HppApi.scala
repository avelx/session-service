package avel.session.service

import avel.session.service.routes.SessionRoutes
import cats.effect.Sync
import org.http4s.server.Router
import org.http4s.server.middleware.{AutoSlash}
import org.http4s.{HttpApp, HttpRoutes}


object HttpApi {
  def make[F[_]: Sync](
                         services: Services[F],
                       ): HttpApi[F] =
    new HttpApi[F](services) {}
}

sealed abstract class HttpApi[F[_]: Sync] private (
                                                     services: Services[F]
                                                   ) {

  private val sessionRoute    = SessionRoutes[F](services.session).routes

  // Combining all the http routes
  private val openRoutes: HttpRoutes[F] =
    sessionRoute

  private val routes: HttpRoutes[F] = Router(
    "v1"            -> openRoutes,
  )

  // TODO: find out what this for
  private val middleware: HttpRoutes[F] => HttpRoutes[F] = {
    { http: HttpRoutes[F] =>
      AutoSlash(http)
    }
  }

  val httpApp: HttpApp[F] = routes.orNotFound
}
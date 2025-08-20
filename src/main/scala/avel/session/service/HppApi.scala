package avel.session.service

import avel.session.service.routes.{QueueuRoutes, SessionStateRoutes}
import avel.session.service.services.{QueueService, SessionStateCounterService}
import cats.effect.Sync
import cats.implicits.toSemigroupKOps
import org.http4s.server.Router
import org.http4s.{HttpApp, HttpRoutes}

object HttpApi {
  def make[F[_] : Sync](
                         state: SessionStateCounterService[F],
                         queue: QueueService[F]
                       ): HttpApi[F] =
    new HttpApi[F](state, queue) {}
}

sealed abstract class HttpApi[F[_] : Sync] private(
                                                    state: SessionStateCounterService[F],
                                                    queue: QueueService[F]
                                                  ) {

  private val sessionRoute = SessionStateRoutes[F](state).routes()
  private val queueRoute = QueueuRoutes[F](queue).routes()

  private val openRoutes: HttpRoutes[F] = sessionRoute <+> queueRoute

  private val routes: HttpRoutes[F] = Router(
    "v1" -> openRoutes,
  )

  val httpApp: HttpApp[F] = routes.orNotFound
}
package avel.session.service.server

import avel.session.service.routes.{CounterStateRoutes, ErrorRoutes, QueueuRoutes}
import avel.session.service.services.{CounterService, QueueService}
import cats.effect.Sync
import cats.implicits.toSemigroupKOps
import org.http4s.server.Router
import org.http4s.{HttpApp, HttpRoutes}
import org.typelevel.log4cats.Logger

object HttpApi {
  def make[F[_] : Sync: Logger](
                                 state: CounterService[F],
                                 queue: QueueService[F]
                       ): HttpApi[F] =
    new HttpApi[F](state, queue) {}
}

sealed abstract class HttpApi[F[_] : Sync: Logger] private(
                                                            state: CounterService[F],
                                                            queue: QueueService[F]
                                                  ) {

  private val sessionRoute = CounterStateRoutes[F](state).routes()
  private val queueRoute = QueueuRoutes[F](queue).routes()
  private val errorRoute = ErrorRoutes[F]().routes()

  private val openRoutes: HttpRoutes[F] =
    sessionRoute <+> queueRoute <+> errorRoute

  private val routes: HttpRoutes[F] = Router(
    "v1" -> openRoutes,
  )

  val httpApp: HttpApp[F] = routes.orNotFound
}
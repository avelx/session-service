package avel.session.service.server

import avel.session.service.routes.{CounterStateRoutes, ErrorRoutes, QueueuRoutes, SessionRoutes}
import avel.session.service.services.{CounterService, QueueService, SessionService}
import cats.effect.kernel.Async
import cats.implicits.toSemigroupKOps
import org.http4s.server.Router
import org.http4s.{HttpApp, HttpRoutes}
import org.typelevel.log4cats.Logger

object HttpApi {
  def make[F[_] : Async: Logger](
                                 counterService: CounterService[F],
                                 queue: QueueService[F],
                                 sessionService: SessionService[F]
                       ): HttpApi[F] =
    new HttpApi[F](counterService, queue, sessionService) {}
}

sealed abstract class HttpApi[F[_] : Async: Logger] private(
                                                            state: CounterService[F],
                                                            queue: QueueService[F],
                                                            sessionService: SessionService[F]
                                                  ) {

  private val sessionRoute = SessionRoutes[F](sessionService).routes()
  private val counterRoute = CounterStateRoutes[F](state).routes()
  private val queueRoute = QueueuRoutes[F](queue).routes()
  private val errorRoute = ErrorRoutes[F]().routes()

  private val openRoutes: HttpRoutes[F] =
    counterRoute <+> queueRoute <+> errorRoute <+> sessionRoute

  private val routes: HttpRoutes[F] = Router(
    "v1" -> openRoutes,
  )

  val httpApp: HttpApp[F] = routes.orNotFound
}
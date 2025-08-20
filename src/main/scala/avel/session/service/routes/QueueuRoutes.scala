package avel.session.service.routes

import avel.session.service.services.{QueueService}
import cats.effect.kernel.Sync
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router


final case class QueueuRoutes[F[_]: Sync ](
                                            queue: QueueService[F]
                                         ) extends Http4sDsl[F] {

  private[routes] val prefixPath = "/queue"

  private def httpRoutes(queue: QueueService[F]): HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "push" / item =>
      val none = queue.push(item)
      Ok(none)

    case GET -> Root / "pull" =>
      val st = queue.pull
      Ok(st)

    }

  def routes(): HttpRoutes[F] = Router(
    prefixPath -> httpRoutes(queue)
  )

}
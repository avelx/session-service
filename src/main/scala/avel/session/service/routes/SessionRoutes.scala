package avel.session.service.routes

import avel.session.service.session.SessionService
import cats.{Monad}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router


final case class SessionRoutes[F[_]: Monad](
                                           session: SessionService[F]
                                         ) extends Http4sDsl[F] {

  private[routes] val prefixPath = "/session"

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root => {
      val state = session.getState
      Ok(state)
    }
  }

  val routes: HttpRoutes[F] = Router(
    prefixPath -> httpRoutes
  )

}
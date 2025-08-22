package avel.session.service.routes

import avel.session.service.models.UserSessionData
import avel.session.service.services.SessionService
import cats.effect.kernel.{Async, Sync}
import cats.implicits.{catsSyntaxApplyOps, catsSyntaxFlatten}
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.typelevel.log4cats.Logger


final case class SessionRoutes[F[_]: Async : Logger](
                                              sessionService: SessionService[F]
                                            ) extends Http4sDsl[F] {

    private[routes] val prefixPath = "/session"


    private def httpRoutes(sessionService: SessionService[F]): HttpRoutes[F] = HttpRoutes.of[F] {

      case GET -> Root / "create" / sessionId =>
        val res = sessionService
          .create( UserSessionData(sessionId = sessionId,
            tag = "SomeData",
            ticks = 50L))
        Ok(res)

      case GET -> Root / "get" / sessionId =>
        Logger[F].info(s"Get a session: ${sessionId}") *>
          Sync[F].delay {
            val s = sessionService.getById(sessionId)
            Ok(s)
          }.flatten
    }

    def routes(): HttpRoutes[F] = Router(
      prefixPath -> httpRoutes(sessionService)
    )

  }
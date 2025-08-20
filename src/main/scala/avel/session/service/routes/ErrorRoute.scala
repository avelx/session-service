package avel.session.service.routes

import cats.effect.kernel.Sync
import cats.implicits.{catsSyntaxApplicativeError, catsSyntaxApplyOps}
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router
import org.typelevel.log4cats.Logger


final case class ErrorRoutes[F[_] :Sync: Logger]() extends Http4sDsl[F] {

  private[routes] val prefixPath = "/error"

  val errorRoute: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "suppression" =>
      Logger[F].error("Exception raised ...") *>
        Sync[F].delay(throw new Exception("Hey don't swallow me") )
          .attempt *>
      Ok("Ok")
  }

  def routes(): HttpRoutes[F] = Router(
    prefixPath -> errorRoute
  )

}
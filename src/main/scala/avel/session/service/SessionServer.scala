package avel.session.service

import avel.session.service.SessionService.SessionState
import cats.effect.Async
import com.comcast.ip4s._
import fs2.io.net.Network
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger

object SessionServer {

  val state: Map[String, SessionState] = Map("56575-45456" -> SessionState(454))

  def run[F[_]: Async: Network]: F[Nothing] = {
    for {
      _ <- EmberClientBuilder.default[F].build // TODO: drop this client later on
      //helloWorldAlg = HelloWorld.impl[F]
      sessionService = SessionService.impl[F](state)
      //jokeAlg = Jokes.impl[F](client)

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract segments not checked
      // in the underlying routes.
      httpApp = (
        //SessionserviceRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
        SessionServiceRoutes.sessionServiceRoutes[F](sessionService)
//          <+> SessionServiceRoutes.jokeRoutes[F](jokeAlg)
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      _ <- 
        EmberServerBuilder.default[F]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build
    } yield ()
  }.useForever
}

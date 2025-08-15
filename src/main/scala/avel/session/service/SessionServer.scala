package avel.session.service

import avel.session.service.SessionService.SessionState
import cats.effect.kernel.Ref
import com.comcast.ip4s._
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.middleware.Logger
import cats.effect.{IO}

object SessionServer {

  val state: IO[Ref[IO, Map[String, SessionState]]] = Ref[IO].of(Map("56575-45456" -> SessionState(454)))

  def run: IO[Nothing] = {
    for {
      _ <- EmberClientBuilder.default[IO].build // TODO: drop this client later on
      //helloWorldAlg = HelloWorld.impl[F]
      sessionService = SessionService.impl(state)
      //jokeAlg = Jokes.impl[F](client)

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract segments not checked
      // in the underlying routes.
      httpApp = (
        //SessionserviceRoutes.helloWorldRoutes[F](helloWorldAlg) <+>
        SessionServiceRoutes.sessionServiceRoutes(sessionService)
//          <+> SessionServiceRoutes.jokeRoutes[F](jokeAlg)
      ).orNotFound

      // With Middlewares in place
      finalHttpApp = Logger.httpApp(true, true)(httpApp)

      _ <- 
        EmberServerBuilder.default[IO]
          .withHost(ipv4"0.0.0.0")
          .withPort(port"8080")
          .withHttpApp(finalHttpApp)
          .build
    } yield ()
  }.useForever
}

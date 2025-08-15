package avel.session.service

import avel.session.service.SessionService._
import cats.effect.{IO, Sync}
import cats.implicits._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl

object SessionServiceRoutes {

  def jokeRoutes[F[_]: Sync](J: Jokes[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "joke" =>
        for {
          joke <- J.get
          resp <- Ok(joke)
        } yield resp
    }
  }

  def helloWorldRoutes[F[_]: Sync](H: HelloWorld[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "hello" / name =>
        for {
          greeting <- H.hello(HelloWorld.Name(name))
          resp <- Ok(greeting)
        } yield resp
    }
  }

  def sessionServiceRoutes(service: SessionService): HttpRoutes[IO] = {
    val dsl = new Http4sDsl[IO]{}
    import dsl._

    HttpRoutes.of[IO] {
      case GET -> Root / "session" / sessionId =>
        for {
          sessionState <- service.getState( Session(sessionId))
          resp <- Ok(sessionState)
        } yield resp
    }

  }
}
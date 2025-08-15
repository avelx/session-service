package avel.session.service

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple {
  val run = SessionServer.run[IO]
}

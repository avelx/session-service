package avel.session.service

import cats.effect.{IOApp}

object Main extends IOApp.Simple {
  val run = SessionServer.run
}

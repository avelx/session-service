package avel.session.service

import avel.session.service.services.SessionService
import cats.effect.IO

import scala.concurrent.duration.FiniteDuration

trait BackgroundFiber {

  import scala.concurrent.duration.DurationInt

  private val refreshFrequency: FiniteDuration = 10.second

  def backgroundProcess(sessionService: SessionService[IO]): IO[Unit] = {
    for {
      _ <- IO.sleep(refreshFrequency)
      _ <- IO.cede
      _ <- sessionService.cleanUp
      _ <- IO.cede
      _ <- backgroundProcess(sessionService)
    } yield ()
  }

}

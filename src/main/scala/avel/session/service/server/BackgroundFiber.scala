package avel.session.service

import avel.session.service.services.SessionService
import cats.effect.IO

trait BackgroundFiber {

  import scala.concurrent.duration.DurationInt

  def backgroundProcess(sessionService: SessionService[IO]): IO[Unit] = {
    for {
      _ <- IO.sleep(10.second)
      _ <- IO.cede
      _ <- sessionService.cleanUp
      _ <- IO.cede
      _ <- backgroundProcess(sessionService)
    } yield ()
  }

}

package avel.session.service

import avel.session.service.models.SessionStateCounterImpl
import avel.session.service.services.MkHttpServer
import cats.effect.{IO, IOApp}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp.Simple {
  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = {
      for {
        stateCounter <- SessionStateCounterImpl.make[IO]
        api = HttpApi.make[IO](stateCounter)
        httpServer = MkHttpServer[IO].newEmber(api.httpApp)
        runner <- httpServer.useForever
      } yield runner
  }

}

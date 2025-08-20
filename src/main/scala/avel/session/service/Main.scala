package avel.session.service

import avel.session.service.config.Config
import avel.session.service.services.SessionStateCounterService
import cats.effect.{IO, IOApp}
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}
import org.typelevel.log4cats.slf4j.Slf4jLogger


object Main extends IOApp.Simple {

  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = {
    Config.load[IO].flatMap { config =>
      Logger[IO].info(s"Loaded config $config") >>
      SessionStateCounterService.impl[IO].flatMap { sessionService =>
        val api = HttpApi.make[IO](sessionService)
        val httpServer = MkHttpServer[IO].newEmber(api.httpApp, config.get)
        httpServer.useForever
      }
    }
  }

}

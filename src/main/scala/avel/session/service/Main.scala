package avel.session.service

import avel.session.service.config.Config
import avel.session.service.resources.AppResources
import avel.session.service.services.{QueueService, SessionStateCounterService}
import cats.effect.{IO, IOApp}
import dev.profunktor.redis4cats.effect.Log.NoOp.instance
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}

object Main extends IOApp.Simple {

  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = {
    Config.load[IO].flatMap { config =>
      Logger[IO].info(s"Loaded config $config") >>
        AppResources.make[IO](config).use { _ =>
          SessionStateCounterService.impl[IO].flatMap { sessionService =>
            QueueService.impl[IO].flatMap { queueService =>
              val api = HttpApi.make[IO](sessionService, queueService)
              val httpServer = MkHttpServer[IO].newEmber(api.httpApp, config)
              httpServer.useForever
            }
          }
        }
    }
  }

}

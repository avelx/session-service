package avel.session.service

import avel.session.service.config.Config
import avel.session.service.resources.AppResources
import avel.session.service.server.{HttpApi, MkHttpServer}
import avel.session.service.services.{CounterService, QueueService, SessionService}
import cats.effect.{IO, IOApp}
import dev.profunktor.redis4cats.effect.Log.NoOp.instance
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.{Logger, SelfAwareStructuredLogger}

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext



object Main extends IOApp.Simple {

  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  import scala.concurrent.duration.DurationInt

  private def backgroundProcess(sessionService: SessionService[IO]): IO[Unit] = {
    for {
      _ <- IO.sleep(10.second)
      _ <- IO.cede
      _ <- sessionService.cleanUp
      _ <- IO.cede
      _ <- backgroundProcess((sessionService))
    } yield ()
  }

  override def run: IO[Unit] = {
    Config.load[IO].flatMap { config =>
      Logger[IO].info(s"Loaded config $config") >>
        AppResources.make[IO](config).use { _ =>
          CounterService.impl[IO].flatMap { counterService =>
            SessionService.impl[IO].flatMap { sessionService =>
              QueueService.impl[IO].flatMap { queueService =>
                val api = HttpApi.make[IO](counterService, queueService, sessionService)
                val httpServer = MkHttpServer[IO].newEmber(api.httpApp, config)
                // Parallel eval for two effects
                  IO.racePair(backgroundProcess (sessionService),
                      httpServer.use(_ => IO.never)).void
              }
            }
          }
        }
    }
  }

}

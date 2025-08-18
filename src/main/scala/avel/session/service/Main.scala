package avel.session.service

import avel.session.service.services.{MkHttpServer, Services}
import cats.effect.{IO, IOApp}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp.Simple {
  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = {
      for {
        counter <- CounterImpl.make[IO]
        api = HttpApi.make[IO](Services.make[IO](), counter)
        httpServer = MkHttpServer[IO].newEmber(api.httpApp)
        runner <- httpServer.useForever
      } yield runner
  }

}

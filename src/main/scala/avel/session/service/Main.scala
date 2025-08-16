package avel.session.service

import avel.session.service.server.{MkHttpServer, Services}
import cats.effect.{IO, IOApp}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp.Simple {
  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = {
      val api = HttpApi.make[IO](Services.make[IO])
      val httpServer = MkHttpServer[IO].newEmber(api.httpApp)
      httpServer
        .useForever
  }

}

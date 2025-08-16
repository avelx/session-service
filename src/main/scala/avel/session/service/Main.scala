package avel.session.service

import avel.session.service.server.MkHttpServer
import cats.effect.unsafe.implicits.global
import cats.effect.{IO, IOApp}
import cats.implicits.catsSyntaxApplicativeId
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp.Simple {
  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = {
    for {
      services <- Services.make[IO].pure[IO]
      api <- HttpApi.make[IO](services).pure[IO]
      httpServer = MkHttpServer[IO].newEmber(api.httpApp)
    } yield {
      httpServer
        .useForever
        .unsafeRunSync()
    }
  }

}

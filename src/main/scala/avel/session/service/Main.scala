package avel.session.service

import avel.session.service.models.SessionState
import avel.session.service.server.{MkHttpServer, Services}
import cats.effect.kernel.Ref
import cats.effect.{IO, IOApp}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object Main extends IOApp.Simple {
  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  override def run: IO[Unit] = {
      val state = Ref[IO].of(SessionState(0))
      val api = HttpApi.make[IO](Services.make[IO](state))
      val httpServer = MkHttpServer[IO].newEmber(api.httpApp)
      httpServer
        .useForever
  }

}

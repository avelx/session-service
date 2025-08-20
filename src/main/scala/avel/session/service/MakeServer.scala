package avel.session.service

import avel.session.service.config.ServiceConfig
import cats.effect.kernel.{Async, Resource}
import com.comcast.ip4s.{Host, Port}
import fs2.io.net.Network
import org.http4s.HttpApp
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Server
import org.http4s.server.defaults.Banner
import org.typelevel.log4cats.Logger

trait MkHttpServer[F[_]] {
  def newEmber(httpApp: HttpApp[F], config:ServiceConfig): Resource[F, Server]
}

object MkHttpServer {
  def apply[F[_]: MkHttpServer]: MkHttpServer[F] = implicitly

  private def showEmberBanner[F[_]: Logger](s: Server): F[Unit] =
    Logger[F].info(s"\n${Banner.mkString("\n")}\nHTTP Server started at ${s.address}")

  implicit def forAsyncLogger[F[_]: Async: Logger: Network]: MkHttpServer[F] =
    new MkHttpServer[F] {
      def newEmber(httpApp: HttpApp[F], config: ServiceConfig): Resource[F, Server] =
        EmberServerBuilder
          .default[F]
          .withHost(Host.fromString(config.host).get)
          .withPort(Port.fromInt(config.port.number).get)
          .withHttpApp(httpApp)
          .build
          .evalTap(showEmberBanner[F])
    }
}

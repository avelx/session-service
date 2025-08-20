package avel.session.service.resources

import avel.session.service.config.ServiceConfig
import cats.effect.{Concurrent, Resource}
import cats.implicits.{toFlatMapOps, toFoldableOps}
import dev.profunktor.redis4cats.effect.MkRedis
import dev.profunktor.redis4cats.{Redis, RedisCommands}
import org.typelevel.log4cats.Logger

abstract class AppResources[F[_]](
                                   val redis: RedisCommands[F, String, String]
                               )

// More or less copy from the G.Volpe code: shopping-cart
object AppResources {

  def make[F[_]: Concurrent: Logger: MkRedis](cfg: ServiceConfig): Resource[F, AppResources[F]] = {

    def checkRedisConnection(
                              redis: RedisCommands[F, String, String]
                            ): F[Unit] =
      redis.info.flatMap {
        _.get("redis_version").traverse_ { v =>
          Logger[F].info(s"Connected to Redis $v")
        }
      }

    def mkRedisResource(cfg: ServiceConfig): Resource[F, RedisCommands[F, String, String]] =
      Redis[F].utf8(cfg.redis)
        .evalTap(checkRedisConnection)

    (
      mkRedisResource(cfg)
    ).map(new AppResources[F](_) {})

  }

}
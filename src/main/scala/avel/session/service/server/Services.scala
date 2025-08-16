package avel.session.service.server

import avel.session.service.session.{SessionService, SessionServiceImpl}
import cats.effect.kernel.Sync
import org.typelevel.log4cats.Logger

object Services {
  def make[F[_]: Sync: Logger](): Services[F] = {
    new Services[F]( // TODO: extend here by adding more services
      session = SessionServiceImpl.make[F],
    ) {

    }
  }
}

sealed abstract class Services[F[_]] private(
                                              val session: SessionService[F]
                                            )
package avel.session.service

import avel.session.service.session.{SessionService, SessionServiceImpl}
import cats.effect.kernel.{Sync}

object Services {
  def make[F[_]: Sync]: Services[F] = {
    new Services[F](
      session = SessionServiceImpl.make[F],
    ) {

    }
  }
}

sealed abstract class Services[F[_]] private(
                                              val session: SessionService[F]
                                            )
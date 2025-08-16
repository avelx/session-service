package avel.session.service.server

import avel.session.service.models.SessionState
import avel.session.service.session.{SessionService, SessionServiceImpl}
import cats.effect.kernel.{Ref, Sync}
import org.typelevel.log4cats.Logger

object Services {
  def make[F[_]: Sync: Logger](state : F[Ref[F, SessionState]]): Services[F] = {
    new Services[F]( // TODO: extend here by adding more services
      session = SessionServiceImpl.make[F](state),
    ) {

    }
  }
}

sealed abstract class Services[F[_]] private(
                                              val session: SessionService[F]
                                            )
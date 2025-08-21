package avel.session.service.services

import avel.session.service.models.UserSessionData
import cats.effect.kernel.Async
import cats.effect.std.MapRef
import cats.implicits.toFunctorOps



trait SessionService[F[_]] {
  def create(session: UserSessionData) : F[Unit]
  def getById(sessionId: String): F[Option[UserSessionData]]
}

object SessionService {
  private val shardCound : Int = 5
  private val ttlCounter : Long = 1000

  def impl[F[_] : Async]: F[SessionService[F]] = {

      MapRef.ofShardedImmutableMap[F, String, UserSessionData](shardCound).map { mapRef =>
        new SessionService[F] {

          override def create(session: UserSessionData): F[Unit] = {
//            val backgroundOneSecondTick: F[Unit] =
//              mapRef(session.sessionId).get.map(_.get.ticks > 0).ifM(
//                Temporal[F].sleep(1.second) >>
//                  mapRef(session.sessionId).update { ss =>
//                    ss.map(s => s.copy(ticks = s.ticks - 1))
//                  },
//                mapRef(session.sessionId).update(_ => None)
//              )

            mapRef(session.sessionId).update(_ => Some(session.copy(ticks = ttlCounter)))
            //backgroundOneSecondTick
          }

          override def getById(sessionId: String): F[Option[UserSessionData]] = {
            mapRef(sessionId).get
          }

        }
      }

  }


}
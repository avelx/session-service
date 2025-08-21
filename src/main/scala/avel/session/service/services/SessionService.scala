package avel.session.service.services

import avel.session.service.models.UserSessionData
import cats.effect.kernel.Async
import cats.effect.std.MapRef
import cats.implicits.{catsSyntaxApplyOps, toFunctorOps}


trait SessionService[F[_]] {
  def create(session: UserSessionData) : F[Unit]
  def getById(sessionId: String): F[Option[UserSessionData]]
}

object SessionService {
  private val shardCound : Int = 5

  def impl[F[_] : Async]: F[SessionService[F]] = {
      MapRef.ofShardedImmutableMap[F, String, UserSessionData](shardCound).map { mapRef =>
        new SessionService[F] {

          override def create(session: UserSessionData): F[Unit] = {
            mapRef(session.sessionId).update(_ => Some(session))
          }

          override def getById(sessionId: String): F[Option[UserSessionData]] = {
            mapRef(sessionId).update(ss => ss.map(_.tick).flatten) *>
            mapRef(sessionId).get
          }

        }
      }
  }

}
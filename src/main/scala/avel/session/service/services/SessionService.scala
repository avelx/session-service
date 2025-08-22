package avel.session.service.services

import avel.session.service.models.UserSessionData
import cats.effect.kernel.{Async, Temporal}
import cats.effect.std.MapRef
import cats.implicits.{catsSyntaxApplyOps, toFunctorOps}
import org.typelevel.log4cats.Logger

import scala.concurrent.duration.DurationInt


trait SessionService[F[_]] {
  def create(session: UserSessionData) : F[Unit]
  def getById(sessionId: String): F[Option[UserSessionData]]
  def cleanUp: F[Unit]
}

object SessionService {
  private val shardCound : Int = 5

  def impl[F[_] : Async: Logger]: F[SessionService[F]] = {
      MapRef.ofShardedImmutableMap[F, String, UserSessionData](shardCound).map { mapRef =>
        new SessionService[F] {

          override def create(session: UserSessionData): F[Unit] = {
            mapRef(session.sessionId).update(_ => Some(session))
          }

          override def getById(sessionId: String): F[Option[UserSessionData]] = {
            mapRef(sessionId).update(ss => ss.map(_.tick).flatten) *>
            mapRef(sessionId).get
          }

          // Clean up not expired sessions
          /*
            TODO: store sessionId's in kind of the Queue and use these values for expiry checks
           */
          override def cleanUp: F[Unit] = {
            Temporal[F].delay(10.second) *>
              Logger[F].info("Ticks")
          }
        }
      }
  }

}
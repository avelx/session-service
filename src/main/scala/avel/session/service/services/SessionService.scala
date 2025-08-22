package avel.session.service.services

import avel.session.service.models.UserSessionData
import cats.effect.kernel.{Async, Sync, Temporal}
import cats.effect.std.{MapRef, Queue}
import cats.implicits.{catsSyntaxApplicativeId, catsSyntaxApplyOps, catsSyntaxFlatMapOps, catsSyntaxIfM, toFlatMapOps, toFunctorOps}
import org.typelevel.log4cats.Logger

import scala.concurrent.duration.DurationInt


trait SessionService[F[_]] {
  def create(session: UserSessionData): F[Unit]

  def getById(sessionId: String): F[Option[UserSessionData]]

  def cleanUp: F[Unit]

  def total: F[Int]
}

/*
    SessionService build upon two concurrency primitives
    bounded Queue and MapRef with fixed number of shards
    cleanUp function need to be triggered externally to clean up expired sessions
    otherwise session would expire on Get request
    also service provided number of currently live sessions
    This is alpha version of this service, more changes expected.
 */
object SessionService {
  private val shardCound: Int = 5
  private val queueSize: Int = 100

  def impl[F[_] : Async : Logger]: F[SessionService[F]] = {
    Queue.bounded[F, String](queueSize).map { queue =>
      MapRef.ofShardedImmutableMap[F, String, UserSessionData](shardCound).map { mapRef =>
        new SessionService[F] {

          override def create(session: UserSessionData): F[Unit] = {
          // Protective logic around session creation
            getById(session.sessionId).map(_.isDefined).ifM(
              Logger[F].info(s"Session exists: ${session.sessionId}"),
              queue.offer(session.sessionId) *>
                mapRef(session.sessionId).update(_ => Some(session)) *>
                Logger[F].info(s"Session created: ${session.sessionId}")
            )

          }

          override def getById(sessionId: String): F[Option[UserSessionData]] = {
            mapRef(sessionId).updateAndGet(ss =>
              ss.map(_.tick).flatten
            ) *> mapRef(sessionId).get
          }

          // Clean up expired sessions
          override def cleanUp: F[Unit] = {
            Temporal[F].delay(10.second) *>
              Sync[F].delay(
                queue.tryTake.flatMap { sessionId =>
                  (sessionId.pure[F]).map(_.isDefined).ifM(
                    getById(sessionId.get).map(_.isEmpty).ifM(
                      Logger[F].info(s"Expired: $sessionId"),
                      Logger[F].info(s"Still alive: $sessionId") *>
                        queue.tryOffer(sessionId.getOrElse("")).void
                    ),
                    Logger[F].info(s"No session with id: $sessionId")
                  )
                }).flatten >> // Show sessions total
              queue.size.flatMap(size =>
                Logger[F].debug(s"CleanUp: sessions::Total: ${size}"))
          }

          override def total: F[Int] = {
            queue.size.flatMap(size =>
              Logger[F].info(s"Total sessions: ${size}")) *> queue.size
          }
        }
      }
    }.flatten
  }

}
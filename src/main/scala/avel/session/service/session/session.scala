package avel.session.service.session

import avel.session.service.models.SessionState
import cats.Monad
import cats.effect.kernel.{Ref, Sync}
import org.typelevel.log4cats.Logger

trait SessionService[F[_]] {
  def getState: F[SessionState]
  def inc: F[Unit]
  def log: F[Unit]
}

object SessionServiceImpl {
  def make[F[_]: Sync: Logger](state : F[Ref[F, SessionState]]) : SessionService[F] = {
    new SessionServiceImpl[F](state)
  }
}

// TODO: implement/pass Ref in some way
class SessionServiceImpl[F[_]: Sync: Logger] private(state : F[Ref[F, SessionState]]) extends SessionService[F] {
//  private val state : F[Ref[F, SessionState]] = Ref.of(SessionState(0))

  override def getState: F[SessionState] =  {
    Monad[F].flatMap(state){ ps =>
       ps.get
    }

  }
  override def inc: F[Unit] = {
    Monad[F].flatMap(state) { ps =>
      ps.update (s => SessionState(counter = s.counter + 5))
    }
  }

  override def log: F[Unit] = {
    Monad[F].flatMap(state) { ps =>
      Logger[F].info(s"Current state: ${ps.get}")
    }
  }
}
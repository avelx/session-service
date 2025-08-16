package avel.session.service.session

import avel.session.service.models.SessionState
import cats.effect.kernel.Sync
import cats.implicits.catsSyntaxApplicativeId

trait SessionService[F[_]] {
  def getState: F[SessionState]
}

object SessionServiceImpl {
  def make[F[_]: Sync] : SessionService[F] = {
    new SessionServiceImpl[F]( SessionState(0))
  }
}

// TODO: implement/pass Ref in some way
class SessionServiceImpl[F[_]: Sync] private (state: SessionState) extends SessionService[F] {
  override def getState: F[SessionState] =  {
    val newState = state.copy(counter = state.counter + 1)
    newState.pure[F]
  }
}
package avel.session.service.services

import avel.session.service.models.SessionState
import cats.effect.kernel.{Ref, Sync}
import cats.syntax.all._
import cats.{Functor, Monad}

import scala.util.Random

trait SessionService[F[_]] {
  def getState: F[SessionState]
  def inc: F[Unit]
}

object SessionServiceImpl {

  def make[F[_]: Sync] : SessionService[F] = {
    val state : F[Ref[F, SessionState]] = Ref.of(SessionState(0))
    new SessionServiceImpl[F](state)
  }
}

// TODO: implement/pass Ref in some way
class SessionServiceImpl[F[_]: Sync] private(state : F[Ref[F, SessionState]]) extends SessionService[F] {

  override def getState: F[SessionState] =  {
      Monad[F].flatMap(state){ ps =>
        Functor[F].map(ps.get){ value =>
          println(s"CheapWay: $value")
          //Logger[F].info(s"Extract value: ${value.toString}")
        } *>
        ps.get
      }

  }
  override def inc: F[Unit] = {
    Monad[F].flatMap(state) { ps =>
      ps.set(SessionState(counter = Random.nextInt()))
    }
  }

}
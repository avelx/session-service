package avel.session.service.session

import avel.session.service.models.SessionState
import cats.{Functor, Monad}
import cats.effect.kernel.{Ref, Sync}
import cats.syntax.all._

trait SessionService[F[_]] {
  def getState: F[SessionState]
  def inc: F[Unit]
  //def log: F[Unit]
}

object SessionServiceImpl {
  //implicit def logger[F[_]: Sync]: Logger[F] = Slf4jLogger.getLogger[F]

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
    val s = Monad[F].flatMap(state) { ps =>
      ps.update (s => SessionState(counter = s.counter + 5))
    }
    s
  }

//  override def log: F[Unit] = {
//    Monad[F].flatMap(state) { ps =>
//      Functor[F].map(ps.get){ value =>
//              Logger[F].info(s"Here is actual value: ${value.toString}")
//            } *>
//      ().pure[F]
//    }
//  }
}
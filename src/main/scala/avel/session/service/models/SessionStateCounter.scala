package avel.session.service.models

import avel.session.service.SessionState
import cats.Functor
import cats.effect.kernel.Ref
import cats.syntax.functor._

trait SessionStateCounter[F[_]] {
  def get: F[SessionState]

  def inc: F[Unit]
}

object SessionStateCounterImpl {

  def make[F[_] : Functor : Ref.Make]: F[SessionStateCounter[F]] = {
    Ref.of[F, SessionState](SessionState(0)).map { ref =>
      new SessionStateCounter[F] {
        def inc: F[Unit] = {
          ref.update(st => { // Use same sessionState object
            //            val res =
            st.copy(counter = st.counter + 1)
            //println(s"Current state: $res")
            //            res
          })
        }

        def get: F[SessionState] = ref.get
      }
    }
  }

}
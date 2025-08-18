package avel.session.service

import avel.session.service.models.SessionState
import cats.Functor
import cats.syntax.functor._
import cats.effect.kernel.Ref

trait Counter[F[_]] {

  def get: F[SessionState]

  def inc: F[Unit]
}

object CounterImpl {

  def make[F[_] : Functor : Ref.Make]: F[Counter[F]] = {
    Ref.of[F, SessionState](SessionState(0)).map { ref =>
      new Counter[F] {
        def inc: F[Unit] = {
          ref.update(st => SessionState(counter = st.counter + 1))
        }
        def get: F[SessionState] = ref.get
      }
    }
  }

}
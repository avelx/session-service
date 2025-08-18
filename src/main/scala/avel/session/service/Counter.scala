package avel.session.service

import cats.{Functor}
import cats.syntax.functor.
_
import cats.effect.kernel.Ref

trait Counter[F[_]] {

  def get: F[Int]

  def inc: F[Int]
}

object CounterImpl {

  def make[F[_] : Functor : Ref.Make]: F[Counter[F]] = {
    Ref.of[F, Int](0).map { ref =>
      new Counter[F] {
        def inc: F[Int] = {
          ref.updateAndGet(_ + 1)
        }
        def get: F[Int] = ref.get
      }
    }
  }

}
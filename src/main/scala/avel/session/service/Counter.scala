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

//  def makeCounter[F[_]](incF: F[Int], retrieveF: F[Int]): Counter[F] = new Counter[F] {
//    override def get: F[Int] = retrieveF
//
//    override def inc: F[Int] = incF
//  }

  def make[F[_] : Functor : Ref.Make]: F[Counter[F]] = {
    Ref.of[F, Int](0).map { ref =>
      println("Create new ...")
      new Counter[F] {
        def inc: F[Int] = {
          println("Update ...")
          ref.updateAndGet(_ + 1)
        }
        def get: F[Int] = ref.get
      }
    }
  }

}
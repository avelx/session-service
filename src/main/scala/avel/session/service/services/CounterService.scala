package avel.session.service.services

import avel.session.service.CounterState
import cats.effect.kernel.{Ref, Sync}
import cats.implicits.{catsSyntaxApplyOps, toFlatMapOps}
import cats.syntax.functor._
import org.typelevel.log4cats.Logger

trait CounterService[F[_]] {
  def get: F[CounterState]
  def inc: F[Unit]
}

object CounterService {

  def impl[F[_] : Sync : Ref.Make : Logger]: F[CounterService[F]] = {
    Ref.of[F, CounterState](CounterState(0)).map { ref =>
      new CounterService[F] {

        def inc: F[Unit] =

              ref.update(st => {
                st.copy(counter = st.counter + 1)
              }) *> // This is a way to extract value out of F[_] and log it
                 ref.get.flatMap{v =>
                    Logger[F].debug(s"SS_CNT::INC::VALUE->$v")
                }

        def get: F[CounterState] = {
          ref.get.flatMap{v =>
            Logger[F].debug(s"SS_CNT::GET::VALUE->$v")
          } *>
          ref.get
        }
      }
    }
  }

}
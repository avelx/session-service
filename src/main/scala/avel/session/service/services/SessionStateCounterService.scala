package avel.session.service.services

import avel.session.service.SessionState
import cats.effect.kernel.{Ref, Sync}
import cats.implicits.{catsSyntaxApplyOps, toFlatMapOps}
import cats.syntax.functor._
import org.typelevel.log4cats.Logger

trait SessionStateCounterService[F[_]] {
  def get: F[SessionState]
  def inc: F[Unit]
}

object SessionStateCounterService {

  def impl[F[_] : Sync : Ref.Make : Logger]: F[SessionStateCounterService[F]] = {
    Ref.of[F, SessionState](SessionState(0)).map { ref =>
      new SessionStateCounterService[F] {

        def inc: F[Unit] =

              ref.update(st => {
                st.copy(counter = st.counter + 1)
              }) *> // This is a way to extract value out of F[_] and log it
                 ref.get.flatMap{v =>
                    Logger[F].debug(s"SS_CNT::INC::VALUE->$v")
                }

        def get: F[SessionState] = {
          ref.get.flatMap{v =>
            Logger[F].debug(s"SS_CNT::GET::VALUE->$v")
          } *>
          ref.get
        }
      }
    }
  }

}
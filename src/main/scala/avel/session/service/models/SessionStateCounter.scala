package avel.session.service.models

import avel.session.service.SessionState
import cats.effect.kernel.{Ref, Sync}
import cats.implicits.{catsSyntaxApplyOps, toFlatMapOps}
import cats.syntax.functor._
import org.typelevel.log4cats.Logger

trait SessionStateCounter[F[_]] {
  def get: F[SessionState]
  def inc: F[Unit]
}

object SessionStateCounter {

  def impl[F[_] : Sync : Ref.Make : Logger]: F[SessionStateCounter[F]] = {
    Ref.of[F, SessionState](SessionState(0)).map { ref =>
      new SessionStateCounter[F] {

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
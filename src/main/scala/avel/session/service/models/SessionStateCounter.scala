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

object SessionStateCounterImpl {

  def make[F[_] : Sync : Ref.Make : Logger]: F[SessionStateCounter[F]] = {
    Ref.of[F, SessionState](SessionState(0)).map { ref =>
      new SessionStateCounter[F] {

        def inc: F[Unit] =
         Logger[F].info("ATTEMPT:::INC") *>
            Sync[F].delay(
              ref.update(st => {
                st.copy(counter = st.counter + 1)
              })
            ).flatMap(x => x)

        def get: F[SessionState] = ref.get
      }
    }
  }

}
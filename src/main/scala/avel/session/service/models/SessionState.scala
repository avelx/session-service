package avel.session.service

import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe._

final case class SessionState private( counter: Int)

object SessionState {
  def apply(counter: Int): SessionState = {
    new SessionState( counter = counter)
  }

  implicit val sessionStateEncoder: Encoder[SessionState] = new Encoder[SessionState] {
    final def apply(a: SessionState): Json = Json.obj(
      ("state", Json.fromInt(a.counter))
    )
  }

  implicit def sessionStateEntityEncoder[F[_]]: EntityEncoder[F, SessionState] =
    jsonEncoderOf[F, SessionState]
}

package avel.session.service

import java.util.UUID

object models {
  import io.circe.{Encoder, Json}
  import org.http4s.EntityEncoder
  import org.http4s.circe._

  final case class SessionState private(id: UUID, counter : Int)

  object SessionState {
    def apply(counter: Int) : SessionState = {
      new SessionState(id = UUID.randomUUID(), counter = counter)
    }

    implicit val sessionStateEncoder: Encoder[SessionState] = new Encoder[SessionState] {
      final def apply(a: SessionState): Json = Json.obj(
        ("guid", Json.fromString(a.id.toString)),
        ("state", Json.fromInt(a.counter)),
      )
    }

    implicit def sessionStateEntityEncoder[F[_]]: EntityEncoder[F, SessionState] =
      jsonEncoderOf[F, SessionState]
  }
}
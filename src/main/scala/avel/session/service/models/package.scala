package avel.session.service

object models {
  import io.circe.{Encoder, Json}
  import org.http4s.EntityEncoder
  import org.http4s.circe._


  final case class Session(sessionId: String) extends AnyVal

  final case class SessionState(counter: Int) extends AnyVal


  object Session {
    implicit val sessionEncoder: Encoder[Session] = new Encoder[Session] {
      final def apply(a: Session): Json = Json.obj(
        ("sessionId", Json.fromString(a.sessionId.toString)),
      )
    }

    implicit def sessionEntityEncoder[F[_]]: EntityEncoder[F, Session] =
      jsonEncoderOf[F, Session]
  }

  object SessionState {
    implicit val sessionStateEncoder: Encoder[SessionState] = new Encoder[SessionState] {
      final def apply(a: SessionState): Json = Json.obj(
        ("state", Json.fromInt(a.counter)),
      )
    }

    implicit def sessionStateEntityEncoder[F[_]]: EntityEncoder[F, SessionState] =
      jsonEncoderOf[F, SessionState]
  }
}
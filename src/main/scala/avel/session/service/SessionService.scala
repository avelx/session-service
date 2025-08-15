package avel.session.service

import avel.session.service.SessionService.{Session, SessionState}
import cats.Applicative
import cats.implicits.catsSyntaxApplicativeId
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe._

trait SessionService[F[_]] {
  def getState(session: Session): F[Option[SessionState]]
}

object SessionService {

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


  def impl[F[_]: Applicative](state: Map[String, SessionState]): SessionService[F] = new SessionService[F] {
    override def getState(session: Session): F[Option[SessionState]] = {
      state
        .collect {
          case (sessionId, stateFound) if sessionId == session.sessionId => stateFound
        }
        .headOption
        .pure[F]
    }
  }
}

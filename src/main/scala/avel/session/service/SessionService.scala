package avel.session.service

import avel.session.service.SessionService.{Session, SessionState}
import cats.effect.IO
import cats.effect.kernel.Ref
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe._

trait SessionService {
  def getState(session: Session): IO[Option[SessionState]]
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


  def impl(state: IO[Ref[IO, Map[String, SessionState]]]): SessionService = new SessionService {
    override def getState(session: Session): IO[Option[SessionState]] = {
      for {
        res <- state
        x <- res.get
      } yield x
        .collect {
          case (sessionId, stateFound) if sessionId == session.sessionId => stateFound
        }
        .headOption
    }
  }
}

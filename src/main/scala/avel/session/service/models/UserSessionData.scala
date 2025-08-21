package avel.session.service.models

import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe._

/*
  Ony auth user can have only a single session/(id)
  Ticks counter: record will be erased when ticks == 0
 */
final case class UserSessionData(
                                  sessionId: String,
                                  ticks: Long = 0, // Time To Live counter
                                  tag: String
                                )


object UserSessionData {

  implicit val sessionStateEncoder: Encoder[UserSessionData] = new Encoder[UserSessionData] {
    final def apply(a: UserSessionData): Json = Json.obj(
      ("sessionId", Json.fromString(a.sessionId)),
      ("ticks", Json.fromLong(a.ticks)),
      ("tag", Json.fromString(a.tag)),
    )
  }

  implicit def sessionStateEntityEncoder[F[_]]: EntityEncoder[F, UserSessionData] =
    jsonEncoderOf[F, UserSessionData]
}
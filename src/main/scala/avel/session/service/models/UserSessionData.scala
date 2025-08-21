package avel.session.service.models

import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe._

import java.time.Instant

/*
  Ony auth user can have only a single session/(id)
  Ticks counter: record will be erased when ticks == 0
 */
final case class UserSessionData private (
                                  sessionId: String,
                                  creationTick : Long,
                                  ticks: Long, // Time To Live counter
                                  tag: String
                                ) {
  def tick: Option[UserSessionData] = {
    val ticksElapsed = Instant.now().getEpochSecond - this.creationTick
    if (this.ticks - ticksElapsed > 0)
      Some(this.copy(ticks = this.ticks - ticksElapsed, creationTick = Instant.now().getEpochSecond))
    else
      None
  }
}


object UserSessionData {

  def apply(sessionId: String, ticks: Long, tag: String) = {
    new UserSessionData(sessionId,
      creationTick = Instant.now().getEpochSecond,
      ticks = ticks,
      tag = tag)
  }



  implicit val sessionStateEncoder: Encoder[UserSessionData] = new Encoder[UserSessionData] {
    final def apply(a: UserSessionData): Json = Json.obj(
      ("sessionId", Json.fromString(a.sessionId)),
      ("ttl", Json.fromLong(a.ticks)),
      ("tag", Json.fromString(a.tag)),
    )
  }

  implicit def sessionStateEntityEncoder[F[_]]: EntityEncoder[F, UserSessionData] =
    jsonEncoderOf[F, UserSessionData]
}
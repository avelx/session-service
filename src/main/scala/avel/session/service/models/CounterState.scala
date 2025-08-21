package avel.session.service

import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe._

final case class CounterState private(counter: Int)

object CounterState {
  def apply(counter: Int): CounterState = {
    new CounterState( counter = counter)
  }

  implicit val sessionStateEncoder: Encoder[CounterState] = new Encoder[CounterState] {
    final def apply(a: CounterState): Json = Json.obj(
      ("state", Json.fromInt(a.counter))
    )
  }

  implicit def sessionStateEntityEncoder[F[_]]: EntityEncoder[F, CounterState] =
    jsonEncoderOf[F, CounterState]
}

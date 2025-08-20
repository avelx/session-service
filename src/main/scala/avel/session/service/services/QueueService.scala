package avel.session.service.services

import cats.effect.kernel.Async
import cats.effect.std.Queue
import cats.implicits._
import org.typelevel.log4cats.Logger

trait QueueService[F[_]] {
  def pull: F[Option[String]]
  def push(item: String): F[Unit]
}


object QueueService {
  private val queueSize : Int = 10

  def imp[F[_]: Async: Logger] : F[QueueService[F]] = {

    Queue.bounded[F, String](queueSize).map { queue =>
      new QueueService[F] {

        override def pull: F[Option[String]] = {
          queue.tryTake.flatMap{ value =>
            Logger[F].info(s"QueueService::PULL: $value") *>
            value.pure[F]
          }

        }

        override def push(item: String): F[Unit] = {
          Logger[F].info(s"QueueService::PUSH->$item") *>
          queue.offer(item)
        }

      }
    }

  }
}
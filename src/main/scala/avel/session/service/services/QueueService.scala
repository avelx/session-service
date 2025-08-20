package avel.session.service.services

import cats.effect.kernel.Async
import cats.effect.std.Queue
import cats.implicits._

trait QueueService[F[_]] {
  def pull: F[String]
  def push(item: String): F[Unit]
}


object QueueService {
  val queueSize : Int = 10

  def imp[F[_]: Async] : F[QueueService[F]] = {

    Queue.bounded[F, String](queueSize).map { queue =>
      new QueueService[F] {

        override def pull: F[String] = {
          queue.take
        }

        override def push(item: String): F[Unit] = {
          queue.offer(item)
        }

      }
    }

  }
}
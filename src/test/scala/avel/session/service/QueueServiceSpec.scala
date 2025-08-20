package avel.session.service

import avel.session.service.routes.QueueuRoutes
import avel.session.service.services.QueueService
import cats.effect.IO
import fs2.Compiler.Target.forConcurrent
import munit.CatsEffectSuite
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{Method, Request, Response, Status, Uri}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class QueueServiceSpec extends CatsEffectSuite {

  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  test("QueueService::push returns status code 200") {
    List(
      uri"/queue/push/itemA",
      uri"/queue/push/itemB",
      uri"/queue/push/itemC",
      uri"/queue/push/itemD").foreach { u =>
        assertIO(retQueueService(u).map(_.status), Status.Ok)
    }
  }

  test("QueueService::pull returns status code 200") {
      assertIO(retQueueServiceDobuleRequest(
        uri"/queue/push/itemA", uri"/queue/push/secondItem" , uri"/queue/pull")
        .flatMap(x => x.as[Option[String]]), Some("secondItem"))
  }


  private[this] def retQueueServiceDobuleRequest(uriOne: Uri, uriTwo: Uri, uriLastOne: Uri): IO[Response[IO]] = {
    val pushRequest: Request[IO] = Request[IO](Method.GET, uriOne)
    val push2Request: Request[IO] = Request[IO](Method.GET, uriTwo)
    val pullRequest: Request[IO] = Request[IO](Method.GET, uriLastOne)
    for {
      queueService <- QueueService.impl[IO]
      _ <- QueueuRoutes[IO](queueService).routes().orNotFound(pushRequest)
      _ <- QueueuRoutes[IO](queueService).routes().orNotFound(push2Request)
      _ <- QueueuRoutes[IO](queueService).routes().orNotFound(pullRequest)
      response <- QueueuRoutes[IO](queueService).routes().orNotFound(pullRequest)
    } yield response
  }

  private[this] def retQueueService(path: Uri): IO[Response[IO]] = {
    val pushRequest: Request[IO] = Request[IO](Method.GET, path)
    for {
      queueService <- QueueService.impl[IO]
      response <- QueueuRoutes[IO](queueService).routes().orNotFound(pushRequest)
    } yield response
  }

}

package avel.session.service

import avel.session.service.routes.QueueuRoutes
import avel.session.service.services.QueueService
import cats.effect.IO
import org.http4s.{Method, Request, Response, Status, Uri}
import munit.CatsEffectSuite
import org.http4s.implicits.http4sLiteralsSyntax
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class QueueServiceSpec extends CatsEffectSuite {

  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  test("QueueService::INC returns status code 200") {
    List(
      uri"/queue/push/itemA",
      uri"/queue/push/itemB",
      uri"/queue/push/itemC",
      uri"/queue/push/itemD").foreach { u =>
        assertIO(retQueueService(u).map(_.status), Status.Ok)
    }
  }

  private[this] def retQueueService(path: Uri): IO[Response[IO]] = {
    val pushRequest: Request[IO] = Request[IO](Method.GET, path)
    for {
      queueService <- QueueService.impl[IO]
      response <- QueueuRoutes[IO](queueService).routes().orNotFound(pushRequest)
    } yield response
  }

}

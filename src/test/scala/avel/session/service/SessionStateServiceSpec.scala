package avel.session.service

import avel.session.service.routes.SessionStateRoutes
import avel.session.service.services.SessionStateCounterService
import cats.effect.IO
import munit.CatsEffectSuite
import org.http4s._
import org.http4s.implicits.http4sLiteralsSyntax
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class SessionStateServiceSpec extends CatsEffectSuite {

  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  test("SessionService :: returns status code 200") {
    assertIO(retSessionState.map(_.status), Status.Ok) *>
      assertIO(retSessionState.flatMap(x => x.as[String]), "{\"state\":0}")
  }

  test("SessionService::INC returns status code 200") {
    assertIO(retSessionStateInc.map(_.status), Status.Ok)
  }

  private[this] def retSessionState: IO[Response[IO]] = {
    val getHW = Request[IO](Method.GET, uri"/session")
    for {
      stateService <- SessionStateCounterService.impl[IO]
      response <- SessionStateRoutes[IO](stateService).routes().orNotFound(getHW)
    } yield response
  }

  private[this] def retSessionStateInc: IO[Response[IO]] = {
    val incStateRequest: Request[IO] = Request[IO](Method.GET, uri"/session/inc")
    for {
      stateService <- SessionStateCounterService.impl[IO]
      response <- SessionStateRoutes[IO](stateService).routes().orNotFound(incStateRequest)
    } yield response
  }

}
package avel.session.service

import avel.session.service.models.SessionStateCounter
import avel.session.service.routes.SessionStateRoutes
import cats.effect.IO
import org.http4s._
import munit.CatsEffectSuite
import org.http4s.implicits.http4sLiteralsSyntax
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger

class SessionStateSpec extends CatsEffectSuite {

  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

    test("SessionService :: returns status code 200") {
      assertIO(retSessionState.map(_.status) ,Status.Ok)
    }


    test("SessionService::INC returns status code 200") {
      assertIO(retSessionStateInc.map(_.status) ,Status.Ok)
      //TODO: fix values for the test
      //assertIO(retSessionStateInc.flatMap(x => x.as[String]), "{\"message\":\"Hello, world\"}")
    }

  private[this] def retSessionState: IO[Response[IO]] = {
    val getHW = Request[IO](Method.GET, uri"/session")
    for {
      state <- SessionStateCounter.impl[IO]
      request <- SessionStateRoutes[IO](state).routes().orNotFound(getHW)
    } yield request
  }

  private[this] def retSessionStateInc: IO[Response[IO]] = {
    val getHW = Request[IO](Method.GET, uri"/session/inc")
    for {
      state <- SessionStateCounter.impl[IO]
      request <- SessionStateRoutes[IO](state).routes().orNotFound(getHW)
    } yield request
  }

}
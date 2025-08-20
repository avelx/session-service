package avel.session.service.config

import cats.effect.kernel.Sync
import cats.implicits.catsSyntaxApplicativeId
import pureconfig._
import pureconfig.generic.auto._

object Config {

  // Ciris promotes configuration as code
  def load[F[_] : Sync]: F[Option[ServiceConfig]] = {
    ConfigSource.default.load[ServiceConfig].toOption.pure[F]
  }

}
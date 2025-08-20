package avel.session.service.config

import cats.effect.kernel.Sync
import cats.implicits.catsSyntaxApplicativeId
import pureconfig._
import pureconfig.generic.auto._

object Config {

  def load[F[_] : Sync]: F[ServiceConfig] = {
    ConfigSource
      .default
      .load[ServiceConfig]
      .toOption.getOrElse(throw new Error("ServiceConfig is missing"))
      .pure[F]

  }

}
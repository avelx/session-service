package avel.session.service.config

case class ServiceConfig(host: String, port: Port)

case class Port(number: Int) extends AnyVal
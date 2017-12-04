package org.wbq.akka

sealed trait AkkaMsg

case class RegistClient(host: String, port: Int, actorSystem: String, actorName: String) extends AkkaMsg

package org.wbq.akka

import akka.actor.ActorRef

sealed trait AkkaMsg

case class RegistClient(runId: String, taskId: String, host: String, port: Int, actorSystem: String, actorName: String, send: ActorRef) extends AkkaMsg

case class RegistedClient(master: ActorRef) extends AkkaMsg

case class RunClass(classRunId: String, className: String, classPath: String, send: ActorRef) extends AkkaMsg

case class ClassRunSuccess(classRunId: String, send: ActorRef) extends AkkaMsg

case class ClassRunFailed(classRunId: String, msg: String, send: ActorRef) extends AkkaMsg

case class DestroyClient(send: ActorRef) extends AkkaMsg

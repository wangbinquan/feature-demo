package org.wbq.akka.master

import akka.actor.{Actor, ActorRef}
import org.wbq.akka.{ClassRunFailed, ClassRunSuccess, RegistClient, RegistedClient}
import org.wbq.common.util.Logging

class MasterActor extends Actor with Logging {
  override def receive = {
    case r@RegistClient(runId: String, taskId: String, host: String, port: Int, actorSystem: String, actorName: String, send: ActorRef) => {
      LOG.info(r.toString)
      send ! RegistedClient(self)
    }
    case r@ClassRunSuccess(classRunId: String, send: ActorRef) =>{

    }
    case r@ClassRunFailed(classRunId: String, msg: String, send: ActorRef) =>{

    }
  }
}

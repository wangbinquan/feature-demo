package org.wbq.akka.client

import akka.actor.{Actor, ActorRef}
import org.wbq.akka._
import org.wbq.common.util.Logging

class ClientActor extends Actor with Logging {
  override def receive = {
    case r@RegistedClient(master: ActorRef) => {
      LOG.info(s"Regist client to [$master] success")
    }
    case r@RunClass(classRunId: String, className: String, classPath: String, send: ActorRef) =>{

    }
    case d@DestroyClient(send: ActorRef) =>{

    }
  }
}

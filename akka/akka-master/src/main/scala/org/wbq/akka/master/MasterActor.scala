package org.wbq.akka.master

import akka.actor.Actor
import org.wbq.akka.RegistClient
import org.wbq.common.util.Logging

class MasterActor extends Actor with Logging{
  override def receive = {
    case r@RegistClient(host: String, port: Int, actorSystem: String, actorName: String) =>{
      LOG.info(r.toString)
      sender() ! "OK~~~~~~~~~~~~~~~~~~~~~~~~~~~"
    }
  }
}

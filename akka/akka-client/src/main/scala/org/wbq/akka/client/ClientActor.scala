package org.wbq.akka.client

import akka.actor.{Actor, ActorRef}
import org.wbq.akka.{AkkaTools, RegistClient}
import org.wbq.common.util.Logging

class ClientActor extends Actor with Logging{
  override def receive = {
    case s: String => {
      LOG.info(s"Get String[$s]")
    }
    case master: ActorRef =>{
      master ! RegistClient(context.system.settings.config.getString("akka.remote.netty.tcp.hostname"),
        context.system.settings.config.getInt("akka.remote.netty.tcp.port"), context.system.settings.name, self.path.name)
    }
  }
}

package org.wbq.akka.master

import akka.actor
import akka.actor.{ActorRef, ActorSystem, Props}
import org.wbq.common.util.GenName

object AkkaMaster {
  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem.create("MyAkkaMaster")
    val masterActor: actor.ActorRef = actorSystem.actorOf(Props[MasterActor], name = "masterActor")
    val master = akkaUrlOf(actorSystem, masterActor)
    ClientTools.createNewClient(GenName.genName("runId"), GenName.genName("taskId"), master)
  }

  def akkaUrlOf(actorSystem: ActorSystem, actorRef: ActorRef): String = {
    val serviceName = actorSystem.settings.name
    val host = actorSystem.settings.config.getString("akka.remote.netty.tcp.hostname")
    val port = actorSystem.settings.config.getInt("akka.remote.netty.tcp.port")
    val actorName = actorRef.path.name
    "akka.tcp://" + serviceName + "@" + host + ":" + port + "/user/" + actorName
  }

}

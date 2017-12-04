package org.wbq.akka.master

import akka.actor.{ActorSystem, Props}

object AkkaMaster {
  def main(args: Array[String]): Unit = {
    val actorSystem = ActorSystem.create("MyAkkaMaster")
    val masterActor = actorSystem.actorOf(Props[MasterActor], name = "masterActor")

  }

}

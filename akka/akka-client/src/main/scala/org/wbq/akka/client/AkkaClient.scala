package org.wbq.akka.client

import akka.actor._
import akka.util.Timeout._

import scala.concurrent.duration._
import akka.pattern._
import org.wbq.akka.{AkkaTools, RegistClient}
import org.wbq.common.util.Logging

import scala.concurrent.ExecutionContext.Implicits._


object AkkaClient extends Logging {
  def main(args: Array[String]): Unit = {
    LOG.info(s"Client start with args${args.mkString("[", "][", "]")}")
    ClientInfo.runId = args(0)
    ClientInfo.taskId = args(1)
    ClientInfo.masterUrl = args(2)
    LOG.info(s"Client runId[${ClientInfo.runId}] taskId[${ClientInfo.taskId}] masterUrl[${ClientInfo.masterUrl}]")

    //    implicit val finiteDuration = FiniteDuration(1000, "second")

    val actorSystem0: Option[ActorSystem] = AkkaTools.builder().name("MyAkkaClient").host("127.0.0.1").buildWithAutoPort(100)
    val actorSystem1: Option[ActorSystem] = AkkaTools.builder().name("MyAkkaClient").host("127.0.0.1").buildWithAutoPort(100)
    val actorSystem2: Option[ActorSystem] = AkkaTools.builder().name("MyAkkaClient").host("127.0.0.1").buildWithAutoPort(100)
    val actorSystem3: Option[ActorSystem] = AkkaTools.builder().name("MyAkkaClient").host("127.0.0.1").buildWithAutoPort(100)
    val actorSystem4: Option[ActorSystem] = AkkaTools.builder().name("MyAkkaClient").host("127.0.0.1").buildWithAutoPort(100)

    val act0: ActorRef = actorSystem0.get.actorOf(Props[ClientActor], name = "clientActor")
    act0 ! "Start"
    val act1 = actorSystem1.get.actorOf(Props[ClientActor], name = "clientActor")
    act1 ! "Start"
    val act2 = actorSystem2.get.actorOf(Props[ClientActor], name = "clientActor")
    act2 ! "Start"
    val act3 = actorSystem3.get.actorOf(Props[ClientActor], name = "clientActor")
    act3 ! "Start"
    val act4 = actorSystem4.get.actorOf(Props[ClientActor], name = "clientActor")
    act4 ! "Start"

    for (calcActor: ActorRef <- actorSystem0.get.actorSelection(ClientInfo.masterUrl).resolveOne(FiniteDuration(1000, "s"))) {
      calcActor ! RegistClient(ClientInfo.runId, ClientInfo.taskId, actorSystem0.get.settings.config.getString("akka.remote.netty.tcp.hostname"), actorSystem0.get.settings.config.getInt("akka.remote.netty.tcp.port"), actorSystem0.get.settings.name, act0.path.name, act0)
      calcActor ! RegistClient(ClientInfo.runId, ClientInfo.taskId, actorSystem1.get.settings.config.getString("akka.remote.netty.tcp.hostname"), actorSystem1.get.settings.config.getInt("akka.remote.netty.tcp.port"), actorSystem1.get.settings.name, act1.path.name, act1)
      calcActor ! RegistClient(ClientInfo.runId, ClientInfo.taskId, actorSystem2.get.settings.config.getString("akka.remote.netty.tcp.hostname"), actorSystem2.get.settings.config.getInt("akka.remote.netty.tcp.port"), actorSystem2.get.settings.name, act2.path.name, act2)
      calcActor ! RegistClient(ClientInfo.runId, ClientInfo.taskId, actorSystem3.get.settings.config.getString("akka.remote.netty.tcp.hostname"), actorSystem3.get.settings.config.getInt("akka.remote.netty.tcp.port"), actorSystem3.get.settings.name, act3.path.name, act3)
      calcActor ! RegistClient(ClientInfo.runId, ClientInfo.taskId, actorSystem4.get.settings.config.getString("akka.remote.netty.tcp.hostname"), actorSystem4.get.settings.config.getInt("akka.remote.netty.tcp.port"), actorSystem4.get.settings.name, act4.path.name, act4)
    }
  }


}

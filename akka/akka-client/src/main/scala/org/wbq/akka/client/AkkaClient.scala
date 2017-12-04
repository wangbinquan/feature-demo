package org.wbq.akka.client

import akka.actor._
import akka.util.Timeout._

import scala.concurrent.duration._
import akka.pattern._
import org.wbq.akka.AkkaTools

import scala.concurrent.ExecutionContext.Implicits._


object AkkaClient {
  def main(args: Array[String]): Unit = {
//    implicit val finiteDuration = FiniteDuration(1000, "second")

    val actorSystem0: Option[ActorSystem] = AkkaTools.builder().name("MyAkkaClient").host("127.0.0.1").buildWithAutoPort(100)
    val actorSystem1: Option[ActorSystem] = AkkaTools.builder().name("MyAkkaClient").host("127.0.0.1").buildWithAutoPort(100)
    val actorSystem2: Option[ActorSystem] = AkkaTools.builder().name("MyAkkaClient").host("127.0.0.1").buildWithAutoPort(100)
    val actorSystem3: Option[ActorSystem] = AkkaTools.builder().name("MyAkkaClient").host("127.0.0.1").buildWithAutoPort(100)
    val actorSystem4: Option[ActorSystem] = AkkaTools.builder().name("MyAkkaClient").host("127.0.0.1").buildWithAutoPort(100)

    val master = AkkaTools.akkaUrlOf("MyAkkaMaster", "127.0.0.1", 15962, "masterActor")
    val act0: ActorRef = actorSystem0.get.actorOf(Props[ClientActor], name = "clientActor")
    act0 ! "Start"
    for (calcActor : ActorRef <- actorSystem0.get.actorSelection(master).resolveOne(FiniteDuration(1000, "s"))) {
      act0 ! calcActor
    }

    val act1 = actorSystem1.get.actorOf(Props[ClientActor], name = "clientActor")
    act1 ! "Start"
    val act2 = actorSystem2.get.actorOf(Props[ClientActor], name = "clientActor")
    act2 ! "Start"
    val act3 = actorSystem3.get.actorOf(Props[ClientActor], name = "clientActor")
    act3 ! "Start"
    val act4 = actorSystem4.get.actorOf(Props[ClientActor], name = "clientActor")
    act4 ! "Start"
  }


}

package org.wbq.akka

import java.util

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}
import org.jboss.netty.channel.ChannelException
import org.wbq.common.util.Logging

import scala.collection.JavaConverters._

object AkkaTools extends Logging {
  def builder(): AkkaActorBuilder = new AkkaActorBuilder()

  def akkaUrlOf(serviceName: String, host: String, port: Int, actorName: String): String = {
    "akka.tcp://" + serviceName + "@" + host + ":" + port + "/user/" + actorName
  }

  class AkkaActorBuilder {
    private var name = "defaultName"
    private val confMap = new util.HashMap[String, Object]()

    private val remoteTransports = new util.ArrayList[String]()
    remoteTransports.add("akka.remote.netty.tcp")
    confMap.put("akka.remote.enabled-transports", remoteTransports)
    confMap.put(AkkaProps.MAX_FRAME_SIZE, Integer.valueOf(100 * 1024 * 1024))
    confMap.put("akka.actor.default-dispatcher.fork-join-executor.parallelism-factor", "50")
    confMap.put("akka.actor.default-dispatcher.fork-join-executor.parallelism-max", "50")
    //    confMap .put("akka.actor.provider", "remote")
    confMap.put("akka.actor.remote.log-sent-messages", "on")
    confMap.put("akka.actor.remote.log-received-messages", "on")
    confMap.put("akka.actor.provider", "akka.remote.RemoteActorRefProvider")


    def name(name: String): this.type = {
      this.name = name
      this
    }

    def set(key: String, value: Object): this.type = {
      confMap.put(key, value)
      this
    }

    def host(host: String): this.type = {
      set(AkkaProps.HOST, host)
    }

    def port(port: Int): this.type = {
      set(AkkaProps.PORT, Integer.valueOf(port))
    }

    private def tryBuild(port: Int): Option[ActorSystem] = {
      set(AkkaProps.PORT, Integer.valueOf(port))
      val config: Config = ConfigFactory.parseMap(confMap)
      try {
        LOG.info(s"Start ActorSystem, Props{${config.entrySet().asScala.mkString(",")}}")
        val actorSystem = ActorSystem.create(name, config)
        LOG.info("Start ActorSystem Succeed")
        Some(actorSystem)
      }
      catch {
        case e: Throwable => {
          LOG.error("Start ActorSystem Failed", e)
          None
        }
      }
    }

    def buildWithAutoPort(maxTryNum: Int): Option[ActorSystem] = {
      val maxTry = if (maxTryNum > 20000 || maxTryNum < 0) 20000 else maxTryNum
      val startPort = 20000
      var currentPort = startPort
      var actorSystem: Option[ActorSystem] = None
      while (actorSystem.isEmpty && currentPort - startPort < maxTry) {
        actorSystem = tryBuild(currentPort)
        currentPort += 1
      }
      actorSystem
    }

    def build(): Option[ActorSystem] = {
      val config: Config = ConfigFactory.parseMap(confMap)
      try {
        LOG.info("Start ActorSystem")
        val actorSystem = ActorSystem.create(name, config)
        LOG.info("Start ActorSystem Succeed")
        Some(actorSystem)
      }
      catch {
        case e: Throwable => {
          LOG.error("Start ActorSystem Failed", e)
          None
        }
      }
    }
  }

}

object AkkaProps {
  val HOST = "akka.remote.netty.tcp.hostname"
  val PORT = "akka.remote.netty.tcp.port"
  val MAX_FRAME_SIZE = "akka.remote.netty.tcp.maximum-frame-size"
}

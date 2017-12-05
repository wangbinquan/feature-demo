package org.wbq.akka.master

import java.io.File

import org.wbq.common.util.shell.ShellRunner

object ClientTools {
  def createNewClient(runId: String, taskId: String, masterUrl: String): Unit = {
    val clientFile = new File("E:\\github\\feature-demo\\akka\\akka-client\\target")
    val javaCommand = "java"
    val classPath = "./lib/*;akka-client-1.0-SNAPSHOT.jar"
    val mainClass = "org.wbq.akka.client.AkkaClient"
    val classArgs = runId :: taskId :: masterUrl :: Nil

    val args = "-classpath" :: ("\"" + classPath + "\"") :: mainClass :: classArgs

    val shellRunner = new ShellRunner(runId, javaCommand, args, clientFile)
    shellRunner.start()
  }

}

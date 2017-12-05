/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
//FROM org.apache.spark.deploy.worker.ExecutorRunner

package org.wbq.common.util.shell

import java.io.File
import java.nio.charset.StandardCharsets

import org.wbq.common.util.{Logging, Utils}
import org.wbq.common.util.logging.FileAppender

import scala.collection.JavaConverters._

class ShellRunner(runId: String, runCommand: String, args: Seq[String], executorDir: File) extends Logging{

  @volatile var state: RunnerState.Value = RunnerState.LAUNCHING
  private var workerThread: Thread = null
  private var process: Process = null
  private var stdoutAppender: FileAppender = null
  private var stderrAppender: FileAppender = null

  // Timeout to wait for when trying to terminate an executor.
  private val EXECUTOR_TERMINATE_TIMEOUT_MS = 10 * 1000

  // NOTE: This is now redundant with the automated shut-down enforced by the Executor. It might
  // make sense to remove this in the future.
//  private var shutdownHook: AnyRef = null

  def start() {
    workerThread = new Thread("ExecutorRunner for " + runId) {
      override def run() { fetchAndRunExecutor() }
    }
    workerThread.start()
    // Shutdown hook that kills actors on shutdown.
//    shutdownHook = ShutdownHookManager.addShutdownHook { () =>
//      // It's possible that we arrive here before calling `fetchAndRunExecutor`, then `state` will
//      // be `ExecutorState.RUNNING`. In this case, we should set `state` to `FAILED`.
//      if (state == RunnerState.RUNNING) {
//        state = RunnerState.FAILED
//      }
//      killProcess(Some("Worker shutting down")) }
  }

  /**
    * Kill executor process, wait for exit and notify worker to update resource status.
    *
    * @param message the exception message which caused the executor's death
    */
  def killProcess(message: Option[String]) {
    var exitCode: Option[Int] = None
    if (process != null) {
      LOG.info("Killing process!")
      if (stdoutAppender != null) {
        stdoutAppender.stop()
      }
      if (stderrAppender != null) {
        stderrAppender.stop()
      }
      exitCode = Utils.terminateProcess(process, EXECUTOR_TERMINATE_TIMEOUT_MS)
      if (exitCode.isEmpty) {
        LOG.warn("Failed to terminate process: " + process +
          ". This process will likely be orphaned.")
      }
    }
//    try {
//      worker.send(ExecutorStateChanged(appId, execId, state, message, exitCode))
//    } catch {
//      case e: IllegalStateException => LOG.warn(e.getMessage(), e)
//    }
  }

  /** Stop this executor runner, including killing the process it launched */
  def kill() {
    if (workerThread != null) {
      // the workerThread will kill the child process when interrupted
      workerThread.interrupt()
      workerThread = null
      state = RunnerState.KILLED
//      try {
//        ShutdownHookManager.removeShutdownHook(shutdownHook)
//      } catch {
//        case e: IllegalStateException => None
//      }
    }
  }

  /**
    * Download and run the executor described in our ApplicationDescription
    */
  def fetchAndRunExecutor() {
    try {
      // Launch the process
      val builder = CommandUtils.buildProcessBuilder(runCommand, args)
      val command = builder.command()
      val formattedCommand = command.asScala.mkString("\"", "\" \"", "\"")
      logInfo(s"Launch command: $formattedCommand")

      builder.directory(executorDir)

      process = builder.start()
      val header = "Spark Executor Command: %s\n%s\n\n".format(
        formattedCommand, "=" * 40)

      // Redirect its stdout and stderr to files
      val stdout = new File(executorDir, s"${runId}_stdout")
      stdoutAppender = FileAppender(process.getInputStream, stdout)

      val stderr = new File(executorDir, s"${runId}_stderr")
      com.google.common.io.Files.asCharSink(stderr, StandardCharsets.UTF_8).write(header)

      stderrAppender = FileAppender(process.getErrorStream, stderr)

      // Wait for it to exit; executor may exit with code 0 (when driver instructs it to shutdown)
      // or with nonzero exit code
      val exitCode = process.waitFor()
      state = RunnerState.EXITED
      val message = "Command exited with code " + exitCode
//      worker.send(ExecutorStateChanged(appId, execId, state, Some(message), Some(exitCode)))
    } catch {
      case interrupted: InterruptedException =>
        logInfo("Runner thread for executor " + runId + " interrupted")
        state = RunnerState.KILLED
        killProcess(None)
      case e: Exception =>
        logError("Error running executor", e)
        state = RunnerState.FAILED
        killProcess(Some(e.toString))
    }
  }
}

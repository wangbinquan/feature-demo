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
//FROM org.apache.spark.util

package org.wbq.common.util

import java.util.concurrent.TimeUnit

import scala.util.control.{ControlThrowable, NonFatal}

object Utils extends Logging{
  /**
    * Execute the given block, logging and re-throwing any uncaught exception.
    * This is particularly useful for wrapping code that runs in a thread, to ensure
    * that exceptions are printed, and to avoid having to catch Throwable.
    */
  def logUncaughtExceptions[T](f: => T): T = {
    try {
      f
    } catch {
      case ct: ControlThrowable =>
        throw ct
      case t: Throwable =>
        logError(s"Uncaught exception in thread ${Thread.currentThread().getName}", t)
        throw t
    }
  }

  /**
    * Execute a block of code, then a finally block, but if exceptions happen in
    * the finally block, do not suppress the original exception.
    *
    * This is primarily an issue with `finally { out.close() }` blocks, where
    * close needs to be called to clean up `out`, but if an exception happened
    * in `out.write`, it's likely `out` may be corrupted and `out.close` will
    * fail as well. This would then suppress the original/likely more meaningful
    * exception from the original `out.write` call.
    */
  def tryWithSafeFinally[T](block: => T)(finallyBlock: => Unit): T = {
    var originalThrowable: Throwable = null
    try {
      block
    } catch {
      case t: Throwable =>
        // Purposefully not using NonFatal, because even fatal exceptions
        // we don't want to have our finallyBlock suppress
        originalThrowable = t
        throw originalThrowable
    } finally {
      try {
        finallyBlock
      } catch {
        case t: Throwable =>
          if (originalThrowable != null) {
            originalThrowable.addSuppressed(t)
            logWarning(s"Suppressing exception in finally: " + t.getMessage, t)
            throw originalThrowable
          } else {
            throw t
          }
      }
    }
  }

  /**
    * Terminates a process waiting for at most the specified duration.
    *
    * @return the process exit value if it was successfully terminated, else None
    */
  def terminateProcess(process: Process, timeoutMs: Long): Option[Int] = {
    // Politely destroy first
    process.destroy()

    if (waitForProcess(process, timeoutMs)) {
      // Successful exit
      Option(process.exitValue())
    } else {
      // Java 8 added a new API which will more forcibly kill the process. Use that if available.
      try {
        classOf[Process].getMethod("destroyForcibly").invoke(process)
      } catch {
        case _: NoSuchMethodException => return None // Not available; give up
        case NonFatal(e) => logWarning("Exception when attempting to kill process", e)
      }
      // Wait, again, although this really should return almost immediately
      if (waitForProcess(process, timeoutMs)) {
        Option(process.exitValue())
      } else {
        logWarning("Timed out waiting to forcibly kill process")
        None
      }
    }
  }

  /**
    * Wait for a process to terminate for at most the specified duration.
    *
    * @return whether the process actually terminated before the given timeout.
    */
  def waitForProcess(process: Process, timeoutMs: Long): Boolean = {
    try {
      // Use Java 8 method if available
      classOf[Process].getMethod("waitFor", java.lang.Long.TYPE, classOf[TimeUnit])
        .invoke(process, timeoutMs.asInstanceOf[java.lang.Long], TimeUnit.MILLISECONDS)
        .asInstanceOf[Boolean]
    } catch {
      case _: NoSuchMethodException =>
        // Otherwise implement it manually
        var terminated = false
        val startTime = System.currentTimeMillis
        while (!terminated) {
          try {
            process.exitValue()
            terminated = true
          } catch {
            case e: IllegalThreadStateException =>
              // Process not terminated yet
              if (System.currentTimeMillis - startTime > timeoutMs) {
                return false
              }
              Thread.sleep(100)
          }
        }
        true
    }
  }
}

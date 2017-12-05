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
//FROM org.apache.spark.util.logging.FileAppender

package org.wbq.common.util.logging

import java.io.{File, FileOutputStream, IOException, InputStream}

import org.wbq.common.util.{Logging, Utils}

/**
  * Continuously appends the data from an input stream into the given file.
  */
class FileAppender(inputStream: InputStream, file: File, bufferSize: Int = 8192)
  extends Logging {
  @volatile private var outputStream: FileOutputStream = null
  @volatile private var markedForStop = false     // has the appender been asked to stopped

  // Thread that reads the input stream and writes to file
  private val writingThread = new Thread("File appending thread for " + file) {
    setDaemon(true)
    override def run() {
      Utils.logUncaughtExceptions {
        appendStreamToFile()
      }
    }
  }
  writingThread.start()

  /**
    * Wait for the appender to stop appending, either because input stream is closed
    * or because of any error in appending
    */
  def awaitTermination() {
    writingThread.join()
  }

  /** Stop the appender */
  def stop() {
    markedForStop = true
  }

  /** Continuously read chunks from the input stream and append to the file */
  protected def appendStreamToFile() {
    try {
      logDebug("Started appending thread")
      Utils.tryWithSafeFinally {
        openFile()
        val buf = new Array[Byte](bufferSize)
        var n = 0
        while (!markedForStop && n != -1) {
          try {
            n = inputStream.read(buf)
          } catch {
            // An InputStream can throw IOException during read if the stream is closed
            // asynchronously, so once appender has been flagged to stop these will be ignored
            case _: IOException if markedForStop =>  // do nothing and proceed to stop appending
          }
          if (n > 0) {
            appendToFile(buf, n)
          }
        }
      } {
        closeFile()
      }
    } catch {
      case e: Exception =>
        logError(s"Error writing stream to file $file", e)
    }
  }

  /** Append bytes to the file output stream */
  protected def appendToFile(bytes: Array[Byte], len: Int) {
    if (outputStream == null) {
      openFile()
    }
    outputStream.write(bytes, 0, len)
  }

  /** Open the file output stream */
  protected def openFile() {
    outputStream = new FileOutputStream(file, false)
    logDebug(s"Opened file $file")
  }

  /** Close the file output stream */
  protected def closeFile() {
    outputStream.flush()
    outputStream.close()
    logDebug(s"Closed file $file")
  }
}

object FileAppender extends Logging {
  /** Create the right appender based on Spark configuration */
  def apply(inputStream: InputStream, file: File): FileAppender = {
    new FileAppender(inputStream, file)
  }
}



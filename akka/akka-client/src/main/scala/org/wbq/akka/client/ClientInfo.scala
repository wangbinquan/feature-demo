package org.wbq.akka.client

import akka.actor.ActorRef

object ClientInfo {
  var runId: String = null
  var taskId: String = null
  var masterUrl: String = null
  var masterRef: ActorRef = null

}

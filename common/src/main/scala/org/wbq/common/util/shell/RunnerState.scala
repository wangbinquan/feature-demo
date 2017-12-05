package org.wbq.common.util.shell

object RunnerState extends Enumeration {

  val LAUNCHING, RUNNING, KILLED, FAILED, LOST, EXITED = Value

  type RunnerState = Value

  def isFinished(state: RunnerState): Boolean = Seq(KILLED, FAILED, LOST, EXITED).contains(state)
}
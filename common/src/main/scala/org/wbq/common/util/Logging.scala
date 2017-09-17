package org.wbq.common.util

import org.slf4j.LoggerFactory

trait Logging {
  protected final val LOG = LoggerFactory.getLogger(getClass)

}

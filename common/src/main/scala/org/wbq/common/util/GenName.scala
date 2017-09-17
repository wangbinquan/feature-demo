package org.wbq.common.util

import java.text.SimpleDateFormat
import java.util.{Date, UUID}

object GenName {
  private final val timeFormater = new SimpleDateFormat("yyyyMMddHHmmssSSS")
  private final def now = timeFormater.format(new Date())
  private final def uuid = UUID.randomUUID().toString.replace("-", "")
  final def genName(prifix: String) = s"${prifix}_${uuid}_$now"

}

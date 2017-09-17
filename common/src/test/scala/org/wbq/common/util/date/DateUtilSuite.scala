package org.wbq.common.util.date

import java.sql.Timestamp

import org.junit.Assert
import org.scalatest.FunSuite
import org.slf4j.LoggerFactory

class DateUtilSuite extends FunSuite {
  private val LOG = LoggerFactory.getLogger(getClass)

  test("dateMatcher") {
    val cronExprs = Array("14 56 23 25 6 ? 2017")
    val matcher = DateUtil.getDateMatcher(cronExprs)
    Assert.assertTrue(matcher.isAllMatch(DateUtil.dateOf(2017, 6, 25, 23, 56, 14, 999999999)))
  }

}

package org.wbq.common.util.date

import org.junit.Assert
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.wbq.common.util.Logging

@RunWith(classOf[JUnitRunner])
class DateUtilSuite extends FunSuite with Logging {
  test("dateMatcher") {
    val cronExprs = Array("14 56 23 25 6 ? 2017")
    val matcher = DateUtil.getDateMatcher(cronExprs)
    Assert.assertTrue(matcher.isAllMatch(DateUtil.dateOf(2017, 6, 25, 23, 56, 14, 999999999)))
  }

}

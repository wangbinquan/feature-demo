package org.wbq.common.util

import org.junit.Assert
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.slf4j.LoggerFactory

@RunWith(classOf[JUnitRunner])
class GenNameSuite extends FunSuite with Logging {
  test("genName") {
    val name = GenName.genName("test")
    LOG.info(s"GenName: $name")
    assert(name.startsWith("test"))
    val totalLength = 4 + 1 + 32 + 1 + 17 //55
    Assert.assertEquals(name.length, totalLength)
  }

}

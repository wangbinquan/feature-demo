package org.wbq

import org.scalatest.FunSuite

class ScalaClassSuite extends FunSuite {
  test("callScalaClass"){
    assert((new ScalaClass).hello().equals("hello scala"))
  }
}

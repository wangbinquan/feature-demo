package org.wbq

import org.scalatest.FunSuite

class JavaClassSuite extends FunSuite {
  test("callJavaClass"){
    val javaCallScala = new JavaCallScala
    assert(javaCallScala.callScalaClass().equals("hello scala"))
    assert(javaCallScala.callScalaObject().equals("scala object"))
  }

}

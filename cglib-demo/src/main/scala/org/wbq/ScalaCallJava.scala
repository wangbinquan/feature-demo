package org.wbq

class ScalaCallJava{
  def callJava(): String ={
//    config as scala compile first
//    JavaClass.staticHello()
    "java static"
  }
}

object ScalaCallJava {
//  config as scala compile first
//  val javaClass = new JavaClass
  def callJava(): String ={
//    javaClass.hello()
  "hello java"
  }
}

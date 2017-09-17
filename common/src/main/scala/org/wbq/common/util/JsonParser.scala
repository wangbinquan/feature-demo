package org.wbq.common.util

import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object JsonParser {
  private final val objectMapper = new ObjectMapper()
  objectMapper.registerModule(DefaultScalaModule)
  objectMapper.findAndRegisterModules()

  def writeClass(obj: Object): String = {
    objectMapper.writeValueAsString(obj)
  }

  def readClass[T](json: String, clazz: Class[T]): T = {
    objectMapper.readValue(json, clazz)
  }

  def readClass[T](json: String, typeReference: TypeReference[T]): T = {
    objectMapper.readValue(json, typeReference)
  }

}

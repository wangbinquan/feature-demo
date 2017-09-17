package org.wbq.antlr4.node.function

import org.wbq.antlr4.node.DataType.DataType
import org.wbq.antlr4.node.TreeNode

/**
  * Created by Administrator on 2017/1/21 0021.
  */
case class FunctionFromResourceFile(funcName: String, paras: java.util.List[TreeNode], private val codePattern: String, private val parasNum: Int) extends Function {
  override val getCode: String = {
    var codeOut = codePattern
    var index = 1
    while (index <= parasNum) {
      codeOut = codeOut.replaceAll("\\$" + index, paras.get(index).getCode)
      index += 1
    }
    codeOut
  }

  override val getType: DataType = ???


}

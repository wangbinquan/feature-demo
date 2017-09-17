package org.wbq.antlr4.node.function

import org.wbq.antlr4.node.TreeNode

/**
  * Created by Administrator on 2017/1/21 0021.
  */
object FunctionFactory {
  def functionOf(funcName: String, paras: java.util.List[TreeNode]): Function = {
    funcName.trim match {
      case "SIN" => {
        assert(paras.size() == 1)
        Sin(paras.get(0))
      }
    }
  }

}

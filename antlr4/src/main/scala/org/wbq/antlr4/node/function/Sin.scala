package org.wbq.antlr4.node.function

import org.wbq.antlr4.node.DataType.DataType
import org.wbq.antlr4.node.{DataType, TreeNode}

/**
  * Created by Administrator on 2017/1/21 0021.
  */
case class Sin(expr: TreeNode) extends Function{
  override val getCode: String = "sin(" + expr.getCode + ")"
  override val getType: DataType = DataType.NumCol

}

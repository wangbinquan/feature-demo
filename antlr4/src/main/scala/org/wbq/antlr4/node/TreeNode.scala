package org.wbq.antlr4.node

import org.wbq.antlr4.node.DataType.DataType

/**
  * Created by Administrator on 2017/1/21 0021.
  */
trait TreeNode {
  val getCode: String
  val getType: DataType
}

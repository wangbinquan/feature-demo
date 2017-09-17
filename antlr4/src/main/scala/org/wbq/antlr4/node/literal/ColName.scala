package org.wbq.antlr4.node.literal
import org.wbq.antlr4.node.DataType
import org.wbq.antlr4.node.DataType.DataType

/**
  * Created by Administrator on 2017/1/21 0021.
  */
case class ColName(colName: String) extends Literal{
  override val getCode: String = "$\"" + colName + "\""
  lazy override val getType: DataType = null
}

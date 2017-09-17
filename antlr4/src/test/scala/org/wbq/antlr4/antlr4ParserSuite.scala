package org.wbq.antlr4

import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}
import org.scalatest.FunSuite
import org.wbq.antlr4.node.TreeNode
import org.wbq.common.util.Logging

/**
  * Created by Administrator on 2017/1/21 0021.
  */
class antlr4ParserSuite extends FunSuite with Logging {
  test("Add"){
    val input = new ANTLRInputStream("a + 2")
    val lexer = new FunctionExprParserLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new FunctionExprParserParser(tokens)
    val tree = parser.exp()
    val mv = new ExprVistor()
    val res = mv.visit(tree).asInstanceOf[TreeNode]
    assert(res.getCode == """a+2.0""")
  }

  test("Sub"){
    val input = new ANTLRInputStream("[a] - 2")
    val lexer = new FunctionExprParserLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new FunctionExprParserParser(tokens)
    val tree = parser.exp()
    val mv = new ExprVistor()
    val res = mv.visit(tree).asInstanceOf[TreeNode]
    assert(res.getCode == """$"a"-2.0""")
  }

  test("mix1"){
    val input = new ANTLRInputStream("[a] - 2 * [de] + [j3] / ed - fs")
    val lexer = new FunctionExprParserLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new FunctionExprParserParser(tokens)
    val tree = parser.exp()
    val mv = new ExprVistor()
    val res = mv.visit(tree).asInstanceOf[TreeNode]
    assert(res.getCode == """$"a"-2.0*$"de"+$"j3"/ed-fs""")
  }

  test("sin func"){
    val input = new ANTLRInputStream("[a] - SIN(2 + [de] * [j3]) / ed - fs")
    val lexer = new FunctionExprParserLexer(input)
    val tokens = new CommonTokenStream(lexer)
    val parser = new FunctionExprParserParser(tokens)
    val tree = parser.exp()
    val mv = new ExprVistor()
    val res = mv.visit(tree).asInstanceOf[TreeNode]
    assert(res.getCode == """$"a"-sin(2.0+$"de"*$"j3")/ed-fs""")
  }

}

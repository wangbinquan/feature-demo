package org.wbq.antlr4;

import org.wbq.antlr4.node.TreeNode;
import org.wbq.antlr4.node.function.*;
import org.wbq.antlr4.node.literal.ColName;
import org.wbq.antlr4.node.literal.Num;
import org.wbq.antlr4.node.literal.Str;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/1/21 0021.
 */
public class ExprVistor extends FunctionExprParserBaseVisitor {
    @Override
    public TreeNode visitExp(FunctionExprParserParser.ExpContext ctx) {
        if (ctx.getChildCount() == 1){
            return (TreeNode)visit(ctx.getChild(0));
        }
        else if (ctx.getChildCount() == 3){
            switch (ctx.op.getType()){
                case FunctionExprParserParser.ADD :
                    return new Add((TreeNode) visit(ctx.getChild(0)), (TreeNode) visit(ctx.getChild(2)));
                case FunctionExprParserParser.SUB :
                    return new Sub((TreeNode) visit(ctx.getChild(0)), (TreeNode) visit(ctx.getChild(2)));
            }
        }
        throw new RuntimeException();
    }

    @Override
    public TreeNode visitTerm(FunctionExprParserParser.TermContext ctx) {
        if (ctx.getChildCount() == 1){
            return (TreeNode)visit(ctx.getChild(0));
        }
        else if (ctx.getChildCount() == 3){
            switch (ctx.op.getType()){
                case FunctionExprParserParser.MUL :
                    return new Mul((TreeNode) visit(ctx.getChild(0)), (TreeNode) visit(ctx.getChild(2)));
                case FunctionExprParserParser.DIV :
                    return new Div((TreeNode) visit(ctx.getChild(0)), (TreeNode) visit(ctx.getChild(2)));
            }
        }
        throw new RuntimeException();
    }

    @Override
    public TreeNode visitColumnName(FunctionExprParserParser.ColumnNameContext ctx) {
        return new ColName(ctx.colName.getText());
    }

    @Override
    public TreeNode visitChar(FunctionExprParserParser.CharContext ctx) {
        return new Str(ctx.varName.getText().trim());
    }

    @Override
    public TreeNode visitNumber(FunctionExprParserParser.NumberContext ctx) {
        return new Num(Double.parseDouble(ctx.number.getText()));
    }

    @Override
    public TreeNode visitNgNumber(FunctionExprParserParser.NgNumberContext ctx) {
        return new Num(0 - Double.parseDouble(ctx.number.getText()));
    }

    @Override
    public TreeNode visitFunc(FunctionExprParserParser.FuncContext ctx) {
        return (Function)visitFunCall(ctx.funCall());
    }

    @Override
    public TreeNode visitParenthesis(FunctionExprParserParser.ParenthesisContext ctx) {
        return visitExp(ctx.exp());
    }

    @Override
    public TreeNode visitFunCall(FunctionExprParserParser.FunCallContext ctx) {
        return FunctionFactory.functionOf(ctx.funName.getText(), visitParams(ctx.params()));
    }

    @Override
    public List<TreeNode> visitParams(FunctionExprParserParser.ParamsContext ctx) {
        if (ctx.getChildCount() == 1){
            List<TreeNode> paraOut = new ArrayList<TreeNode>();
            paraOut.add((TreeNode) visit(ctx.exp()));
            return paraOut;
        }
        else if (ctx.getChildCount() == 2){
            List<TreeNode> parasOut = new ArrayList<TreeNode>();
            parasOut.add((TreeNode) visit(ctx.exp()));
            parasOut.addAll(visitParams(ctx.params()));
            return parasOut;
        }
        throw new RuntimeException();
    }
}

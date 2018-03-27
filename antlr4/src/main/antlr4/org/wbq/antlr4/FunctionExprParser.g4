grammar FunctionExprParser;

@header {
package org.wbq.antlr4;
}

exp: term
| exp op = (ADD | SUB) exp;

term: factor
|term op = (MUL | DIV) term;

factor: '[' colName = CHARACTER ']'     #columnName
|varName=CHARACTER                      #char
|number=NUMBER                          #number
|'-' number=NUMBER                      #ngNumber
|funCall                                #func
|'(' exp ')'                            #parenthesis
;

funCall: funName=CHARACTER '(' params ')';

params: exp
|exp ',' params;


ADD : '+' ;
SUB : '-' ;
MUL : '*' ;
DIV : '/' ;
CHARACTER: [A-Za-z][A-Za-z0-9_ ]*;
NUMBER: [0-9]+([\.][0-9]*)?;
WS : [ \t\n\r]+ -> skip;
grammar MOL;

@header {
    package main.grammar;
}

// 1. TOP-LEVEL STRUCTURE

program: declaration+ EOF;

declaration
    : moduleDecl
    | structDecl
    ;

moduleDecl
    : 'module' ID ('includes' ID (',' ID)*)? 'begin' memberDecl* 'end'
    ;

structDecl
    : 'struct' ID 'begin' fieldDecl* 'end'
    ;

memberDecl
    : fieldDecl
    | methodDecl
    | constructorDecl
    ;


// 2. CLASS MEMBERS & VARIABLES

fieldDecl
    : accessModifier? ('mut')? type ID ';'
    ;

methodDecl
    : accessModifier? type ID '(' paramList? ')' block
    ;

constructorDecl
    : accessModifier? ID '(' paramList? ')' block
    ;

accessModifier
    : 'public'
    | 'private'
    ;

paramList
    : param (',' param)*
    ;

param
    : ('mut')? type ID
    ;


// 3. STATEMENTS & CONTROL FLOW

block
    : 'begin' statement* 'end'
    ;

statement
    : block                                                                 # BlockStmt
    | varDeclStmt                                                           # VarDeclarationStmt
    | expression '=' expression ';'                                         # AssignmentStmt
    | 'if' '(' expression ')' block ('elif' '(' expression ')' block)* ('else' block)? # IfStmt
    | 'while' '(' expression ')' block                                      # WhileStmt
    | 'for' '(' forInit? ';' expression? ';' forUpdate? ')' block           # ForStmt
    | 'return' expression? ';'                                              # ReturnStmt
    | 'break' ';'                                                           # BreakStmt
    | 'continue' ';'                                                        # ContinueStmt
    | 'output' expression ';'                                               # OutputStmt
    | expression ';'                                                        # ExprStmt
    ;

varDeclStmt
    : ('mut')? type ('(' argList? ')')? ID ('=' expression)? ';'
    ;

forInit
    : ('mut')? type ID ('=' expression)?                         # ForInitDecl
    | expression '=' expression (',' expression '=' expression)* # ForInitAssign
    ;

forUpdate
    : expression '=' expression (',' expression '=' expression)* # ForUpdateAssign
    ;


// 4. EXPRESSIONS

argList
    : expression (',' expression)*
    ;

expression
    : primary                                             # PrimaryExpr
    | expression '.' ID                                   # MemberAccessExpr
    | expression '(' argList? ')'                         # CallExpr
    | ('+' | '-') expression                              # UnaryExpr
    | expression ('*' | '/' | '%') expression             # MultExpr
    | expression ('+' | '-') expression                   # AddExpr
    | expression ('<' | '>' | '<=' | '>=') expression     # RelExpr
    | expression ('==' | '!=') expression                 # EqExpr
    ;

primary
    : ID
    | INT
    | FLOAT_LITERAL
    | BOOL_LITERAL
    | 'this'
    | '(' expression ')'
    ;

type
    : 'int' | 'float' | 'double' | 'char' | 'bool' | 'void' | ID
    ;


// 5. LEXER RULES (TOKENS)

BOOL_LITERAL : 'true' | 'false';

ID : [a-zA-Z_][a-zA-Z0-9_]*;
INT : [0-9]+;
FLOAT_LITERAL : [0-9]+ '.' [0-9]+;

WHITE_SPACE : [ \t\r\n]+ -> skip;
COMMENT : '//' ~[\r\n]* -> skip;
BLOCK_COMMENT : '/*' .*? '*/' -> skip;
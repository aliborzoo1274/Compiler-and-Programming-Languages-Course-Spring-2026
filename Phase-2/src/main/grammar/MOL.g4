grammar MOL;

@header {
    package main.grammar;

    import java.util.ArrayList;
    import java.util.List;

    import main.ast.nodes.*;
    import main.ast.nodes.decl.*;
    import main.ast.nodes.expr.*;
    import main.ast.nodes.stmt.*;
}

@members {
    private BinaryOperator toBinaryOperator(String text) {
        switch (text) {
            case "*": return BinaryOperator.MULT;
            case "/": return BinaryOperator.DIV;
            case "%": return BinaryOperator.MOD;
            case "+": return BinaryOperator.PLUS;
            case "-": return BinaryOperator.MINUS;
            case "<": return BinaryOperator.LT;
            case ">": return BinaryOperator.GT;
            case "<=": return BinaryOperator.LE;
            case ">=": return BinaryOperator.GE;
            case "==": return BinaryOperator.EQ;
            case "!=": return BinaryOperator.NE;
            default: return null;
        }
    }
}

// 1. TOP-LEVEL STRUCTURE

program returns [Program ASTNode]
    : { $ASTNode = new Program(); }
        (d=declaration { if ($d.ASTNode != null) $ASTNode.addDeclaration($d.ASTNode); })+
        EOF
    ;

declaration returns [Declaration ASTNode]
    : m=moduleDecl { $ASTNode = $m.ASTNode; }
    | s=structDecl { $ASTNode = $s.ASTNode; }
    ;

moduleDecl returns [ModuleDeclaration ASTNode]
    : 'module' id=ID
      {
          $ASTNode = new ModuleDeclaration();
          $ASTNode.setName($id.getText());
          $ASTNode.setLine($id.getLine());
      }
      ('includes' inc=ID { $ASTNode.addInclude($inc.getText()); }
          (',' inc2=ID { $ASTNode.addInclude($inc2.getText()); })*
      )?
      'begin'
      (m=memberDecl { if ($m.ASTNode != null) $ASTNode.addMember($m.ASTNode); })*
      'end'
    ;

structDecl returns [StructDeclaration ASTNode]
    : 'struct' id=ID
      {
          $ASTNode = new StructDeclaration();
          $ASTNode.setName($id.getText());
          $ASTNode.setLine($id.getLine());
      }
      'begin'
      (f=fieldDecl { $ASTNode.addField($f.ASTNode); })*
      'end'
    ;

memberDecl returns [Declaration ASTNode]
    : f=fieldDecl { $ASTNode = $f.ASTNode; }
    | m=methodDecl { $ASTNode = $m.ASTNode; }
    | c=constructorDecl { $ASTNode = $c.ASTNode; }
    ;


// 2. CLASS MEMBERS & VARIABLES

fieldDecl returns [FieldDeclaration ASTNode]
    : am=accessModifier? m='mut'? t=type id=ID ';'
      {
          $ASTNode = new FieldDeclaration();
          $ASTNode.setLine($id.getLine());
          $ASTNode.setAccessModifier($am.ASTNode);
          $ASTNode.setMutable($m != null);
          $ASTNode.setType($t.ASTNode);
          $ASTNode.setName($id.getText());
      }
    ;

methodDecl returns [MethodDeclaration ASTNode]
    : am=accessModifier? t=type id=ID '(' pl=paramList? ')' b=block
      {
          $ASTNode = new MethodDeclaration();
          $ASTNode.setLine($id.getLine());
          $ASTNode.setAccessModifier($am.ASTNode);
          $ASTNode.setReturnType($t.ASTNode);
          $ASTNode.setName($id.getText());
          if ($ctx.pl != null) $ASTNode.setParams($pl.ASTNode);
          $ASTNode.setBody($b.ASTNode);
      }
    ;

constructorDecl returns [ConstructorDeclaration ASTNode]
    : am=accessModifier? id=ID '(' pl=paramList? ')' b=block
      {
          $ASTNode = new ConstructorDeclaration();
          $ASTNode.setLine($id.getLine());
          $ASTNode.setAccessModifier($am.ASTNode);
          $ASTNode.setName($id.getText());
          if ($pl.ASTNode != null) $ASTNode.setParams($pl.ASTNode);
          $ASTNode.setBody($b.ASTNode);
      }
    ;

accessModifier returns [AccessModifier ASTNode]
    : 'public' { $ASTNode = AccessModifier.PUBLIC; }
    | 'private' { $ASTNode = AccessModifier.PRIVATE; }
    ;

paramList returns [List<Param> ASTNode]
        : { $ASTNode = new ArrayList<>(); }
            p=param { $ASTNode.add($p.ASTNode); }
            (',' p2=param { $ASTNode.add($p2.ASTNode); })*
        ;

param returns [Param ASTNode]
    : m='mut'? t=type id=ID
      {
          $ASTNode = new Param();
          $ASTNode.setLine($id.getLine());
          $ASTNode.setMutable($m != null);
          $ASTNode.setType($t.ASTNode);
          $ASTNode.setName($id.getText());
      }
    ;


// 3. STATEMENTS & CONTROL FLOW

block returns [BlockStmt ASTNode]
    : b='begin'
      {
          $ASTNode = new BlockStmt();
          $ASTNode.setLine($b.getLine());
      }
      (s=statement { if ($s.ASTNode != null) $ASTNode.addStatement($s.ASTNode); })*
      'end'
    ;

statement returns [Statement ASTNode]
        : b=block
            { $ASTNode = $b.ASTNode; }                                             # BlockStmt
        | v=varDeclStmt
            { $ASTNode = $v.ASTNode; }                                             # VarDeclarationStmt
        | e1=expression '=' e2=expression ';'
            {
                    Assignment assign = new Assignment();
                    assign.setLine($start.getLine());
                    assign.setLeft($e1.ASTNode);
                    assign.setRight($e2.ASTNode);
                    AssignmentStmt stmt = new AssignmentStmt();
                    stmt.setLine($start.getLine());
                    stmt.setAssignment(assign);
                    $ASTNode = stmt;
            }                                                                     # AssignmentStmt
        | 'if' '(' c=expression ')' tb=block
            {
                    IfStmt ifStmt = new IfStmt();
                    ifStmt.setLine($start.getLine());
                    ifStmt.setCondition($c.ASTNode);
                    ifStmt.setThenBlock($tb.ASTNode);
            }
            ('elif' '(' ec=expression ')' eb=block
                    {
                            ElseIfClause clause = new ElseIfClause();
                            clause.setLine($start.getLine());
                            clause.setCondition($ec.ASTNode);
                            clause.setThenBlock($eb.ASTNode);
                            ifStmt.addElseIfClause(clause);
                    }
            )*
            ('else' eb2=block { ifStmt.setElseBlock($eb2.ASTNode); })?
            { $ASTNode = ifStmt; }                                                 # IfStmt
        | 'while' '(' c=expression ')' b=block
            {
                    WhileStmt stmt = new WhileStmt();
                    stmt.setLine($start.getLine());
                    stmt.setCondition($c.ASTNode);
                    stmt.setBody($b.ASTNode);
                    $ASTNode = stmt;
            }                                                                     # WhileStmt
        | 'for' '(' fi=forInit? ';' c=expression? ';' fu=forUpdate? ')' b=block
            {
                    ForStmt stmt = new ForStmt();
                    stmt.setLine($start.getLine());
                    stmt.setInit($fi.ASTNode);
                    stmt.setCondition($c.ASTNode);
                    stmt.setUpdate($fu.ASTNode);
                    stmt.setBody($b.ASTNode);
                    $ASTNode = stmt;
            }                                                                     # ForStmt
        | r='return' e=expression? ';'
            {
                    ReturnStmt stmt = new ReturnStmt();
                    stmt.setLine($r.getLine());
                    if ($e.ASTNode != null) stmt.setValue($e.ASTNode);
                    $ASTNode = stmt;
            }                                                                     # ReturnStmt
        | b='break' ';'
            {
                    BreakStmt stmt = new BreakStmt();
                    stmt.setLine($b.getLine());
                    $ASTNode = stmt;
            }                                                                     # BreakStmt
        | c='continue' ';'
            {
                    ContinueStmt stmt = new ContinueStmt();
                    stmt.setLine($c.getLine());
                    $ASTNode = stmt;
            }                                                                     # ContinueStmt
        | o='output' e=expression ';'
            {
                    OutputStmt stmt = new OutputStmt();
                    stmt.setLine($o.getLine());
                    stmt.setValue($e.ASTNode);
                    $ASTNode = stmt;
            }                                                                     # OutputStmt
        | e=expression ';'
            {
                    ExprStmt stmt = new ExprStmt();
                    stmt.setLine($start.getLine());
                    stmt.setExpression($e.ASTNode);
                    $ASTNode = stmt;
            }                                                                     # ExprStmt
        ;

varDeclStmt returns [VarDeclarationStmt ASTNode]
    : m='mut'? t=type ('(' al=argList? ')')? id=ID ('=' e=expression)? ';'
      {
          $ASTNode = new VarDeclarationStmt();
          $ASTNode.setLine($id.getLine());
          $ASTNode.setMutable($m != null);
          $ASTNode.setType($t.ASTNode);
          $ASTNode.setName($id.getText());
          if ($ctx.al != null) {
              $ASTNode.setHasConstructorCall(true);
              $ASTNode.setConstructorArgs($al.ASTNode);
          }
          if ($ctx.e != null) $ASTNode.setInitializer($e.ASTNode);
      }
    ;

forInit returns [ForInit ASTNode]
    : m='mut'? t=type id=ID ('=' e=expression)?
      {
          ForInitDecl init = new ForInitDecl();
          init.setLine($id.getLine());
          init.setMutable($m != null);
          init.setType($t.ASTNode);
          init.setName($id.getText());
          if ($e.ASTNode != null) init.setInitializer($e.ASTNode);
          $ASTNode = init;
      }                                                                     # ForInitDecl
    | e1=expression '=' e2=expression (',' e3+=expression '=' e4+=expression)*
      {
          ForInitAssign init = new ForInitAssign();
          init.setLine($start.getLine());
          Assignment first = new Assignment();
          first.setLine($start.getLine());
          first.setLeft($e1.ASTNode);
          first.setRight($e2.ASTNode);
          init.addAssignment(first);
          for (int i = 0; i < $e3.size(); i++) {
              Assignment assign = new Assignment();
              assign.setLine($e3.get(i).start.getLine());
              assign.setLeft($e3.get(i).ASTNode);
              assign.setRight($e4.get(i).ASTNode);
              init.addAssignment(assign);
          }
          $ASTNode = init;
      }                                                                     # ForInitAssign
    ;

forUpdate returns [ForUpdateAssign ASTNode]
    : e1=expression '=' e2=expression (',' e3+=expression '=' e4+=expression)*
      {
          ForUpdateAssign update = new ForUpdateAssign();
          update.setLine($start.getLine());
          Assignment first = new Assignment();
          first.setLine($start.getLine());
          first.setLeft($e1.ASTNode);
          first.setRight($e2.ASTNode);
          update.addAssignment(first);
          for (int i = 0; i < $e3.size(); i++) {
              Assignment assign = new Assignment();
              assign.setLine($e3.get(i).start.getLine());
              assign.setLeft($e3.get(i).ASTNode);
              assign.setRight($e4.get(i).ASTNode);
              update.addAssignment(assign);
          }
          $ASTNode = update;
      }                                                                     # ForUpdateAssign
    ;


// 4. EXPRESSIONS

argList returns [List<Expression> ASTNode]
        : { $ASTNode = new ArrayList<>(); }
            e=expression { $ASTNode.add($e.ASTNode); }
            (',' e2=expression { $ASTNode.add($e2.ASTNode); })*
        ;

expression returns [Expression ASTNode]
        : p=primary
            { $ASTNode = $p.ASTNode; }                                   # PrimaryExpr
        | e=expression '.' id=ID
            {
                    MemberAccessExpr expr = new MemberAccessExpr();
                    expr.setLine($id.getLine());
                    expr.setInstance($e.ASTNode);
                    expr.setMemberName($id.getText());
                    $ASTNode = expr;
            }                                                               # MemberAccessExpr
        | e=expression '(' al=argList? ')'
            {
                    CallExpr expr = new CallExpr();
                    expr.setLine($start.getLine());
                    expr.setCallee($e.ASTNode);
                    if ($al.ASTNode != null) expr.setArgs($al.ASTNode);
                    $ASTNode = expr;
            }                                                               # CallExpr
        | op=('+' | '-') e=expression
            {
                    UnaryExpr expr = new UnaryExpr();
                    expr.setLine($start.getLine());
                    expr.setOperator("+".equals($op.getText()) ? UnaryOperator.PLUS : UnaryOperator.MINUS);
                    expr.setOperand($e.ASTNode);
                    $ASTNode = expr;
            }                                                               # UnaryExpr
        | e1=expression op=('*' | '/' | '%') e2=expression
            {
                    BinaryExpr expr = new BinaryExpr();
                    expr.setLine($start.getLine());
                    expr.setLeft($e1.ASTNode);
                    expr.setRight($e2.ASTNode);
                    expr.setOperator(toBinaryOperator($op.getText()));
                    $ASTNode = expr;
            }                                                               # MultExpr
        | e1=expression op=('+' | '-') e2=expression
            {
                    BinaryExpr expr = new BinaryExpr();
                    expr.setLine($start.getLine());
                    expr.setLeft($e1.ASTNode);
                    expr.setRight($e2.ASTNode);
                    expr.setOperator(toBinaryOperator($op.getText()));
                    $ASTNode = expr;
            }                                                               # AddExpr
        | e1=expression op=('<' | '>' | '<=' | '>=') e2=expression
            {
                    BinaryExpr expr = new BinaryExpr();
                    expr.setLine($start.getLine());
                    expr.setLeft($e1.ASTNode);
                    expr.setRight($e2.ASTNode);
                    expr.setOperator(toBinaryOperator($op.getText()));
                    $ASTNode = expr;
            }                                                               # RelExpr
        | e1=expression op=('==' | '!=') e2=expression
            {
                    BinaryExpr expr = new BinaryExpr();
                    expr.setLine($start.getLine());
                    expr.setLeft($e1.ASTNode);
                    expr.setRight($e2.ASTNode);
                    expr.setOperator(toBinaryOperator($op.getText()));
                    $ASTNode = expr;
            }                                                               # EqExpr
        ;

primary returns [Expression ASTNode]
        : id=ID
            {
                    Identifier expr = new Identifier($id.getText());
                    expr.setLine($id.getLine());
                    $ASTNode = expr;
            }
        | i=INT
            {
                    IntLiteral expr = new IntLiteral(Integer.parseInt($i.getText()));
                    expr.setLine($i.getLine());
                    $ASTNode = expr;
            }
        | f=FLOAT_LITERAL
            {
                    FloatLiteral expr = new FloatLiteral(Double.parseDouble($f.getText()));
                    expr.setLine($f.getLine());
                    $ASTNode = expr;
            }
        | b=BOOL_LITERAL
            {
                    BoolLiteral expr = new BoolLiteral("true".equals($b.getText()));
                    expr.setLine($b.getLine());
                    $ASTNode = expr;
            }
        | t='this'
            {
                    ThisExpr expr = new ThisExpr();
                    expr.setLine($t.getLine());
                    $ASTNode = expr;
            }
        | '(' e=expression ')'
            { $ASTNode = $e.ASTNode; }
        ;

type returns [Type ASTNode]
    : t=('int' | 'float' | 'double' | 'char' | 'bool' | 'void' | ID)
      {
          $ASTNode = new Type($t.getText());
          $ASTNode.setLine($t.getLine());
      }
    ;


// 5. LEXER RULES (TOKENS)

BOOL_LITERAL : 'true' | 'false';

ID : [a-zA-Z_][a-zA-Z0-9_]*;
INT : [0-9]+;
FLOAT_LITERAL : [0-9]+ '.' [0-9]+;

WHITE_SPACE : [ \t\r\n]+ -> skip;
COMMENT : '//' ~[\r\n]* -> skip;
BLOCK_COMMENT : '/*' .*? '*/' -> skip;
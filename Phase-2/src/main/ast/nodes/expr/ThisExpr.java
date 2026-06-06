package main.ast.nodes.expr;

import main.visitor.ASTVisitor;

public class ThisExpr extends Expression {
    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

package main.ast.nodes.stmt;

import main.ast.nodes.expr.Expression;
import main.visitor.ASTVisitor;

public class ExprStmt extends Statement {
    private Expression expression;

    public Expression getExpression() { return expression; }
    public void setExpression(Expression expression) { this.expression = expression; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

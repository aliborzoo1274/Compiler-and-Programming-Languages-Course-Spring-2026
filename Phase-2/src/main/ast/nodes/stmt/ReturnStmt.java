package main.ast.nodes.stmt;

import main.ast.nodes.expr.Expression;
import main.visitor.ASTVisitor;

public class ReturnStmt extends Statement {
    private Expression value;

    public Expression getValue() { return value; }
    public void setValue(Expression value) { this.value = value; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

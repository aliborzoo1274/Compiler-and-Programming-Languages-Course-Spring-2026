package main.ast.nodes.stmt;

import main.ast.nodes.expr.Expression;
import main.visitor.ASTVisitor;

public class WhileStmt extends Statement {
    private Expression condition;
    private BlockStmt body;

    public Expression getCondition() { return condition; }
    public void setCondition(Expression condition) { this.condition = condition; }
    public BlockStmt getBody() { return body; }
    public void setBody(BlockStmt body) { this.body = body; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

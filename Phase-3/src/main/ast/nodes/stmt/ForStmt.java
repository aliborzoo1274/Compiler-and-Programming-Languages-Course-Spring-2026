package main.ast.nodes.stmt;

import main.ast.nodes.expr.Expression;
import main.visitor.ASTVisitor;

public class ForStmt extends Statement {
    private ForInit init;
    private Expression condition;
    private ForUpdateAssign update;
    private BlockStmt body;

    public ForInit getInit() { return init; }
    public void setInit(ForInit init) { this.init = init; }
    public Expression getCondition() { return condition; }
    public void setCondition(Expression condition) { this.condition = condition; }
    public ForUpdateAssign getUpdate() { return update; }
    public void setUpdate(ForUpdateAssign update) { this.update = update; }
    public BlockStmt getBody() { return body; }
    public void setBody(BlockStmt body) { this.body = body; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

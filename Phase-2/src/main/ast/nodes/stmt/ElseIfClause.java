package main.ast.nodes.stmt;

import main.ast.nodes.Node;
import main.ast.nodes.expr.Expression;
import main.visitor.ASTVisitor;

public class ElseIfClause extends Node {
    private Expression condition;
    private BlockStmt thenBlock;

    public Expression getCondition() { return condition; }
    public void setCondition(Expression condition) { this.condition = condition; }
    public BlockStmt getThenBlock() { return thenBlock; }
    public void setThenBlock(BlockStmt thenBlock) { this.thenBlock = thenBlock; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

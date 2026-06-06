package main.ast.nodes.stmt;

import main.ast.nodes.expr.Expression;
import main.visitor.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class IfStmt extends Statement {
    private Expression condition;
    private BlockStmt thenBlock;
    private final List<ElseIfClause> elseIfClauses = new ArrayList<>();
    private BlockStmt elseBlock;

    public Expression getCondition() { return condition; }
    public void setCondition(Expression condition) { this.condition = condition; }
    public BlockStmt getThenBlock() { return thenBlock; }
    public void setThenBlock(BlockStmt thenBlock) { this.thenBlock = thenBlock; }
    public List<ElseIfClause> getElseIfClauses() { return elseIfClauses; }
    public void setElseIfClauses(List<ElseIfClause> elseIfClauses) {
        this.elseIfClauses.clear();
        if (elseIfClauses != null) {
            this.elseIfClauses.addAll(elseIfClauses);
        }
    }
    public void addElseIfClause(ElseIfClause elseIfClause) { this.elseIfClauses.add(elseIfClause); }
    public BlockStmt getElseBlock() { return elseBlock; }
    public void setElseBlock(BlockStmt elseBlock) { this.elseBlock = elseBlock; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

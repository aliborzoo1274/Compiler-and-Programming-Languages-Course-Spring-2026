package main.ast.nodes.stmt;

import main.visitor.ASTVisitor;

public class ContinueStmt extends Statement {
    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

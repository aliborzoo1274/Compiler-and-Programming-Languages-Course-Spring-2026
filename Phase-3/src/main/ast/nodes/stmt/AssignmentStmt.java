package main.ast.nodes.stmt;

import main.visitor.ASTVisitor;

public class AssignmentStmt extends Statement {
    private Assignment assignment;

    public Assignment getAssignment() { return assignment; }
    public void setAssignment(Assignment assignment) { this.assignment = assignment; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

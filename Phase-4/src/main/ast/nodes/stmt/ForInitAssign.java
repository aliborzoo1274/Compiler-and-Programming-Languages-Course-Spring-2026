package main.ast.nodes.stmt;

import main.visitor.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class ForInitAssign extends ForInit {
    private final List<Assignment> assignments = new ArrayList<>();

    public List<Assignment> getAssignments() { return assignments; }
    public void setAssignments(List<Assignment> assignments) {
        this.assignments.clear();
        if (assignments != null) {
            this.assignments.addAll(assignments);
        }
    }
    public void addAssignment(Assignment assignment) { this.assignments.add(assignment); }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

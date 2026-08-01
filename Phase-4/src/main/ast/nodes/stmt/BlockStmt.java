package main.ast.nodes.stmt;

import main.visitor.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class BlockStmt extends Statement {
    private final List<Statement> statements = new ArrayList<>();

    public List<Statement> getStatements() { return statements; }
    public void setStatements(List<Statement> statements) {
        this.statements.clear();
        if (statements != null) {
            this.statements.addAll(statements);
        }
    }
    public void addStatement(Statement statement) { this.statements.add(statement); }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

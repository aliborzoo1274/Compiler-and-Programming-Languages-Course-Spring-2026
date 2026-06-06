package main.ast.nodes.expr;

import main.visitor.ASTVisitor;

public class Identifier extends Expression {
    private String name;

    public Identifier() {
    }

    public Identifier(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

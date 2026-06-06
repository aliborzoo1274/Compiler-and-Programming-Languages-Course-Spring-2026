package main.ast.nodes;

import main.visitor.ASTVisitor;

public class Type extends Node {
    private String name;

    public Type() {
    }

    public Type(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

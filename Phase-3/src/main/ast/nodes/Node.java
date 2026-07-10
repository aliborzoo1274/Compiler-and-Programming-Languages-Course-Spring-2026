package main.ast.nodes;

import main.visitor.ASTVisitor;

public abstract class Node {
    private int line;

    public void setLine(int line) { this.line = line; }
    public int getLine() { return this.line; }

    public abstract <T> T accept(ASTVisitor<T> visitor);
}

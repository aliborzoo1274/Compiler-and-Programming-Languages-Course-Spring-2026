package main.ast.nodes.stmt;

import main.ast.nodes.Node;
import main.ast.nodes.expr.Expression;
import main.visitor.ASTVisitor;

public class Assignment extends Node {
    private Expression left;
    private Expression right;

    public Expression getLeft() { return left; }
    public void setLeft(Expression left) { this.left = left; }
    public Expression getRight() { return right; }
    public void setRight(Expression right) { this.right = right; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

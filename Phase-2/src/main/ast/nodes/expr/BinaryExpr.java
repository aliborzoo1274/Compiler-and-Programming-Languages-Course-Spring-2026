package main.ast.nodes.expr;

import main.visitor.ASTVisitor;

public class BinaryExpr extends Expression {
    private Expression left;
    private Expression right;
    private BinaryOperator operator;

    public Expression getLeft() { return left; }
    public void setLeft(Expression left) { this.left = left; }
    public Expression getRight() { return right; }
    public void setRight(Expression right) { this.right = right; }
    public BinaryOperator getOperator() { return operator; }
    public void setOperator(BinaryOperator operator) { this.operator = operator; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

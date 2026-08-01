package main.ast.nodes.expr;

import main.visitor.ASTVisitor;

public class IntLiteral extends Expression {
    private int value;

    public IntLiteral() {
    }

    public IntLiteral(int value) {
        this.value = value;
    }

    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

package main.ast.nodes.expr;

import main.visitor.ASTVisitor;

public class FloatLiteral extends Expression {
    private double value;

    public FloatLiteral() {
    }

    public FloatLiteral(double value) {
        this.value = value;
    }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

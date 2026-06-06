package main.ast.nodes.expr;

import main.visitor.ASTVisitor;

public class BoolLiteral extends Expression {
    private boolean value;

    public BoolLiteral() {
    }

    public BoolLiteral(boolean value) {
        this.value = value;
    }

    public boolean getValue() { return value; }
    public void setValue(boolean value) { this.value = value; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

package main.ast.nodes.expr;

import main.visitor.ASTVisitor;

public class UnaryExpr extends Expression {
    private UnaryOperator operator;
    private Expression operand;

    public UnaryOperator getOperator() { return operator; }
    public void setOperator(UnaryOperator operator) { this.operator = operator; }
    public Expression getOperand() { return operand; }
    public void setOperand(Expression operand) { this.operand = operand; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

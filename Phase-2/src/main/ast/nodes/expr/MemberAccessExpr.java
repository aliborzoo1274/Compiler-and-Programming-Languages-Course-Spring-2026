package main.ast.nodes.expr;

import main.visitor.ASTVisitor;

public class MemberAccessExpr extends Expression {
    private Expression instance;
    private String memberName;

    public Expression getInstance() { return instance; }
    public void setInstance(Expression instance) { this.instance = instance; }
    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

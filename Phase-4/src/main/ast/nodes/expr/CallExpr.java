package main.ast.nodes.expr;

import main.visitor.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class CallExpr extends Expression {
    private Expression callee;
    private final List<Expression> args = new ArrayList<>();

    public Expression getCallee() { return callee; }
    public void setCallee(Expression callee) { this.callee = callee; }
    public List<Expression> getArgs() { return args; }
    public void setArgs(List<Expression> args) {
        this.args.clear();
        if (args != null) {
            this.args.addAll(args);
        }
    }
    public void addArg(Expression arg) { this.args.add(arg); }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

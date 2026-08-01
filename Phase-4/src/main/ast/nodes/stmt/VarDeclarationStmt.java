package main.ast.nodes.stmt;

import main.ast.nodes.Type;
import main.ast.nodes.expr.Expression;
import main.visitor.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class VarDeclarationStmt extends Statement {
    private boolean isMutable;
    private Type type;
    private String name;
    private boolean hasConstructorCall;
    private final List<Expression> constructorArgs = new ArrayList<>();
    private Expression initializer;

    public boolean isMutable() { return isMutable; }
    public void setMutable(boolean mutable) { isMutable = mutable; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean hasConstructorCall() { return hasConstructorCall; }
    public void setHasConstructorCall(boolean hasConstructorCall) { this.hasConstructorCall = hasConstructorCall; }
    public List<Expression> getConstructorArgs() { return constructorArgs; }
    public void setConstructorArgs(List<Expression> constructorArgs) {
        this.constructorArgs.clear();
        if (constructorArgs != null) {
            this.constructorArgs.addAll(constructorArgs);
        }
    }
    public void addConstructorArg(Expression arg) { this.constructorArgs.add(arg); }
    public Expression getInitializer() { return initializer; }
    public void setInitializer(Expression initializer) { this.initializer = initializer; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

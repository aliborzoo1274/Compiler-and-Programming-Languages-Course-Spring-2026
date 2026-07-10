package main.ast.nodes.decl;

import main.ast.nodes.Type;
import main.ast.nodes.stmt.BlockStmt;
import main.visitor.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class MethodDeclaration extends Declaration {
    private AccessModifier accessModifier;
    private Type returnType;
    private String name;
    private final List<Param> params = new ArrayList<>();
    private BlockStmt body;

    public AccessModifier getAccessModifier() { return accessModifier; }
    public void setAccessModifier(AccessModifier accessModifier) { this.accessModifier = accessModifier; }
    public Type getReturnType() { return returnType; }
    public void setReturnType(Type returnType) { this.returnType = returnType; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Param> getParams() { return params; }
    public void setParams(List<Param> params) {
        this.params.clear();
        if (params != null) {
            this.params.addAll(params);
        }
    }
    public void addParam(Param param) { this.params.add(param); }
    public BlockStmt getBody() { return body; }
    public void setBody(BlockStmt body) { this.body = body; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

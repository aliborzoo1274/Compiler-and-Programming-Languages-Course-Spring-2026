package main.ast.nodes.decl;

import main.ast.nodes.Type;
import main.visitor.ASTVisitor;

public class FieldDeclaration extends Declaration {
    private AccessModifier accessModifier;
    private boolean isMutable;
    private Type type;
    private String name;

    public AccessModifier getAccessModifier() { return accessModifier; }
    public void setAccessModifier(AccessModifier accessModifier) { this.accessModifier = accessModifier; }
    public boolean isMutable() { return isMutable; }
    public void setMutable(boolean mutable) { isMutable = mutable; }
    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

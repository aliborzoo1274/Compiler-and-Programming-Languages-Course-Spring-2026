package main.ast.nodes.decl;

import main.visitor.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class StructDeclaration extends Declaration {
    private String name;
    private final List<FieldDeclaration> fields = new ArrayList<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<FieldDeclaration> getFields() { return fields; }
    public void setFields(List<FieldDeclaration> fields) {
        this.fields.clear();
        if (fields != null) {
            this.fields.addAll(fields);
        }
    }
    public void addField(FieldDeclaration field) { this.fields.add(field); }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

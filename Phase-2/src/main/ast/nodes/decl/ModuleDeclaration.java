package main.ast.nodes.decl;

import main.visitor.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class ModuleDeclaration extends Declaration {
    private String name;
    private final List<String> includes = new ArrayList<>();
    private final List<Declaration> members = new ArrayList<>();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getIncludes() { return includes; }
    public void setIncludes(List<String> includes) {
        this.includes.clear();
        if (includes != null) {
            this.includes.addAll(includes);
        }
    }
    public void addInclude(String include) { this.includes.add(include); }
    public List<Declaration> getMembers() { return members; }
    public void setMembers(List<Declaration> members) {
        this.members.clear();
        if (members != null) {
            this.members.addAll(members);
        }
    }
    public void addMember(Declaration member) { this.members.add(member); }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

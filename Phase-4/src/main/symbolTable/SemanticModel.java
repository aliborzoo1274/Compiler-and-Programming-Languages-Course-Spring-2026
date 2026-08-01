package main.symbolTable;

import main.ast.nodes.Program;
import main.ast.nodes.decl.AccessModifier;
import main.ast.nodes.decl.ConstructorDeclaration;
import main.ast.nodes.decl.Declaration;
import main.ast.nodes.decl.FieldDeclaration;
import main.ast.nodes.decl.MethodDeclaration;
import main.ast.nodes.decl.ModuleDeclaration;
import main.ast.nodes.decl.Param;
import main.ast.nodes.decl.StructDeclaration;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public final class SemanticModel {
    private SemanticModel() { }

    public static final class Model {
        public final SymbolTable global = new SymbolTable();
        public final Map<Declaration, SymbolTable.Symbol> owners = new IdentityHashMap<>();
        public final Map<MethodDeclaration, SymbolTable.Symbol> methods = new IdentityHashMap<>();
        public final Map<ConstructorDeclaration, SymbolTable.Symbol> constructors = new IdentityHashMap<>();
        public final Map<SymbolTable.Symbol, List<SymbolTable.Symbol>> includes = new IdentityHashMap<>();
    }

    public static Model build(Program program) {
        Model model = new Model();
        for (Declaration declaration : program.getDeclarations()) {
            String name = declaration instanceof ModuleDeclaration
                    ? ((ModuleDeclaration) declaration).getName()
                    : ((StructDeclaration) declaration).getName();
            SymbolTable.Symbol owner = new SymbolTable.Symbol(
                    declaration instanceof ModuleDeclaration ? SymbolTable.Kind.MODULE : SymbolTable.Kind.STRUCT,
                    name, name, false, false, declaration.getLine());
            owner.setMembers(new SymbolTable(model.global));
            if (model.global.defineContainer(owner)) model.owners.put(declaration, owner);
        }

        for (Declaration declaration : program.getDeclarations()) {
            SymbolTable.Symbol owner = model.owners.get(declaration);
            if (owner == null) continue;
            List<SymbolTable.Symbol> included = new ArrayList<>();
            if (declaration instanceof ModuleDeclaration) {
                for (String include : ((ModuleDeclaration) declaration).getIncludes()) {
                    SymbolTable.Symbol target = model.global.resolveContainer(include);
                    if (target != null) included.add(target);
                }
            }
            model.includes.put(owner, included);

            if (declaration instanceof StructDeclaration) {
                for (FieldDeclaration field : ((StructDeclaration) declaration).getFields()) addField(owner, field);
            } else {
                for (Declaration member : ((ModuleDeclaration) declaration).getMembers()) {
                    if (member instanceof FieldDeclaration) addField(owner, (FieldDeclaration) member);
                    else if (member instanceof MethodDeclaration) addMethod(model, owner, (MethodDeclaration) member);
                    else if (member instanceof ConstructorDeclaration) addConstructor(model, owner, (ConstructorDeclaration) member);
                }
            }
        }
        return model;
    }

    private static void addField(SymbolTable.Symbol owner, FieldDeclaration field) {
        SymbolTable.Symbol symbol = new SymbolTable.Symbol(SymbolTable.Kind.FIELD, field.getName(),
                field.getType().getName(), field.isMutable(), field.getAccessModifier() == AccessModifier.PRIVATE,
                field.getLine());
        symbol.setInitialized(true);
        owner.getMembers().defineValue(symbol);
    }

    private static List<String> parameterTypes(List<Param> params) {
        List<String> result = new ArrayList<>();
        for (Param param : params) result.add(param.getType().getName());
        return result;
    }

    private static void addMethod(Model model, SymbolTable.Symbol owner, MethodDeclaration method) {
        SymbolTable.Symbol symbol = new SymbolTable.Symbol(SymbolTable.Kind.METHOD, method.getName(),
                method.getReturnType().getName(), false, method.getAccessModifier() == AccessModifier.PRIVATE,
                method.getLine(), parameterTypes(method.getParams()));
        if (owner.getMembers().defineMethod(symbol)) model.methods.put(method, symbol);
    }

    private static void addConstructor(Model model, SymbolTable.Symbol owner, ConstructorDeclaration constructor) {
        SymbolTable.Symbol symbol = new SymbolTable.Symbol(SymbolTable.Kind.CONSTRUCTOR, constructor.getName(),
                "void", false, constructor.getAccessModifier() == AccessModifier.PRIVATE,
                constructor.getLine(), parameterTypes(constructor.getParams()));
        if (owner.getMembers().defineMethod(symbol)) model.constructors.put(constructor, symbol);
    }
}

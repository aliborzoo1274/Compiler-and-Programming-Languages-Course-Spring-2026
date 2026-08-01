package main.visitor;

import main.ast.nodes.*;
import main.ast.nodes.decl.*;
import main.ast.nodes.stmt.BlockStmt;

import java.util.List;

public class PrintVisitor extends Visitor<Void> {
    private final StringBuilder builder = new StringBuilder();
    private int indent = 0;

    @Override
    public Void visit(Program program) {
        int moduleCount = 0;
        int structCount = 0;
        for (Declaration declaration : program.getDeclarations()) {
            if (declaration instanceof ModuleDeclaration) {
                moduleCount++;
            } else if (declaration instanceof StructDeclaration) {
                structCount++;
            }
        }

        appendLine("program [modules:" + moduleCount + " structs:" + structCount + "]");
        indent++;
        for (Declaration declaration : program.getDeclarations()) {
            if (declaration instanceof ModuleDeclaration || declaration instanceof StructDeclaration) {
                declaration.accept(this);
            }
        }
        indent--;

        String output = builder.toString();
        if (output.endsWith("\n")) {
            output = output.substring(0, output.length() - 1);
        }
        System.out.print(output);
        return null;
    }

    @Override
    public Void visit(ModuleDeclaration moduleDeclaration) {
        int methodCount = 0;
        int fieldCount = 0;
        for (Declaration member : moduleDeclaration.getMembers()) {
            if (member instanceof MethodDeclaration || member instanceof ConstructorDeclaration) {
                methodCount++;
            } else if (member instanceof FieldDeclaration) {
                fieldCount++;
            }
        }

        appendLine("module " + moduleDeclaration.getName() + " [methods:" + methodCount + " fields:" + fieldCount + "]");
        indent++;
        for (Declaration member : moduleDeclaration.getMembers()) {
            if (member instanceof MethodDeclaration || member instanceof FieldDeclaration || member instanceof ConstructorDeclaration) {
                member.accept(this);
            }
        }
        indent--;
        return null;
    }

    @Override
    public Void visit(StructDeclaration structDeclaration) {
        appendLine("struct " + structDeclaration.getName() + " [fields:" + structDeclaration.getFields().size() + "]");
        indent++;
        for (FieldDeclaration field : structDeclaration.getFields()) {
            field.accept(this);
        }
        indent--;
        return null;
    }

    @Override
    public Void visit(FieldDeclaration fieldDeclaration) {
        String typeName = typeName(fieldDeclaration.getType());
        String line = "field " + fieldDeclaration.getName() + " " + typeName + accessSuffix(fieldDeclaration.getAccessModifier());
        appendLine(line);
        return null;
    }

    @Override
    public Void visit(MethodDeclaration methodDeclaration) {
        String signature = "(" + paramTypes(methodDeclaration.getParams()) + " -> " + typeName(methodDeclaration.getReturnType()) + ")";
        int statementCount = statementCount(methodDeclaration.getBody());
        String line = "method " + methodDeclaration.getName() + " " + signature
                + accessSuffix(methodDeclaration.getAccessModifier())
                + " [statements:" + statementCount + "]";
        appendLine(line);
        return null;
    }

    @Override
    public Void visit(ConstructorDeclaration constructorDeclaration) {
        String signature = "(" + paramTypes(constructorDeclaration.getParams()) + " -> " + "void" + ")";
        int statementCount = statementCount(constructorDeclaration.getBody());
        String line = "method " + constructorDeclaration.getName() + " " + signature
                + accessSuffix(constructorDeclaration.getAccessModifier())
                + " [statements:" + statementCount + "]";
        appendLine(line);
        return null;
    }

    private void appendLine(String line) {
        for (int i = 0; i < indent; i++) {
            builder.append('\t');
        }
        builder.append(line).append('\n');
    }

    private String accessSuffix(AccessModifier modifier) {
        if (modifier == null) {
            return "";
        }
        return modifier == AccessModifier.PUBLIC ? ":public" : ":private";
    }

    private String typeName(Type type) {
        if (type == null || type.getName() == null) {
            return "";
        }
        return type.getName();
    }

    private String paramTypes(List<Param> params) {
        if (params == null || params.isEmpty()) {
            return "void";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) {
                result.append('*');
            }
            result.append(typeName(params.get(i).getType()));
        }
        return result.toString();
    }

    private int statementCount(BlockStmt body) {
        if (body == null) {
            return 0;
        }
        return body.getStatements().size();
    }
}

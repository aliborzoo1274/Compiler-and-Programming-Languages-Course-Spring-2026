package main.visitor;

import main.ast.nodes.Program;
import main.ast.nodes.decl.*;
import main.ast.nodes.expr.*;
import main.ast.nodes.stmt.*;
import main.symbolTable.SemanticModel;
import main.symbolTable.SymbolTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TypeAnalyzer extends Visitor<String> {
    private SemanticModel.Model model;
    private SymbolTable scope;
    private SymbolTable.Symbol owner;
    private String expectedReturnType;

    @Override
    public String visit(Program program) {
        model = SemanticModel.build(program);
        for (Declaration declaration : program.getDeclarations()) {
            SymbolTable.Symbol current = model.owners.get(declaration);
            if (!(declaration instanceof ModuleDeclaration) || current == null) continue;
            owner = current;
            for (Declaration member : ((ModuleDeclaration) declaration).getMembers()) {
                if (member instanceof MethodDeclaration) analyseMethod((MethodDeclaration) member);
                else if (member instanceof ConstructorDeclaration) analyseConstructor((ConstructorDeclaration) member);
            }
        }
        return null;
    }

    @Override
    public String visit(BlockStmt block) {
        visitBlock(block);
        return null;
    }

    @Override
    public String visit(VarDeclarationStmt variable) {
        for (Expression argument : variable.getConstructorArgs()) argument.accept(this);
        if (variable.getInitializer() != null) checkAssignment(variable.getLine(), variable.getType().getName(), variable.getInitializer().accept(this));
        if (knownType(variable.getType().getName()))
            scope.defineValue(new SymbolTable.Symbol(SymbolTable.Kind.VARIABLE, variable.getName(), variable.getType().getName(),
                    variable.isMutable(), false, variable.getLine()));
        return null;
    }

    @Override
    public String visit(ForInitDecl init) {
        if (init.getInitializer() != null) checkAssignment(init.getLine(), init.getType().getName(), init.getInitializer().accept(this));
        if (knownType(init.getType().getName()))
            scope.defineValue(new SymbolTable.Symbol(SymbolTable.Kind.VARIABLE, init.getName(), init.getType().getName(), init.isMutable(), false, init.getLine()));
        return null;
    }

    @Override
    public String visit(AssignmentStmt statement) {
        if (statement.getAssignment() != null) statement.getAssignment().accept(this);
        return null;
    }

    @Override
    public String visit(Assignment assignment) {
        SymbolTable.Symbol target = lvalue(assignment.getLeft());
        String source = assignment.getRight() == null ? null : assignment.getRight().accept(this);
        if (target != null) {
            if (!target.isMutable()) error(assignment.getLine(), "Cannot modify immutable variable " + target.getName());
            checkAssignment(assignment.getLine(), target.getType(), source);
        }
        return null;
    }

    @Override
    public String visit(IfStmt statement) {
        checkCondition(statement.getLine(), statement.getCondition());
        visitBlock(statement.getThenBlock());
        for (ElseIfClause clause : statement.getElseIfClauses()) clause.accept(this);
        visitBlock(statement.getElseBlock());
        return null;
    }

    @Override
    public String visit(ElseIfClause clause) {
        checkCondition(clause.getLine(), clause.getCondition());
        visitBlock(clause.getThenBlock());
        return null;
    }

    @Override
    public String visit(WhileStmt statement) {
        checkCondition(statement.getLine(), statement.getCondition());
        visitBlock(statement.getBody());
        return null;
    }

    @Override
    public String visit(ForStmt statement) {
        SymbolTable previous = scope;
        scope = new SymbolTable(scope);
        if (statement.getInit() != null) statement.getInit().accept(this);
        if (statement.getCondition() != null) checkCondition(statement.getLine(), statement.getCondition());
        if (statement.getUpdate() != null) statement.getUpdate().accept(this);
        visitBlock(statement.getBody());
        scope = previous;
        return null;
    }

    @Override
    public String visit(ForInitAssign init) {
        for (Assignment assignment : init.getAssignments()) assignment.accept(this);
        return null;
    }

    @Override
    public String visit(ForUpdateAssign update) {
        for (Assignment assignment : update.getAssignments()) assignment.accept(this);
        return null;
    }

    @Override
    public String visit(ReturnStmt statement) {
        String actual = statement.getValue() == null ? "void" : statement.getValue().accept(this);
        if (actual != null && knownType(expectedReturnType) && !same(expectedReturnType, actual))
            error(statement.getLine(), "Return type mismatch. Expected " + expectedReturnType + ", got " + actual);
        return null;
    }

    @Override
    public String visit(OutputStmt statement) {
        return statement.getValue() == null ? null : statement.getValue().accept(this);
    }

    @Override
    public String visit(ExprStmt statement) {
        return statement.getExpression() == null ? null : statement.getExpression().accept(this);
    }

    @Override
    public String visit(Identifier identifier) {
        SymbolTable.Symbol symbol = resolveValue(identifier.getName());
        if (symbol != null) return symbol.getType();
        SymbolTable.Symbol container = model.global.resolveContainer(identifier.getName());
        return container == null ? null : container.getName();
    }

    @Override
    public String visit(IntLiteral ignored) { return "int"; }

    @Override
    public String visit(FloatLiteral ignored) { return "float"; }

    @Override
    public String visit(BoolLiteral ignored) { return "bool"; }

    @Override
    public String visit(ThisExpr ignored) {
        return owner == null ? null : owner.getName();
    }

    @Override
    public String visit(UnaryExpr expression) {
        return expression.getOperand() == null ? null : expression.getOperand().accept(this);
    }

    @Override
    public String visit(BinaryExpr expression) {
        String left = expression.getLeft() == null ? null : expression.getLeft().accept(this);
        String right = expression.getRight() == null ? null : expression.getRight().accept(this);
        if (left == null || right == null) return null;
        switch (expression.getOperator()) {
            case LT: case GT: case LE: case GE: case EQ: case NE: return "bool";
            default: return same(left, right) && numeric(left) ? left : null;
        }
    }

    @Override
    public String visit(MemberAccessExpr expression) {
        SymbolTable.Symbol container = containerFor(expression.getInstance());
        SymbolTable.Symbol field = container == null ? null : memberValue(container, expression.getMemberName());
        return field == null ? null : field.getType();
    }

    @Override
    public String visit(CallExpr expression) {
        List<String> actual = new ArrayList<>();
        for (Expression argument : expression.getArgs()) actual.add(argument.accept(this));
        String name;
        List<SymbolTable.Symbol> candidates;
        if (expression.getCallee() instanceof Identifier) {
            name = ((Identifier) expression.getCallee()).getName();
            candidates = methodsFor(owner, name);
        }
        else if (expression.getCallee() instanceof MemberAccessExpr) {
            MemberAccessExpr member = (MemberAccessExpr) expression.getCallee();
            name = member.getMemberName();
            candidates = methodsFor(containerFor(member.getInstance()), name);
        }
        else return expression.getCallee() == null ? null : expression.getCallee().accept(this);
        if (candidates.isEmpty()) return null;
        List<SymbolTable.Symbol> countMatches = new ArrayList<>();
        for (SymbolTable.Symbol candidate : candidates) if (candidate.getParameterTypes().size() == actual.size()) countMatches.add(candidate);
        if (countMatches.isEmpty()) {
            error(expression.getLine(), "Argument count mismatch for method " + name + ". Expected "
                    + candidates.get(0).getParameterTypes().size() + ", got " + actual.size());
            return candidates.get(0).getType();
        }
        for (SymbolTable.Symbol candidate : countMatches) {
            boolean match = true;
            for (int i = 0; i < actual.size(); i++)
                if (!same(candidate.getParameterTypes().get(i), actual.get(i))) {
                match = false;
                break;
            }
            if (match) return candidate.getType();
        }
        SymbolTable.Symbol selected = countMatches.get(0);
        for (int i = 0; i < actual.size(); i++)
            if (!same(selected.getParameterTypes().get(i), actual.get(i))) {
            if (actual.get(i) != null) error(expression.getLine(), "Argument type mismatch for method " + name + ", parameter "
                    + (i + 1) + ". Expected " + selected.getParameterTypes().get(i) + ", got " + actual.get(i));
            break;
        }
        return selected.getType();
    }

    
    private void error(int line, String message) {
        System.out.println("Line " + line + " : " + message);
    }

    private boolean same(String left, String right) {
        return left != null && left.equals(right);
    }

    private boolean numeric(String type) {
        return "int".equals(type) || "float".equals(type) || "double".equals(type);
    }

    private boolean knownType(String type) {
        return "int".equals(type) || "float".equals(type) || "double".equals(type) || "char".equals(type)
                || "bool".equals(type) || "void".equals(type) || model.global.resolveContainer(type) != null;
    }

    private void analyseMethod(MethodDeclaration method) {
        scope = new SymbolTable();
        expectedReturnType = method.getReturnType().getName();
        for (Param param : method.getParams()) defineParam(param);
        visitBlock(method.getBody());
        scope = null;
        expectedReturnType = null;
    }

    private void analyseConstructor(ConstructorDeclaration constructor) {
        scope = new SymbolTable();
        expectedReturnType = "void";
        for (Param param : constructor.getParams()) defineParam(param);
        visitBlock(constructor.getBody());
        scope = null;
        expectedReturnType = null;
    }

    private void defineParam(Param param) {
        if (knownType(param.getType().getName()))
            scope.defineValue(new SymbolTable.Symbol(SymbolTable.Kind.PARAMETER, param.getName(), param.getType().getName(),
                    param.isMutable(), false, param.getLine()));
    }

    private void visitBlock(BlockStmt block) {
        if (block == null) return;
        SymbolTable previous = scope;
        scope = new SymbolTable(scope);
        for (Statement statement : block.getStatements()) if (statement != null) statement.accept(this);
        scope = previous;
    }

    private void checkAssignment(int line, String target, String source) {
        if (knownType(target) && source != null && !same(target, source))
            error(line, "Type mismatch in assignment. Cannot assign " + source + " to " + target);
    }

    private void checkCondition(int line, Expression condition) {
        String type = condition == null ? null : condition.accept(this);
        if (type != null && !"bool".equals(type)) error(line, "Condition type must be bool");
    }

    private SymbolTable.Symbol lvalue(Expression expression) {
        if (expression instanceof Identifier) return resolveValue(((Identifier) expression).getName());
        if (expression instanceof MemberAccessExpr) {
            MemberAccessExpr member = (MemberAccessExpr) expression;
            return memberValue(containerFor(member.getInstance()), member.getMemberName());
        }
        return null;
    }

    private SymbolTable.Symbol resolveValue(String name) {
        SymbolTable.Symbol local = scope == null ? null : scope.resolveValue(name);
        return local != null ? local : memberValue(owner, name);
    }

    private SymbolTable.Symbol containerFor(Expression expression) {
        if (expression instanceof ThisExpr) return owner;
        if (expression instanceof Identifier) {
            Identifier identifier = (Identifier) expression;
            SymbolTable.Symbol value = resolveValue(identifier.getName());
            return value == null ? model.global.resolveContainer(identifier.getName()) : model.global.resolveContainer(value.getType());
        }
        String type = expression == null ? null : expression.accept(this);
        return type == null ? null : model.global.resolveContainer(type);
    }

    private SymbolTable.Symbol memberValue(SymbolTable.Symbol from, String name) {
        if (from == null) return null;
        SymbolTable.Symbol value = from.getMembers().localValue(name);
        if (value != null) return value;
        for (SymbolTable.Symbol included : model.includes.getOrDefault(from, Collections.<SymbolTable.Symbol>emptyList())) {
            value = included.getMembers().localValue(name);
            if (value != null) return value;
        }
        return null;
    }

    private List<SymbolTable.Symbol> methodsFor(SymbolTable.Symbol from, String name) {
        if (from == null) return Collections.emptyList();
        List<SymbolTable.Symbol> result = new ArrayList<>(from.getMembers().localMethods(name));
        for (SymbolTable.Symbol included : model.includes.getOrDefault(from, Collections.<SymbolTable.Symbol>emptyList()))
            result.addAll(included.getMembers().localMethods(name));
        return result;
    }
}

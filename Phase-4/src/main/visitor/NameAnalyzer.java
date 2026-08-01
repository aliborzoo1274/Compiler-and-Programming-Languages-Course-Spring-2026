package main.visitor;

import main.ast.nodes.Program;
import main.ast.nodes.Type;
import main.ast.nodes.decl.*;
import main.ast.nodes.expr.*;
import main.ast.nodes.stmt.*;
import main.symbolTable.SemanticModel;
import main.symbolTable.SymbolTable;

import java.util.*;

public class NameAnalyzer extends Visitor<Void> {
    private SemanticModel.Model model;
    private SymbolTable scope;
    private SymbolTable.Symbol owner;
    private final Set<String> visibleContainers = new HashSet<>();
    private final Set<String> referencedContainers = new HashSet<>();

    @Override
    public Void visit(Program program) {
        model = SemanticModel.build(program);
        Set<String> topNames = new HashSet<>();
        for (Declaration declaration : program.getDeclarations()) {
            String name = declaration instanceof ModuleDeclaration ? ((ModuleDeclaration) declaration).getName()
                    : ((StructDeclaration) declaration).getName();
            if (!topNames.add(name)) error(declaration.getLine(), name + " already defined");
            SymbolTable.Symbol current = model.owners.get(declaration);
            if (current == null) continue;
            if (declaration instanceof ModuleDeclaration) {
                for (String include : ((ModuleDeclaration) declaration).getIncludes()) {
                    if (!visibleContainers.contains(include)) error(declaration.getLine(), include + " not declared");
                }
            }
            visibleContainers.add(name);
            checkMemberDuplicates(declaration);
            owner = current;
            if (declaration instanceof ModuleDeclaration) {
                for (Declaration member : ((ModuleDeclaration) declaration).getMembers()) {
                    if (member instanceof MethodDeclaration) analyseMethod((MethodDeclaration) member);
                    else if (member instanceof ConstructorDeclaration) analyseConstructor((ConstructorDeclaration) member);
                }
            }
        }
        warnUnreachable(program);
        return null;
    }

    @Override
    public Void visit(BlockStmt block) {
        visitBlock(block);
        return null;
    }

    @Override
    public Void visit(VarDeclarationStmt variable) {
        checkKnownType(variable.getType(), variable.getLine());
        if (!primitive(variable.getType().getName())) referencedContainers.add(variable.getType().getName());
        if (variable.getInitializer() != null) variable.getInitializer().accept(this);
        for (Expression argument : variable.getConstructorArgs()) argument.accept(this);
        if (!knownType(variable.getType())) return null;
        SymbolTable.Symbol symbol = new SymbolTable.Symbol(SymbolTable.Kind.VARIABLE, variable.getName(), variable.getType().getName(),
                variable.isMutable(), false, variable.getLine()); 
        symbol.setInitialized(variable.getInitializer() != null || variable.hasConstructorCall() || !primitive(variable.getType().getName()));
        if (!scope.defineValue(symbol)) error(variable.getLine(), variable.getName() + " already defined");
        return null;
    }

    @Override
    public Void visit(ForInitDecl init) {
        checkKnownType(init.getType(), init.getLine());
        if (!primitive(init.getType().getName())) referencedContainers.add(init.getType().getName());
        if (init.getInitializer() != null) init.getInitializer().accept(this);
        if (!knownType(init.getType())) return null;
        SymbolTable.Symbol symbol = new SymbolTable.Symbol(SymbolTable.Kind.VARIABLE, init.getName(), init.getType().getName(), init.isMutable(), false, init.getLine());
        symbol.setInitialized(init.getInitializer() != null || !primitive(init.getType().getName()));
        if (!scope.defineValue(symbol)) error(init.getLine(), init.getName() + " already defined");
        return null;
    }

    @Override
    public Void visit(AssignmentStmt statement) {
        if (statement.getAssignment() != null) statement.getAssignment().accept(this);
        return null;
    }

    @Override
    public Void visit(Assignment assignment) {
        SymbolTable.Symbol target = resolveLValue(assignment.getLeft());
        if (assignment.getRight() != null) assignment.getRight().accept(this);
        if (target != null) target.setInitialized(true);
        return null;
    }

    @Override
    public Void visit(IfStmt statement) {
        if (statement.getCondition() != null) statement.getCondition().accept(this);
        visitBlock(statement.getThenBlock());
        for (ElseIfClause clause : statement.getElseIfClauses()) clause.accept(this);
        visitBlock(statement.getElseBlock());
        return null;
    }

    @Override
    public Void visit(ElseIfClause clause) {
        if (clause.getCondition() != null) clause.getCondition().accept(this);
        visitBlock(clause.getThenBlock());
        return null;
    }

    @Override
    public Void visit(WhileStmt statement) {
        if (statement.getCondition() != null) statement.getCondition().accept(this);
        visitBlock(statement.getBody());
        return null;
    }

    @Override
    public Void visit(ForStmt statement) {
        SymbolTable previous = scope;
        scope = new SymbolTable(scope);
        if (statement.getInit() != null) statement.getInit().accept(this);
        if (statement.getCondition() != null) statement.getCondition().accept(this);
        if (statement.getUpdate() != null) statement.getUpdate().accept(this);
        visitBlock(statement.getBody());
        scope = previous;
        return null;
    }

    @Override
    public Void visit(ForInitAssign init) {
        for (Assignment assignment : init.getAssignments()) assignment.accept(this);
        return null;
    }

    @Override
    public Void visit(ForUpdateAssign update) {
        for (Assignment assignment : update.getAssignments()) assignment.accept(this);
        return null;
    }

    @Override
    public Void visit(ReturnStmt statement) {
        if (statement.getValue() != null) statement.getValue().accept(this);
        return null;
    }

    @Override
    public Void visit(OutputStmt statement) {
        if (statement.getValue() != null) statement.getValue().accept(this);
        return null;
    }

    @Override
    public Void visit(ExprStmt statement) {
        if (statement.getExpression() != null) statement.getExpression().accept(this);
        return null;
    }

    @Override
    public Void visit(Identifier identifier) {
        SymbolTable.Symbol symbol = resolveValue(identifier.getName());
        if (symbol == null) error(identifier.getLine(), identifier.getName() + " not declared");
        else if (!symbol.isInitialized()) error(identifier.getLine(), identifier.getName() + " is uninitialized");
        return null;
    }

    @Override
    public Void visit(MemberAccessExpr expression) {
        SymbolTable.Symbol container = containerFor(expression.getInstance(), true);
        if (container != null) resolveMember(container, expression.getMemberName(), expression.getLine(), false);
        return null;
    }

    @Override
    public Void visit(CallExpr expression) {
        for (Expression argument : expression.getArgs()) argument.accept(this);
        if (expression.getCallee() instanceof Identifier) {
            String name = ((Identifier) expression.getCallee()).getName();
            if (methodsFor(owner, name).isEmpty()) error(expression.getLine(), name + " not declared");
        }
        else if (expression.getCallee() instanceof MemberAccessExpr) {
            MemberAccessExpr member = (MemberAccessExpr) expression.getCallee();
            SymbolTable.Symbol container = containerFor(member.getInstance(), true);
            if (container != null && methodsFor(container, member.getMemberName()).isEmpty()) error(expression.getLine(), member.getMemberName() + " not declared");
            else if (container != null) checkPrivate(methodsFor(container, member.getMemberName()).get(0), member.getMemberName(), member.getLine(), container);
        }
        else if (expression.getCallee() != null) expression.getCallee().accept(this);
        return null;
    }

    @Override
    public Void visit(BinaryExpr expression) {
        if (expression.getLeft() != null) expression.getLeft().accept(this);
        if (expression.getRight() != null) expression.getRight().accept(this);
        return null;
    }

    @Override
    public Void visit(UnaryExpr expression) {
        if (expression.getOperand() != null) expression.getOperand().accept(this);
        return null;
    }

    private void error(int line, String message) {
        System.out.println("Line " + line + " : " + message);
    }

    private boolean primitive(String type) {
        return "int".equals(type) || "float".equals(type) || "double".equals(type)
                || "char".equals(type) || "bool".equals(type) || "void".equals(type);
    }

    private void checkMemberDuplicates(Declaration declaration) {
        Set<String> values = new HashSet<>();
        Set<String> methods = new HashSet<>();
        List<Declaration> members = declaration instanceof ModuleDeclaration
                ? ((ModuleDeclaration) declaration).getMembers() : new ArrayList<Declaration>(((StructDeclaration) declaration).getFields());
        for (Declaration member : members) {
            if (member instanceof FieldDeclaration) {
                FieldDeclaration field = (FieldDeclaration) member;
                checkKnownType(field.getType(), field.getLine());
                if (!values.add(field.getName())) error(field.getLine(), field.getName() + " already defined");
            } else if (member instanceof MethodDeclaration) {
                MethodDeclaration method = (MethodDeclaration) member;
                checkKnownType(method.getReturnType(), method.getLine());
                String key = signature(method.getName(), method.getParams());
                if (!methods.add(key)) error(method.getLine(), method.getName() + " already defined");
            } else if (member instanceof ConstructorDeclaration) {
                ConstructorDeclaration constructor = (ConstructorDeclaration) member;
                String key = signature(constructor.getName(), constructor.getParams());
                if (!methods.add(key)) error(constructor.getLine(), constructor.getName() + " already defined");
            }
        }
    }

    private String signature(String name, List<Param> params) {
        StringBuilder result = new StringBuilder(name).append('(');
        for (Param param : params) result.append(param.getType().getName()).append(',');
        return result.append(')').toString();
    }

    private void checkKnownType(Type type, int line) {
        if (type != null && !primitive(type.getName()) && !visibleContainers.contains(type.getName())) error(line, type.getName() + " not declared");
    }

    private boolean knownType(Type type) {
        return type != null && (primitive(type.getName()) || visibleContainers.contains(type.getName()));
    }

    private void analyseMethod(MethodDeclaration method) {
        scope = new SymbolTable();
        for (Param param : method.getParams()) defineParam(param);
        visitBlock(method.getBody());
        scope = null;
    }

    private void analyseConstructor(ConstructorDeclaration constructor) {
        scope = new SymbolTable();
        for (Param param : constructor.getParams()) defineParam(param);
        visitBlock(constructor.getBody());
        scope = null;
    }

    private void defineParam(Param param) {
        checkKnownType(param.getType(), param.getLine());
        if (!knownType(param.getType())) return;
        SymbolTable.Symbol symbol = new SymbolTable.Symbol(SymbolTable.Kind.PARAMETER, param.getName(), param.getType().getName(),
                param.isMutable(), false, param.getLine());
        symbol.setInitialized(true);
        if (!scope.defineValue(symbol)) error(param.getLine(), param.getName() + " already defined");
    }

    private void visitBlock(BlockStmt block) {
        if (block == null) return;
        SymbolTable previous = scope;
        scope = new SymbolTable(scope);
        for (Statement statement : block.getStatements()) if (statement != null) statement.accept(this);
        scope = previous;
    }

    private SymbolTable.Symbol resolveLValue(Expression expression) {
        if (expression instanceof Identifier) return resolveValueForWrite((Identifier) expression);
        if (expression instanceof MemberAccessExpr) {
            MemberAccessExpr member = (MemberAccessExpr) expression;
            SymbolTable.Symbol container = containerFor(member.getInstance(), true);
            return container == null ? null : resolveMember(container, member.getMemberName(), member.getLine(), false);
        }
        if (expression != null) expression.accept(this);
        return null;
    }

    private SymbolTable.Symbol resolveValueForWrite(Identifier identifier) {
        SymbolTable.Symbol symbol = resolveValue(identifier.getName());
        if (symbol == null) error(identifier.getLine(), identifier.getName() + " not declared");
        return symbol;
    }

    private SymbolTable.Symbol resolveValue(String name) {
        SymbolTable.Symbol local = scope == null ? null : scope.resolveValue(name);
        return local != null ? local : resolveMember(owner, name, 0, true);
    }

    private SymbolTable.Symbol containerFor(Expression instance, boolean report) {
        if (instance instanceof ThisExpr) return owner;
        if (instance instanceof Identifier) {
            Identifier identifier = (Identifier) instance;
            SymbolTable.Symbol value = resolveValue(identifier.getName());
            if (value != null) {
                if (!value.isInitialized() && report) error(identifier.getLine(), identifier.getName() + " is uninitialized");
                SymbolTable.Symbol result = model.global.resolveContainer(value.getType());
                if (result == null && report) error(identifier.getLine(), identifier.getName() + " not declared");
                return result;
            }
            SymbolTable.Symbol result = model.global.resolveContainer(identifier.getName());
            if ((result == null || !visibleContainers.contains(result.getName())) && report)
                error(identifier.getLine(), identifier.getName() + " not declared");
            return result;
        }
        if (instance != null) instance.accept(this);
        return null;
    }

    private List<SymbolTable.Symbol> methodsFor(SymbolTable.Symbol from, String name) {
        List<SymbolTable.Symbol> result = new ArrayList<>(from.getMembers().localMethods(name));
        for (SymbolTable.Symbol included : model.includes.getOrDefault(from, Collections.<SymbolTable.Symbol>emptyList())) {
            if (visibleContainers.contains(included.getName())) result.addAll(included.getMembers().localMethods(name));
        }
        return result;
    }

    private SymbolTable.Symbol resolveMember(SymbolTable.Symbol from, String name, int line, boolean quiet) {
        SymbolTable.Symbol symbol = from.getMembers().localValue(name);
        SymbolTable.Symbol origin = from;
        if (symbol == null) for (SymbolTable.Symbol included : model.includes.getOrDefault(from, Collections.<SymbolTable.Symbol>emptyList())) {
            symbol = included.getMembers().localValue(name);
            if (symbol != null) {
                origin = included;
                break;
            }
        }
        if (symbol == null) {
            if (!quiet && line != 0) error(line, name + " not declared");
            return null;
        }
        if (!quiet) checkPrivate(symbol, name, line, origin);
        return symbol;
    }

    private void checkPrivate(SymbolTable.Symbol symbol, String name, int line, SymbolTable.Symbol origin) {
        if (symbol.isPrivate() && origin != owner) error(line, name + " is private");
    }

    private void warnUnreachable(Program program) {
        Set<String> used = new HashSet<>();
        used.addAll(referencedContainers);
        for (Declaration declaration : program.getDeclarations()) if (declaration instanceof ModuleDeclaration)
            used.addAll(((ModuleDeclaration) declaration).getIncludes());
        for (Declaration declaration : program.getDeclarations()) if (declaration instanceof ModuleDeclaration) {
            ModuleDeclaration module = (ModuleDeclaration) declaration;
            if (!"Main".equals(module.getName()) && !used.contains(module.getName()))
                System.out.println("Warning Line " + module.getLine() + " : " + module.getName() + " is unreachable");
        }
    }
}

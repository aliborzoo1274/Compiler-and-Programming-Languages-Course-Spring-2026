package main.visitor;

import main.ast.nodes.Program;
import main.ast.nodes.Type;
import main.ast.nodes.decl.AccessModifier;
import main.ast.nodes.decl.ConstructorDeclaration;
import main.ast.nodes.decl.Declaration;
import main.ast.nodes.decl.FieldDeclaration;
import main.ast.nodes.decl.MethodDeclaration;
import main.ast.nodes.decl.ModuleDeclaration;
import main.ast.nodes.decl.Param;
import main.ast.nodes.decl.StructDeclaration;
import main.ast.nodes.expr.BinaryExpr;
import main.ast.nodes.expr.BinaryOperator;
import main.ast.nodes.expr.BoolLiteral;
import main.ast.nodes.expr.CallExpr;
import main.ast.nodes.expr.Expression;
import main.ast.nodes.expr.FloatLiteral;
import main.ast.nodes.expr.Identifier;
import main.ast.nodes.expr.IntLiteral;
import main.ast.nodes.expr.MemberAccessExpr;
import main.ast.nodes.expr.ThisExpr;
import main.ast.nodes.expr.UnaryExpr;
import main.ast.nodes.expr.UnaryOperator;
import main.ast.nodes.stmt.Assignment;
import main.ast.nodes.stmt.AssignmentStmt;
import main.ast.nodes.stmt.BlockStmt;
import main.ast.nodes.stmt.BreakStmt;
import main.ast.nodes.stmt.ContinueStmt;
import main.ast.nodes.stmt.ElseIfClause;
import main.ast.nodes.stmt.ExprStmt;
import main.ast.nodes.stmt.ForInitAssign;
import main.ast.nodes.stmt.ForInitDecl;
import main.ast.nodes.stmt.ForStmt;
import main.ast.nodes.stmt.ForUpdateAssign;
import main.ast.nodes.stmt.IfStmt;
import main.ast.nodes.stmt.OutputStmt;
import main.ast.nodes.stmt.ReturnStmt;
import main.ast.nodes.stmt.Statement;
import main.ast.nodes.stmt.VarDeclarationStmt;
import main.ast.nodes.stmt.WhileStmt;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class CodeGenerator extends Visitor<String> {
    private static final String INT = "int";
    private static final String BOOL = "bool";
    private static final String FLOAT = "float";
    private static final String DOUBLE = "double";
    private static final String CHAR = "char";
    private static final String VOID = "void";

    private final Path outputDirectory;
    private final Map<String, ClassInfo> classes = new LinkedHashMap<>();
    private final Map<String, String> generatedFiles = new LinkedHashMap<>();
    private final Map<String, Integer> slots = new LinkedHashMap<>();
    private final Map<String, String> localTypes = new LinkedHashMap<>();
    private final Deque<LoopLabels> loops = new ArrayDeque<>();

    private StringBuilder out;
    private ClassInfo currentClass;
    private String currentReturnType;
    private int nextSlot;
    private int nextLabel;

    /** Generates into {@code CodeGenOutput}. */
    public CodeGenerator() {
        this(Paths.get("CodeGenOutput"));
    }

    public CodeGenerator(String outputDirectory) {
        this(Paths.get(outputDirectory));
    }

    public CodeGenerator(Path outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Builds all Jasmin sources without writing them.  The returned map is in
     * declaration order and maps the source filename (for example Main.j) to
     * its contents.
     */
    public Map<String, String> generate(Program program) {
        classes.clear();
        generatedFiles.clear();
        nextLabel = 0;

        indexProgram(program);
        Map<String, Declaration> declarationsByName = new LinkedHashMap<>();
        for (Declaration declaration : program.getDeclarations()) {
            String name = declaration instanceof ModuleDeclaration ? ((ModuleDeclaration) declaration).getName()
                    : declaration instanceof StructDeclaration ? ((StructDeclaration) declaration).getName() : null;
            if (name != null) declarationsByName.put(name, declaration);
        }
        Set<String> emitted = new HashSet<>();
        Set<String> visiting = new HashSet<>();
        for (Declaration declaration : program.getDeclarations()) {
            emitDeclaration(declaration, declarationsByName, emitted, visiting);
        }
        return new LinkedHashMap<>(generatedFiles);
    }

    /** Writes the generated Jasmin files to this generator's output directory. */
    public Map<String, String> write(Program program) throws IOException {
        Map<String, String> files = generate(program);
        Files.createDirectories(outputDirectory);
        for (Map.Entry<String, String> entry : files.entrySet()) {
            Files.write(outputDirectory.resolve(entry.getKey()),
                    entry.getValue().getBytes(StandardCharsets.UTF_8));
        }
        return files;
    }

    public Map<String, String> getGeneratedFiles() {
        return new LinkedHashMap<>(generatedFiles);
    }

    /**
     * Returns the local-variable slot for {@code varName}, creating it when
     * needed. Slot zero is reserved for {@code this}; method parameters are
     * registered first, in source order.
     */
    public int slotOf(String varName) {
        Integer slot = slots.get(varName);
        if (slot == null) {
            slot = nextSlot++;
            slots.put(varName, slot);
        }
        return slot;
    }

    @Override
    public String visit(Program program) {
        generate(program);
        StringBuilder allSources = new StringBuilder();
        for (String source : generatedFiles.values()) allSources.append(source);
        return allSources.toString();
    }

    @Override
    public String visit(ModuleDeclaration moduleDeclaration) {
        return emitModule(moduleDeclaration);
    }

    @Override
    public String visit(StructDeclaration structDeclaration) {
        return emitStruct(structDeclaration);
    }

    @Override
    public String visit(FieldDeclaration fieldDeclaration) {
        return ".field " + access(fieldDeclaration.getAccessModifier()) + " "
                + fieldDeclaration.getName() + " " + descriptor(typeName(fieldDeclaration.getType())) + "\n";
    }

    @Override
    public String visit(MethodDeclaration methodDeclaration) {
        emitMethod(methodDeclaration);
        return "";
    }

    @Override
    public String visit(ConstructorDeclaration constructorDeclaration) {
        emitConstructor(constructorDeclaration);
        return "";
    }

    @Override
    public String visit(BlockStmt blockStmt) {
        if (blockStmt != null) {
            for (Statement statement : blockStmt.getStatements()) {
                if (statement != null) statement.accept(this);
            }
        }
        return "";
    }

    @Override
    public String visit(VarDeclarationStmt varDeclarationStmt) {
        String type = typeName(varDeclarationStmt.getType());
        int slot = slotOf(varDeclarationStmt.getName());
        localTypes.put(varDeclarationStmt.getName(), type);

        if (varDeclarationStmt.hasConstructorCall()) {
            emitNew(type, varDeclarationStmt.getConstructorArgs());
        } else if (varDeclarationStmt.getInitializer() != null) {
            emitExpressionAs(varDeclarationStmt.getInitializer(), type);
        } else if (isReference(type) && classes.containsKey(type)) {
            // MOL examples use `mut Account account;` as construction with the
            // no-argument constructor, rather than as a null declaration.
            emitNew(type, new ArrayList<Expression>());
        } else {
            emitDefaultValue(type);
        }
        emitStore(type, slot);
        return "";
    }

    @Override
    public String visit(Assignment assignment) {
        emitAssignment(assignment);
        return "";
    }

    @Override
    public String visit(AssignmentStmt assignmentStmt) {
        if (assignmentStmt.getAssignment() != null) assignmentStmt.getAssignment().accept(this);
        return "";
    }

    @Override
    public String visit(IfStmt ifStmt) {
        String after = label();
        String thenLabel = label();
        String next = label();
        emitBranch(ifStmt.getCondition(), thenLabel, next);
        // Keeping labels separate from instructions is important for Jasmin's
        // line grammar.
        emitLabel(thenLabel);
        if (ifStmt.getThenBlock() != null) ifStmt.getThenBlock().accept(this);
        emit("goto " + after);
        emitLabel(next);

        for (ElseIfClause clause : ifStmt.getElseIfClauses()) {
            String clauseTrue = label();
            String clauseFalse = label();
            emitBranch(clause.getCondition(), clauseTrue, clauseFalse);
            emitLabel(clauseTrue);
            if (clause.getThenBlock() != null) clause.getThenBlock().accept(this);
            emit("goto " + after);
            emitLabel(clauseFalse);
        }
        if (ifStmt.getElseBlock() != null) ifStmt.getElseBlock().accept(this);
        emitLabel(after);
        return "";
    }

    @Override
    public String visit(ElseIfClause elseIfClause) {
        return "";
    }

    @Override
    public String visit(WhileStmt whileStmt) {
        String condition = label();
        String body = label();
        String after = label();
        emitLabel(condition);
        emitBranch(whileStmt.getCondition(), body, after);
        emitLabel(body);
        loops.push(new LoopLabels(condition, after));
        if (whileStmt.getBody() != null) whileStmt.getBody().accept(this);
        loops.pop();
        emit("goto " + condition);
        emitLabel(after);
        return "";
    }

    @Override
    public String visit(ForInitDecl forInitDecl) {
        String type = typeName(forInitDecl.getType());
        int slot = slotOf(forInitDecl.getName());
        localTypes.put(forInitDecl.getName(), type);
        if (forInitDecl.getInitializer() == null) emitDefaultValue(type);
        else emitExpressionAs(forInitDecl.getInitializer(), type);
        emitStore(type, slot);
        return "";
    }

    @Override
    public String visit(ForInitAssign forInitAssign) {
        for (Assignment assignment : forInitAssign.getAssignments()) emitAssignment(assignment);
        return "";
    }

    @Override
    public String visit(ForUpdateAssign forUpdateAssign) {
        for (Assignment assignment : forUpdateAssign.getAssignments()) emitAssignment(assignment);
        return "";
    }

    @Override
    public String visit(ForStmt forStmt) {
        if (forStmt.getInit() instanceof ForInitDecl) ((ForInitDecl) forStmt.getInit()).accept(this);
        else if (forStmt.getInit() instanceof ForInitAssign) ((ForInitAssign) forStmt.getInit()).accept(this);

        String condition = label();
        String body = label();
        String update = label();
        String after = label();
        emitLabel(condition);
        if (forStmt.getCondition() == null) emit("goto " + body);
        else emitBranch(forStmt.getCondition(), body, after);
        emitLabel(body);
        loops.push(new LoopLabels(update, after));
        if (forStmt.getBody() != null) forStmt.getBody().accept(this);
        loops.pop();
        emitLabel(update);
        if (forStmt.getUpdate() != null) forStmt.getUpdate().accept(this);
        emit("goto " + condition);
        emitLabel(after);
        return "";
    }

    @Override
    public String visit(ReturnStmt returnStmt) {
        if (returnStmt.getValue() == null || VOID.equals(currentReturnType)) {
            emit("return");
        } else {
            emitExpressionAs(returnStmt.getValue(), currentReturnType);
            emit(returnInstruction(currentReturnType));
        }
        return "";
    }

    @Override
    public String visit(BreakStmt breakStmt) {
        if (!loops.isEmpty()) emit("goto " + loops.peek().breakLabel);
        return "";
    }

    @Override
    public String visit(ContinueStmt continueStmt) {
        if (!loops.isEmpty()) emit("goto " + loops.peek().continueLabel);
        return "";
    }

    @Override
    public String visit(OutputStmt outputStmt) {
        emit("getstatic java/lang/System/out Ljava/io/PrintStream;");
        emitExpression(outputStmt.getValue());
        emit("invokevirtual java/io/PrintStream/println(" + printDescriptor(inferType(outputStmt.getValue())) + ")V");
        return "";
    }

    @Override
    public String visit(ExprStmt exprStmt) {
        if (exprStmt.getExpression() != null) {
            String type = inferType(exprStmt.getExpression());
            emitExpression(exprStmt.getExpression());
            if (!VOID.equals(type)) emit(isWide(type) ? "pop2" : "pop");
        }
        return "";
    }

    @Override
    public String visit(Identifier identifier) {
        emitIdentifier(identifier.getName());
        return "";
    }

    @Override
    public String visit(IntLiteral intLiteral) {
        emit("ldc " + intLiteral.getValue());
        return "";
    }

    @Override
    public String visit(FloatLiteral floatLiteral) {
        String type = literalFloatingType(floatLiteral);
        if (DOUBLE.equals(type)) emit("ldc2_w " + floatLiteral.getValue());
        else emit("ldc " + ((float) floatLiteral.getValue()));
        return "";
    }

    @Override
    public String visit(BoolLiteral boolLiteral) {
        emit("ldc " + (boolLiteral.getValue() ? "1" : "0"));
        return "";
    }

    @Override
    public String visit(ThisExpr thisExpr) {
        emit("aload_0");
        return "";
    }

    @Override
    public String visit(UnaryExpr unaryExpr) {
        emitExpression(unaryExpr.getOperand());
        if (unaryExpr.getOperator() == UnaryOperator.MINUS) {
            String type = inferType(unaryExpr.getOperand());
            emit(DOUBLE.equals(type) ? "dneg" : FLOAT.equals(type) ? "fneg" : "ineg");
        }
        return "";
    }

    @Override
    public String visit(BinaryExpr binaryExpr) {
        BinaryOperator operator = binaryExpr.getOperator();
        if (isComparison(operator)) {
            emitBooleanValue(binaryExpr);
            return "";
        }
        String type = inferType(binaryExpr);
        emitExpressionAs(binaryExpr.getLeft(), type);
        emitExpressionAs(binaryExpr.getRight(), type);
        emit(arithmeticInstruction(operator, type));
        return "";
    }

    @Override
    public String visit(MemberAccessExpr memberAccessExpr) {
        emitExpression(memberAccessExpr.getInstance());
        FieldInfo field = resolveField(inferType(memberAccessExpr.getInstance()), memberAccessExpr.getMemberName());
        if (field == null) {
            emit("getfield " + inferType(memberAccessExpr.getInstance()) + "/"
                    + memberAccessExpr.getMemberName() + " Ljava/lang/Object;");
        } else {
            emit("getfield " + field.owner.name + "/" + field.name + " " + descriptor(field.type));
        }
        return "";
    }

    @Override
    public String visit(CallExpr callExpr) {
        emitCall(callExpr);
        return "";
    }

    private String emitModule(ModuleDeclaration module) {
        currentClass = classes.get(module.getName());
        beginClass(currentClass);
        header(currentClass);
        emitFields(currentClass);
        emitConstructors(currentClass);
        for (MethodDeclaration method : currentClass.methods) emitMethod(method);
        if ("Main".equals(currentClass.name) && hasMainMethod(currentClass)) emitStaticMainWrapper();
        return out.toString();
    }

    private String emitStruct(StructDeclaration struct) {
        currentClass = classes.get(struct.getName());
        beginClass(currentClass);
        header(currentClass);
        emitFields(currentClass);
        emitDefaultConstructor();
        emitStructFieldConstructor(currentClass);
        return out.toString();
    }

    private void indexProgram(Program program) {
        for (Declaration declaration : program.getDeclarations()) {
            if (declaration instanceof ModuleDeclaration) {
                ModuleDeclaration module = (ModuleDeclaration) declaration;
                ClassInfo info = new ClassInfo(module.getName(), true);
                info.includes.addAll(module.getIncludes());
                for (Declaration member : module.getMembers()) indexMember(info, member);
                classes.put(info.name, info);
            } else if (declaration instanceof StructDeclaration) {
                StructDeclaration struct = (StructDeclaration) declaration;
                ClassInfo info = new ClassInfo(struct.getName(), false);
                for (FieldDeclaration field : struct.getFields()) indexField(info, field);
                classes.put(info.name, info);
            }
        }
    }

    /** Writes included module sources before their dependent module source. */
    private void emitDeclaration(Declaration declaration, Map<String, Declaration> declarationsByName,
                                 Set<String> emitted, Set<String> visiting) {
        String name = declaration instanceof ModuleDeclaration ? ((ModuleDeclaration) declaration).getName()
                : declaration instanceof StructDeclaration ? ((StructDeclaration) declaration).getName() : null;
        if (name == null || emitted.contains(name)) return;
        if (!visiting.add(name)) return; // Cyclic includes are a semantic error; avoid recursive generation here.
        if (declaration instanceof ModuleDeclaration) {
            for (String included : ((ModuleDeclaration) declaration).getIncludes()) {
                Declaration includedDeclaration = declarationsByName.get(included);
                if (includedDeclaration != null) emitDeclaration(includedDeclaration, declarationsByName, emitted, visiting);
            }
        }
        if (declaration instanceof ModuleDeclaration) {
            generatedFiles.put(name + ".j", emitModule((ModuleDeclaration) declaration));
        } else {
            generatedFiles.put(name + ".j", emitStruct((StructDeclaration) declaration));
        }
        emitted.add(name);
        visiting.remove(name);
    }

    private void indexMember(ClassInfo owner, Declaration member) {
        if (member instanceof FieldDeclaration) indexField(owner, (FieldDeclaration) member);
        else if (member instanceof MethodDeclaration) owner.methods.add((MethodDeclaration) member);
        else if (member instanceof ConstructorDeclaration) owner.constructors.add((ConstructorDeclaration) member);
    }

    private void indexField(ClassInfo owner, FieldDeclaration field) {
        owner.fields.put(field.getName(), new FieldInfo(owner, field.getName(), typeName(field.getType()),
                field.getAccessModifier()));
    }

    private void beginClass(ClassInfo info) {
        out = new StringBuilder();
        slots.clear();
        localTypes.clear();
        loops.clear();
        currentClass = info;
    }

    private void header(ClassInfo info) {
        out.append(".class public ").append(info.name).append('\n');
        out.append(".super ").append(superName(info)).append("\n\n");
    }

    private void emitFields(ClassInfo info) {
        for (FieldInfo field : info.fields.values()) {
            out.append(".field ").append(access(field.access)).append(' ').append(field.name).append(' ')
                    .append(descriptor(field.type)).append('\n');
        }
        if (!info.fields.isEmpty()) out.append('\n');
    }

    private void emitConstructors(ClassInfo info) {
        boolean hasNoArgumentConstructor = false;
        for (ConstructorDeclaration constructor : info.constructors) {
            if (constructor.getParams().isEmpty()) hasNoArgumentConstructor = true;
            emitConstructor(constructor);
        }
        if (!hasNoArgumentConstructor) emitDefaultConstructor();
    }

    private void emitDefaultConstructor() {
        beginMethod(VOID, new ArrayList<Param>());
        out.append(".method public <init>()V\n");
        limits();
        emit("aload_0");
        emit("invokespecial " + superName(currentClass) + "/<init>()V");
        emit("return");
        out.append(".end method\n\n");
    }

    /** Emits the field-wise constructor used by `Point(x, y) variable` syntax. */
    private void emitStructFieldConstructor(ClassInfo info) {
        if (info.fields.isEmpty()) return;
        List<Param> params = new ArrayList<>();
        for (FieldInfo field : info.fields.values()) {
            Param parameter = new Param();
            parameter.setName(field.name);
            parameter.setType(new Type(field.type));
            params.add(parameter);
        }
        beginMethod(VOID, params);
        out.append(".method public <init>(").append(parametersDescriptor(params)).append(")V\n");
        limits();
        emit("aload_0");
        emit("invokespecial java/lang/Object/<init>()V");
        for (FieldInfo field : info.fields.values()) {
            emit("aload_0");
            emitLoad(field.type, slotOf(field.name));
            emit("putfield " + info.name + "/" + field.name + " " + descriptor(field.type));
        }
        emit("return");
        out.append(".end method\n\n");
    }

    private void emitConstructor(ConstructorDeclaration constructor) {
        beginMethod(VOID, constructor.getParams());
        out.append(".method ").append(access(constructor.getAccessModifier())).append(" <init>(")
                .append(parametersDescriptor(constructor.getParams())).append(")V\n");
        limits();
        emit("aload_0");
        emit("invokespecial " + superName(currentClass) + "/<init>()V");
        if (constructor.getBody() != null) constructor.getBody().accept(this);
        emit("return");
        out.append(".end method\n\n");
    }

    private void emitMethod(MethodDeclaration method) {
        String returnType = typeName(method.getReturnType());
        beginMethod(returnType, method.getParams());
        out.append(".method ").append(access(method.getAccessModifier())).append(' ').append(method.getName())
                .append('(').append(parametersDescriptor(method.getParams())).append(')')
                .append(descriptor(returnType)).append('\n');
        limits();
        if (method.getBody() != null) method.getBody().accept(this);
        if (method.getBody() == null || fallsThrough(method.getBody())) emitDefaultReturn(returnType);
        out.append(".end method\n\n");
    }

    private void emitStaticMainWrapper() {
        out.append(".method public static main([Ljava/lang/String;)V\n");
        limits();
        emit("new Main");
        emit("dup");
        emit("invokespecial Main/<init>()V");
        emit("invokevirtual Main/main()V");
        emit("return");
        out.append(".end method\n");
    }

    private void beginMethod(String returnType, List<Param> params) {
        slots.clear();
        localTypes.clear();
        loops.clear();
        currentReturnType = returnType;
        nextSlot = 1; // slot zero is the receiver (`this`) in every MOL method.
        for (Param param : params) {
            int slot = slotOf(param.getName());
            localTypes.put(param.getName(), typeName(param.getType()));
            // Wide values consume a second local-variable slot on the JVM.
            if (isWide(typeName(param.getType()))) nextSlot = Math.max(nextSlot, slot + 2);
        }
    }

    private void limits() {
        out.append("    .limit stack 128\n");
        out.append("    .limit locals 128\n");
    }

    private void emitAssignment(Assignment assignment) {
        if (assignment == null || assignment.getLeft() == null) return;
        Expression left = assignment.getLeft();
        if (left instanceof Identifier) {
            String name = ((Identifier) left).getName();
            String type = typeOfIdentifier(name);
            FieldInfo field = localTypes.containsKey(name) ? null : resolveField(currentClass.name, name);
            if (field == null) {
                emitExpressionAs(assignment.getRight(), type);
                emitStore(type, slotOf(name));
                if (!localTypes.containsKey(name)) localTypes.put(name, type);
            } else {
                emit("aload_0");
                emitExpressionAs(assignment.getRight(), field.type);
                emit("putfield " + field.owner.name + "/" + field.name + " " + descriptor(field.type));
            }
        } else if (left instanceof MemberAccessExpr) {
            MemberAccessExpr member = (MemberAccessExpr) left;
            FieldInfo field = resolveField(inferType(member.getInstance()), member.getMemberName());
            String owner = field == null ? inferType(member.getInstance()) : field.owner.name;
            String type = field == null ? inferType(assignment.getRight()) : field.type;
            emitExpression(member.getInstance());
            emitExpressionAs(assignment.getRight(), type);
            emit("putfield " + owner + "/" + member.getMemberName() + " " + descriptor(type));
        } else {
            // The grammar permits arbitrary expressions on the left.  Evaluating
            // the right side keeps the generated stack valid for malformed ASTs.
            emitExpression(assignment.getRight());
            emit(isWide(inferType(assignment.getRight())) ? "pop2" : "pop");
        }
    }

    private void emitExpression(Expression expression) {
        if (expression == null) {
            emit("aconst_null");
        } else {
            expression.accept(this);
        }
    }

    /** Emits an expression and performs the JVM numeric widening/narrowing required by its target slot. */
    private void emitExpressionAs(Expression expression, String targetType) {
        emitExpression(expression);
        emitNumericConversion(inferType(expression), targetType);
    }

    private void emitIdentifier(String name) {
        if (localTypes.containsKey(name)) {
            emitLoad(localTypes.get(name), slotOf(name));
            return;
        }
        FieldInfo field = resolveField(currentClass.name, name);
        if (field != null) {
            emit("aload_0");
            emit("getfield " + field.owner.name + "/" + field.name + " " + descriptor(field.type));
            return;
        }
        // An unresolved identifier is normally rejected by semantic analysis. A
        // null reference makes generated Jasmin verifiable even if it reaches us.
        emit("aconst_null");
    }

    private void emitNew(String type, List<Expression> args) {
        List<String> parameterTypes = constructorParameterTypes(type, args);
        emit("new " + type);
        emit("dup");
        emitArguments(args, parameterTypes);
        emit("invokespecial " + type + "/<init>(" + typesDescriptor(parameterTypes) + ")V");
    }

    private void emitCall(CallExpr call) {
        Expression callee = call.getCallee();
        if (callee instanceof MemberAccessExpr) {
            MemberAccessExpr member = (MemberAccessExpr) callee;
            String receiverType = inferType(member.getInstance());
            MethodInfo target = resolveMethod(receiverType, member.getMemberName(), call.getArgs());
            emitExpression(member.getInstance());
            emitArguments(call.getArgs(), target == null ? null : target.parameterTypes);
            emit("invokevirtual " + (target == null ? receiverType : target.owner.name) + "/"
                    + member.getMemberName() + "(" + (target == null ? argumentDescriptor(call.getArgs())
                    : typesDescriptor(target.parameterTypes)) + ")"
                    + descriptor(target == null ? "java/lang/Object" : target.returnType));
            return;
        }
        if (callee instanceof Identifier) {
            String name = ((Identifier) callee).getName();
            if (classes.containsKey(name)) {
                emitNew(name, call.getArgs());
                return;
            }
            MethodInfo target = resolveMethod(currentClass.name, name, call.getArgs());
            emit("aload_0");
            emitArguments(call.getArgs(), target == null ? null : target.parameterTypes);
            emit("invokevirtual " + (target == null ? currentClass.name : target.owner.name) + "/" + name
                    + "(" + (target == null ? argumentDescriptor(call.getArgs())
                    : typesDescriptor(target.parameterTypes)) + ")"
                    + descriptor(target == null ? "java/lang/Object" : target.returnType));
            return;
        }

        // A dynamic callee is outside MOL's static call model.  Still consume
        // its receiver and arguments before emitting a harmless object result.
        emitExpression(callee);
        emit("pop");
        for (Expression arg : call.getArgs()) {
            emitExpression(arg);
            emit(isWide(inferType(arg)) ? "pop2" : "pop");
        }
        emit("aconst_null");
    }

    private void emitBranch(Expression expression, String trueLabel, String falseLabel) {
        if (expression instanceof BoolLiteral) {
            emit("goto " + (((BoolLiteral) expression).getValue() ? trueLabel : falseLabel));
            return;
        }
        if (expression instanceof BinaryExpr && isComparison(((BinaryExpr) expression).getOperator())) {
            BinaryExpr binary = (BinaryExpr) expression;
            String leftType = inferType(binary.getLeft());
            String rightType = inferType(binary.getRight());
            String comparisonType = isReference(leftType) ? leftType : numericType(leftType, rightType);
            emitExpressionAs(binary.getLeft(), comparisonType);
            emitExpressionAs(binary.getRight(), comparisonType);
            emitComparisonJump(binary.getOperator(), comparisonType, trueLabel);
            emit("goto " + falseLabel);
            return;
        }
        emitExpression(expression);
        emit("ifne " + trueLabel);
        emit("goto " + falseLabel);
    }

    private void emitBooleanValue(BinaryExpr expression) {
        String trueLabel = label();
        String falseLabel = label();
        String after = label();
        emitBranch(expression, trueLabel, falseLabel);
        emitLabel(trueLabel);
        emit("ldc 1");
        emit("goto " + after);
        emitLabel(falseLabel);
        emit("ldc 0");
        emitLabel(after);
    }

    private void emitComparisonJump(BinaryOperator operator, String operandType, String trueLabel) {
        if (FLOAT.equals(operandType)) {
            emit("fcmpl");
            emit(floatComparisonJump(operator, trueLabel));
        } else if (DOUBLE.equals(operandType)) {
            emit("dcmpl");
            emit(floatComparisonJump(operator, trueLabel));
        } else if (isReference(operandType) && (operator == BinaryOperator.EQ || operator == BinaryOperator.NE)) {
            emit((operator == BinaryOperator.EQ ? "if_acmpeq " : "if_acmpne ") + trueLabel);
        } else {
            emit(intComparisonJump(operator, trueLabel));
        }
    }

    private String inferType(Expression expression) {
        if (expression == null) return "java/lang/Object";
        if (expression instanceof IntLiteral) return INT;
        if (expression instanceof BoolLiteral) return BOOL;
        if (expression instanceof FloatLiteral) return literalFloatingType((FloatLiteral) expression);
        if (expression instanceof ThisExpr) return currentClass == null ? "java/lang/Object" : currentClass.name;
        if (expression instanceof Identifier) return typeOfIdentifier(((Identifier) expression).getName());
        if (expression instanceof UnaryExpr) return inferType(((UnaryExpr) expression).getOperand());
        if (expression instanceof BinaryExpr) {
            BinaryExpr binary = (BinaryExpr) expression;
            return isComparison(binary.getOperator()) ? BOOL
                    : numericType(inferType(binary.getLeft()), inferType(binary.getRight()));
        }
        if (expression instanceof MemberAccessExpr) {
            MemberAccessExpr member = (MemberAccessExpr) expression;
            FieldInfo field = resolveField(inferType(member.getInstance()), member.getMemberName());
            return field == null ? "java/lang/Object" : field.type;
        }
        if (expression instanceof CallExpr) {
            CallExpr call = (CallExpr) expression;
            if (call.getCallee() instanceof MemberAccessExpr) {
                MemberAccessExpr member = (MemberAccessExpr) call.getCallee();
                MethodInfo method = resolveMethod(inferType(member.getInstance()), member.getMemberName(), call.getArgs());
                return method == null ? "java/lang/Object" : method.returnType;
            }
            if (call.getCallee() instanceof Identifier) {
                String name = ((Identifier) call.getCallee()).getName();
                if (classes.containsKey(name)) return name;
                MethodInfo method = resolveMethod(currentClass.name, name, call.getArgs());
                return method == null ? "java/lang/Object" : method.returnType;
            }
        }
        return "java/lang/Object";
    }

    private String typeOfIdentifier(String name) {
        String local = localTypes.get(name);
        if (local != null) return local;
        FieldInfo field = currentClass == null ? null : resolveField(currentClass.name, name);
        if (field != null) return field.type;
        return classes.containsKey(name) ? name : "java/lang/Object";
    }

    private boolean fallsThrough(BlockStmt block) {
        for (Statement statement : block.getStatements()) {
            if (!fallsThrough(statement)) return false;
        }
        return true;
    }

    private boolean fallsThrough(Statement statement) {
        if (statement instanceof ReturnStmt || statement instanceof BreakStmt || statement instanceof ContinueStmt) return false;
        if (statement instanceof BlockStmt) return fallsThrough((BlockStmt) statement);
        if (statement instanceof IfStmt) {
            IfStmt conditional = (IfStmt) statement;
            if (conditional.getElseBlock() == null || fallsThrough(conditional.getThenBlock())
                    || fallsThrough(conditional.getElseBlock())) return true;
            for (ElseIfClause clause : conditional.getElseIfClauses()) {
                if (fallsThrough(clause.getThenBlock())) return true;
            }
            return false;
        }
        // A loop can execute zero times, so control may always continue after it.
        return true;
    }

    private String literalFloatingType(FloatLiteral literal) {
        // The grammar has one floating literal syntax. Its surrounding declared
        // type determines double use; float is the language's default.
        return FLOAT;
    }

    private FieldInfo resolveField(String type, String name) {
        ClassInfo info = classes.get(type);
        while (info != null) {
            FieldInfo result = info.fields.get(name);
            if (result != null) return result;
            info = classes.get(superName(info));
        }
        return null;
    }

    private MethodInfo resolveMethod(String type, String name, List<Expression> args) {
        List<String> types = new ArrayList<>();
        for (Expression arg : args) types.add(inferType(arg));
        ClassInfo info = classes.get(type);
        while (info != null) {
            for (MethodDeclaration method : info.methods) {
                if (!method.getName().equals(name) || method.getParams().size() != types.size()) continue;
                boolean matches = true;
                for (int i = 0; i < types.size(); i++) {
                    if (!compatible(typeName(method.getParams().get(i).getType()), types.get(i))) {
                        matches = false;
                        break;
                    }
                }
                if (matches) return new MethodInfo(info, method);
            }
            info = classes.get(superName(info));
        }
        return null;
    }

    private List<String> constructorParameterTypes(String type, List<Expression> args) {
        ClassInfo info = classes.get(type);
        if (info != null) {
            for (ConstructorDeclaration constructor : info.constructors) {
                if (constructor.getParams().size() == args.size()) {
                    List<String> result = new ArrayList<>();
                    boolean matches = true;
                    for (int i = 0; i < args.size(); i++) {
                        String parameterType = typeName(constructor.getParams().get(i).getType());
                        if (!compatible(parameterType, inferType(args.get(i)))) {
                            matches = false;
                            break;
                        }
                        result.add(parameterType);
                    }
                    if (matches) return result;
                }
            }
            // Struct construction initializes fields in declaration order.
            if (!info.module && info.fields.size() == args.size()) {
                return new ArrayList<>(fieldTypes(info));
            }
        }
        List<String> inferred = new ArrayList<>();
        for (Expression argument : args) inferred.add(inferType(argument));
        return inferred;
    }

    private List<String> fieldTypes(ClassInfo info) {
        List<String> result = new ArrayList<>();
        for (FieldInfo field : info.fields.values()) result.add(field.type);
        return result;
    }

    private boolean compatible(String target, String actual) {
        return target.equals(actual) || "java/lang/Object".equals(actual)
                || (DOUBLE.equals(target) && FLOAT.equals(actual));
    }

    private String superName(ClassInfo info) {
        if (info != null && info.module && !info.includes.isEmpty() && classes.containsKey(info.includes.get(0))) {
            return info.includes.get(0);
        }
        return "java/lang/Object";
    }

    private boolean hasMainMethod(ClassInfo info) {
        for (MethodDeclaration method : info.methods) {
            if ("main".equals(method.getName()) && method.getParams().isEmpty()
                    && VOID.equals(typeName(method.getReturnType()))) return true;
        }
        return false;
    }

    private String typeName(Type type) {
        return type == null || type.getName() == null ? VOID : type.getName();
    }

    private String descriptor(String type) {
        if (INT.equals(type)) return "I";
        if (BOOL.equals(type)) return "Z";
        if (FLOAT.equals(type)) return "F";
        if (DOUBLE.equals(type)) return "D";
        if (CHAR.equals(type)) return "C";
        if (VOID.equals(type)) return "V";
        return "L" + type + ";";
    }

    private String parametersDescriptor(List<Param> params) {
        StringBuilder result = new StringBuilder();
        for (Param param : params) result.append(descriptor(typeName(param.getType())));
        return result.toString();
    }

    private String argumentDescriptor(List<Expression> args) {
        StringBuilder result = new StringBuilder();
        for (Expression arg : args) result.append(descriptor(inferType(arg)));
        return result.toString();
    }

    private String typesDescriptor(List<String> types) {
        StringBuilder result = new StringBuilder();
        for (String type : types) result.append(descriptor(type));
        return result.toString();
    }

    private void emitArguments(List<Expression> args, List<String> targetTypes) {
        for (int i = 0; i < args.size(); i++) {
            if (targetTypes != null && i < targetTypes.size()) emitExpressionAs(args.get(i), targetTypes.get(i));
            else emitExpression(args.get(i));
        }
    }

    private String access(AccessModifier modifier) {
        return modifier == AccessModifier.PRIVATE ? "private" : "public";
    }

    private void emitLoad(String type, int slot) {
        emit(localInstruction("load", type, slot));
    }

    private void emitStore(String type, int slot) {
        emit(localInstruction("store", type, slot));
    }

    private String localInstruction(String action, String type, int slot) {
        String prefix = isReference(type) ? "a" : DOUBLE.equals(type) ? "d" : FLOAT.equals(type) ? "f" : "i";
        return prefix + action + " " + slot;
    }

    private void emitDefaultValue(String type) {
        if (isReference(type)) emit("aconst_null");
        else if (DOUBLE.equals(type)) emit("ldc2_w 0.0");
        else if (FLOAT.equals(type)) emit("ldc 0.0");
        else emit("ldc 0");
    }

    private void emitDefaultReturn(String type) {
        if (VOID.equals(type)) emit("return");
        else {
            emitDefaultValue(type);
            emit(returnInstruction(type));
        }
    }

    private String returnInstruction(String type) {
        if (isReference(type)) return "areturn";
        if (DOUBLE.equals(type)) return "dreturn";
        if (FLOAT.equals(type)) return "freturn";
        return "ireturn";
    }

    private String printDescriptor(String type) {
        if (isReference(type)) return "Ljava/lang/Object;";
        return descriptor(type);
    }

    private String arithmeticInstruction(BinaryOperator operator, String type) {
        String prefix = DOUBLE.equals(type) ? "d" : FLOAT.equals(type) ? "f" : "i";
        switch (operator) {
            case MULT: return prefix + "mul";
            case DIV: return prefix + "div";
            case MOD: return prefix + "rem";
            case PLUS: return prefix + "add";
            case MINUS: return prefix + "sub";
            default: return "nop";
        }
    }

    private String numericType(String first, String second) {
        if (DOUBLE.equals(first) || DOUBLE.equals(second)) return DOUBLE;
        if (FLOAT.equals(first) || FLOAT.equals(second)) return FLOAT;
        return INT;
    }

    private void emitNumericConversion(String source, String target) {
        if (source.equals(target) || isReference(source) || isReference(target) || BOOL.equals(source) || BOOL.equals(target)) {
            return;
        }
        if (isIntegerLike(source)) {
            if (FLOAT.equals(target)) emit("i2f");
            else if (DOUBLE.equals(target)) emit("i2d");
            return;
        }
        if (FLOAT.equals(source)) {
            if (DOUBLE.equals(target)) emit("f2d");
            else if (isIntegerLike(target)) emit("f2i");
            return;
        }
        if (DOUBLE.equals(source)) {
            if (FLOAT.equals(target)) emit("d2f");
            else if (isIntegerLike(target)) emit("d2i");
        }
    }

    private boolean isIntegerLike(String type) {
        return INT.equals(type) || CHAR.equals(type);
    }

    private boolean isComparison(BinaryOperator operator) {
        return operator == BinaryOperator.LT || operator == BinaryOperator.GT || operator == BinaryOperator.LE
                || operator == BinaryOperator.GE || operator == BinaryOperator.EQ || operator == BinaryOperator.NE;
    }

    private boolean isReference(String type) {
        return !(INT.equals(type) || BOOL.equals(type) || FLOAT.equals(type) || DOUBLE.equals(type)
                || CHAR.equals(type) || VOID.equals(type));
    }

    private boolean isWide(String type) {
        return DOUBLE.equals(type);
    }

    private String intComparisonJump(BinaryOperator operator, String target) {
        switch (operator) {
            case LT: return "if_icmplt " + target;
            case GT: return "if_icmpgt " + target;
            case LE: return "if_icmple " + target;
            case GE: return "if_icmpge " + target;
            case EQ: return "if_icmpeq " + target;
            case NE: return "if_icmpne " + target;
            default: return "goto " + target;
        }
    }

    private String floatComparisonJump(BinaryOperator operator, String target) {
        switch (operator) {
            case LT: return "iflt " + target;
            case GT: return "ifgt " + target;
            case LE: return "ifle " + target;
            case GE: return "ifge " + target;
            case EQ: return "ifeq " + target;
            case NE: return "ifne " + target;
            default: return "goto " + target;
        }
    }

    private String label() {
        return "L" + nextLabel++;
    }

    private void emitLabel(String label) {
        out.append(label).append(":\n");
    }

    private void emit(String instruction) {
        out.append("    ").append(instruction).append('\n');
    }

    private static final class ClassInfo {
        private final String name;
        private final boolean module;
        private final List<String> includes = new ArrayList<>();
        private final Map<String, FieldInfo> fields = new LinkedHashMap<>();
        private final List<MethodDeclaration> methods = new ArrayList<>();
        private final List<ConstructorDeclaration> constructors = new ArrayList<>();

        private ClassInfo(String name, boolean module) {
            this.name = name;
            this.module = module;
        }
    }

    private static final class FieldInfo {
        private final ClassInfo owner;
        private final String name;
        private final String type;
        private final AccessModifier access;

        private FieldInfo(ClassInfo owner, String name, String type, AccessModifier access) {
            this.owner = owner;
            this.name = name;
            this.type = type;
            this.access = access;
        }
    }

    private static final class MethodInfo {
        private final ClassInfo owner;
        private final String returnType;
        private final List<String> parameterTypes = new ArrayList<>();

        private MethodInfo(ClassInfo owner, MethodDeclaration method) {
            this.owner = owner;
            this.returnType = method.getReturnType().getName();
            for (Param parameter : method.getParams()) this.parameterTypes.add(parameter.getType().getName());
        }
    }

    private static final class LoopLabels {
        private final String continueLabel;
        private final String breakLabel;

        private LoopLabels(String continueLabel, String breakLabel) {
            this.continueLabel = continueLabel;
            this.breakLabel = breakLabel;
        }
    }
}

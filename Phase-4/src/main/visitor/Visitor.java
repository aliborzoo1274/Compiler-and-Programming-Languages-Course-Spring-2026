package main.visitor;

import main.ast.nodes.*;
import main.ast.nodes.decl.*;
import main.ast.nodes.expr.*;
import main.ast.nodes.stmt.*;

public abstract class Visitor<T> implements ASTVisitor<T> {
    protected T defaultValue() { return null; }

    @Override
    public T visit(Program program) { return defaultValue(); }

    @Override
    public T visit(Type type) { return defaultValue(); }

    @Override
    public T visit(ModuleDeclaration moduleDeclaration) { return defaultValue(); }

    @Override
    public T visit(StructDeclaration structDeclaration) { return defaultValue(); }

    @Override
    public T visit(FieldDeclaration fieldDeclaration) { return defaultValue(); }

    @Override
    public T visit(MethodDeclaration methodDeclaration) { return defaultValue(); }

    @Override
    public T visit(ConstructorDeclaration constructorDeclaration) { return defaultValue(); }

    @Override
    public T visit(Param param) { return defaultValue(); }

    @Override
    public T visit(BlockStmt blockStmt) { return defaultValue(); }

    @Override
    public T visit(VarDeclarationStmt varDeclarationStmt) { return defaultValue(); }

    @Override
    public T visit(Assignment assignment) { return defaultValue(); }

    @Override
    public T visit(AssignmentStmt assignmentStmt) { return defaultValue(); }

    @Override
    public T visit(IfStmt ifStmt) { return defaultValue(); }

    @Override
    public T visit(ElseIfClause elseIfClause) { return defaultValue(); }

    @Override
    public T visit(WhileStmt whileStmt) { return defaultValue(); }

    @Override
    public T visit(ForInitDecl forInitDecl) { return defaultValue(); }

    @Override
    public T visit(ForInitAssign forInitAssign) { return defaultValue(); }

    @Override
    public T visit(ForUpdateAssign forUpdateAssign) { return defaultValue(); }

    @Override
    public T visit(ForStmt forStmt) { return defaultValue(); }

    @Override
    public T visit(ReturnStmt returnStmt) { return defaultValue(); }

    @Override
    public T visit(BreakStmt breakStmt) { return defaultValue(); }

    @Override
    public T visit(ContinueStmt continueStmt) { return defaultValue(); }

    @Override
    public T visit(OutputStmt outputStmt) { return defaultValue(); }

    @Override
    public T visit(ExprStmt exprStmt) { return defaultValue(); }

    @Override
    public T visit(Identifier identifier) { return defaultValue(); }

    @Override
    public T visit(IntLiteral intLiteral) { return defaultValue(); }

    @Override
    public T visit(FloatLiteral floatLiteral) { return defaultValue(); }

    @Override
    public T visit(BoolLiteral boolLiteral) { return defaultValue(); }

    @Override
    public T visit(ThisExpr thisExpr) { return defaultValue(); }

    @Override
    public T visit(UnaryExpr unaryExpr) { return defaultValue(); }

    @Override
    public T visit(BinaryExpr binaryExpr) { return defaultValue(); }

    @Override
    public T visit(MemberAccessExpr memberAccessExpr) { return defaultValue(); }

    @Override
    public T visit(CallExpr callExpr) { return defaultValue(); }
}

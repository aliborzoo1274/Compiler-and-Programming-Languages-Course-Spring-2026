package main.visitor;

import main.ast.nodes.*;
import main.ast.nodes.decl.*;
import main.ast.nodes.expr.*;
import main.ast.nodes.stmt.*;

public interface ASTVisitor<T> {
    T visit(Program program);
    T visit(Type type);

    T visit(ModuleDeclaration moduleDeclaration);
    T visit(StructDeclaration structDeclaration);
    T visit(FieldDeclaration fieldDeclaration);
    T visit(MethodDeclaration methodDeclaration);
    T visit(ConstructorDeclaration constructorDeclaration);
    T visit(Param param);

    T visit(BlockStmt blockStmt);
    T visit(VarDeclarationStmt varDeclarationStmt);
    T visit(Assignment assignment);
    T visit(AssignmentStmt assignmentStmt);
    T visit(IfStmt ifStmt);
    T visit(ElseIfClause elseIfClause);
    T visit(WhileStmt whileStmt);
    T visit(ForInitDecl forInitDecl);
    T visit(ForInitAssign forInitAssign);
    T visit(ForUpdateAssign forUpdateAssign);
    T visit(ForStmt forStmt);
    T visit(ReturnStmt returnStmt);
    T visit(BreakStmt breakStmt);
    T visit(ContinueStmt continueStmt);
    T visit(OutputStmt outputStmt);
    T visit(ExprStmt exprStmt);

    T visit(Identifier identifier);
    T visit(IntLiteral intLiteral);
    T visit(FloatLiteral floatLiteral);
    T visit(BoolLiteral boolLiteral);
    T visit(ThisExpr thisExpr);
    T visit(UnaryExpr unaryExpr);
    T visit(BinaryExpr binaryExpr);
    T visit(MemberAccessExpr memberAccessExpr);
    T visit(CallExpr callExpr);
}

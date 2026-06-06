// Generated from c:/Users/Ali Borzoozadeh/UT Programming/Compiler-and-Programming-Languages-Course-Spring-2026/Phase-2/src/main/grammar/MOL.g4 by ANTLR 4.13.2

    package main.grammar;

    import java.util.ArrayList;
    import java.util.List;

    import main.ast.nodes.*;
    import main.ast.nodes.decl.*;
    import main.ast.nodes.expr.*;
    import main.ast.nodes.stmt.*;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MOLParser}.
 */
public interface MOLListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MOLParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(MOLParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MOLParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(MOLParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MOLParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(MOLParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MOLParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(MOLParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MOLParser#moduleDecl}.
	 * @param ctx the parse tree
	 */
	void enterModuleDecl(MOLParser.ModuleDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MOLParser#moduleDecl}.
	 * @param ctx the parse tree
	 */
	void exitModuleDecl(MOLParser.ModuleDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MOLParser#structDecl}.
	 * @param ctx the parse tree
	 */
	void enterStructDecl(MOLParser.StructDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MOLParser#structDecl}.
	 * @param ctx the parse tree
	 */
	void exitStructDecl(MOLParser.StructDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MOLParser#memberDecl}.
	 * @param ctx the parse tree
	 */
	void enterMemberDecl(MOLParser.MemberDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MOLParser#memberDecl}.
	 * @param ctx the parse tree
	 */
	void exitMemberDecl(MOLParser.MemberDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MOLParser#fieldDecl}.
	 * @param ctx the parse tree
	 */
	void enterFieldDecl(MOLParser.FieldDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MOLParser#fieldDecl}.
	 * @param ctx the parse tree
	 */
	void exitFieldDecl(MOLParser.FieldDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MOLParser#methodDecl}.
	 * @param ctx the parse tree
	 */
	void enterMethodDecl(MOLParser.MethodDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MOLParser#methodDecl}.
	 * @param ctx the parse tree
	 */
	void exitMethodDecl(MOLParser.MethodDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MOLParser#constructorDecl}.
	 * @param ctx the parse tree
	 */
	void enterConstructorDecl(MOLParser.ConstructorDeclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MOLParser#constructorDecl}.
	 * @param ctx the parse tree
	 */
	void exitConstructorDecl(MOLParser.ConstructorDeclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MOLParser#accessModifier}.
	 * @param ctx the parse tree
	 */
	void enterAccessModifier(MOLParser.AccessModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link MOLParser#accessModifier}.
	 * @param ctx the parse tree
	 */
	void exitAccessModifier(MOLParser.AccessModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link MOLParser#paramList}.
	 * @param ctx the parse tree
	 */
	void enterParamList(MOLParser.ParamListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MOLParser#paramList}.
	 * @param ctx the parse tree
	 */
	void exitParamList(MOLParser.ParamListContext ctx);
	/**
	 * Enter a parse tree produced by {@link MOLParser#param}.
	 * @param ctx the parse tree
	 */
	void enterParam(MOLParser.ParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link MOLParser#param}.
	 * @param ctx the parse tree
	 */
	void exitParam(MOLParser.ParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link MOLParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(MOLParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link MOLParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(MOLParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BlockStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBlockStmt(MOLParser.BlockStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BlockStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBlockStmt(MOLParser.BlockStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code VarDeclarationStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterVarDeclarationStmt(MOLParser.VarDeclarationStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code VarDeclarationStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitVarDeclarationStmt(MOLParser.VarDeclarationStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AssignmentStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentStmt(MOLParser.AssignmentStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AssignmentStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentStmt(MOLParser.AssignmentStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IfStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterIfStmt(MOLParser.IfStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IfStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitIfStmt(MOLParser.IfStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code WhileStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterWhileStmt(MOLParser.WhileStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code WhileStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitWhileStmt(MOLParser.WhileStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ForStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterForStmt(MOLParser.ForStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ForStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitForStmt(MOLParser.ForStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ReturnStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStmt(MOLParser.ReturnStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ReturnStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStmt(MOLParser.ReturnStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BreakStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterBreakStmt(MOLParser.BreakStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BreakStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitBreakStmt(MOLParser.BreakStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ContinueStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterContinueStmt(MOLParser.ContinueStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ContinueStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitContinueStmt(MOLParser.ContinueStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OutputStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterOutputStmt(MOLParser.OutputStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OutputStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitOutputStmt(MOLParser.OutputStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ExprStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterExprStmt(MOLParser.ExprStmtContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ExprStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitExprStmt(MOLParser.ExprStmtContext ctx);
	/**
	 * Enter a parse tree produced by {@link MOLParser#varDeclStmt}.
	 * @param ctx the parse tree
	 */
	void enterVarDeclStmt(MOLParser.VarDeclStmtContext ctx);
	/**
	 * Exit a parse tree produced by {@link MOLParser#varDeclStmt}.
	 * @param ctx the parse tree
	 */
	void exitVarDeclStmt(MOLParser.VarDeclStmtContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ForInitDecl}
	 * labeled alternative in {@link MOLParser#forInit}.
	 * @param ctx the parse tree
	 */
	void enterForInitDecl(MOLParser.ForInitDeclContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ForInitDecl}
	 * labeled alternative in {@link MOLParser#forInit}.
	 * @param ctx the parse tree
	 */
	void exitForInitDecl(MOLParser.ForInitDeclContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ForInitAssign}
	 * labeled alternative in {@link MOLParser#forInit}.
	 * @param ctx the parse tree
	 */
	void enterForInitAssign(MOLParser.ForInitAssignContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ForInitAssign}
	 * labeled alternative in {@link MOLParser#forInit}.
	 * @param ctx the parse tree
	 */
	void exitForInitAssign(MOLParser.ForInitAssignContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ForUpdateAssign}
	 * labeled alternative in {@link MOLParser#forUpdate}.
	 * @param ctx the parse tree
	 */
	void enterForUpdateAssign(MOLParser.ForUpdateAssignContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ForUpdateAssign}
	 * labeled alternative in {@link MOLParser#forUpdate}.
	 * @param ctx the parse tree
	 */
	void exitForUpdateAssign(MOLParser.ForUpdateAssignContext ctx);
	/**
	 * Enter a parse tree produced by {@link MOLParser#argList}.
	 * @param ctx the parse tree
	 */
	void enterArgList(MOLParser.ArgListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MOLParser#argList}.
	 * @param ctx the parse tree
	 */
	void exitArgList(MOLParser.ArgListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code RelExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterRelExpr(MOLParser.RelExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code RelExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitRelExpr(MOLParser.RelExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code EqExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterEqExpr(MOLParser.EqExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code EqExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitEqExpr(MOLParser.EqExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MultExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMultExpr(MOLParser.MultExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MultExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMultExpr(MOLParser.MultExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PrimaryExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExpr(MOLParser.PrimaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PrimaryExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExpr(MOLParser.PrimaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AddExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAddExpr(MOLParser.AddExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AddExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAddExpr(MOLParser.AddExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code CallExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterCallExpr(MOLParser.CallExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code CallExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitCallExpr(MOLParser.CallExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MemberAccessExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMemberAccessExpr(MOLParser.MemberAccessExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MemberAccessExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMemberAccessExpr(MOLParser.MemberAccessExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code UnaryExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpr(MOLParser.UnaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code UnaryExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpr(MOLParser.UnaryExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link MOLParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(MOLParser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link MOLParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(MOLParser.PrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link MOLParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(MOLParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MOLParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(MOLParser.TypeContext ctx);
}
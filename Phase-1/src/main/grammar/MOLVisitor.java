// Generated from c:/Users/Ali Borzoozadeh/UT Programming/Compiler-and-Programming-Languages-Course-Spring-2026/Phase-1/src/main/grammar/MOL.g4 by ANTLR 4.13.2

    package main.grammar;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MOLParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MOLVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MOLParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(MOLParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link MOLParser#declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclaration(MOLParser.DeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MOLParser#moduleDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModuleDecl(MOLParser.ModuleDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MOLParser#structDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructDecl(MOLParser.StructDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MOLParser#memberDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberDecl(MOLParser.MemberDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MOLParser#fieldDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFieldDecl(MOLParser.FieldDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MOLParser#methodDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethodDecl(MOLParser.MethodDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MOLParser#constructorDecl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructorDecl(MOLParser.ConstructorDeclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MOLParser#accessModifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAccessModifier(MOLParser.AccessModifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link MOLParser#paramList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParamList(MOLParser.ParamListContext ctx);
	/**
	 * Visit a parse tree produced by {@link MOLParser#param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParam(MOLParser.ParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link MOLParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(MOLParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BlockStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlockStmt(MOLParser.BlockStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code VarDeclarationStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDeclarationStmt(MOLParser.VarDeclarationStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AssignmentStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentStmt(MOLParser.AssignmentStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code IfStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStmt(MOLParser.IfStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code WhileStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileStmt(MOLParser.WhileStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ForStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForStmt(MOLParser.ForStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ReturnStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStmt(MOLParser.ReturnStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code BreakStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBreakStmt(MOLParser.BreakStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ContinueStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitContinueStmt(MOLParser.ContinueStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code OutputStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOutputStmt(MOLParser.OutputStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ExprStmt}
	 * labeled alternative in {@link MOLParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprStmt(MOLParser.ExprStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MOLParser#varDeclStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarDeclStmt(MOLParser.VarDeclStmtContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ForInitDecl}
	 * labeled alternative in {@link MOLParser#forInit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForInitDecl(MOLParser.ForInitDeclContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ForInitAssign}
	 * labeled alternative in {@link MOLParser#forInit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForInitAssign(MOLParser.ForInitAssignContext ctx);
	/**
	 * Visit a parse tree produced by the {@code ForUpdateAssign}
	 * labeled alternative in {@link MOLParser#forUpdate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForUpdateAssign(MOLParser.ForUpdateAssignContext ctx);
	/**
	 * Visit a parse tree produced by {@link MOLParser#argList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgList(MOLParser.ArgListContext ctx);
	/**
	 * Visit a parse tree produced by the {@code RelExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelExpr(MOLParser.RelExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code EqExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqExpr(MOLParser.EqExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MultExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultExpr(MOLParser.MultExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code PrimaryExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimaryExpr(MOLParser.PrimaryExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code AddExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddExpr(MOLParser.AddExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code CallExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCallExpr(MOLParser.CallExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code MemberAccessExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberAccessExpr(MOLParser.MemberAccessExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code UnaryExpr}
	 * labeled alternative in {@link MOLParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryExpr(MOLParser.UnaryExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MOLParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(MOLParser.PrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link MOLParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(MOLParser.TypeContext ctx);
}
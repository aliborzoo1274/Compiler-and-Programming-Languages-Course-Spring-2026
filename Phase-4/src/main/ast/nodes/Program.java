package main.ast.nodes;

import main.ast.nodes.decl.Declaration;
import main.visitor.ASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class Program extends Node {
	private final List<Declaration> declarations = new ArrayList<>();

	public List<Declaration> getDeclarations() { return declarations; }
	public void setDeclarations(List<Declaration> declarations) {
		this.declarations.clear();
		if (declarations != null) {
			this.declarations.addAll(declarations);
		}
	}
	public void addDeclaration(Declaration declaration) { this.declarations.add(declaration); }

	@Override
	public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}

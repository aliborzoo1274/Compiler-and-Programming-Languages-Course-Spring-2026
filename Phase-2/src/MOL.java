import main.ast.nodes.Program;
import main.grammar.MOLLexer;
import main.grammar.MOLParser;
import main.visitor.PrintVisitor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.IOException;

public class MOL {
	public static void main(String[] args) throws IOException {
		CharStream reader = CharStreams.fromFileName(args[0]);
		MOLLexer molLexer = new MOLLexer(reader);
		CommonTokenStream tokens = new CommonTokenStream(molLexer);
		MOLParser molParser = new MOLParser(tokens);
		Program program = molParser.program().ASTNode;
		System.out.println();
		new PrintVisitor().visit(program);
	}
}

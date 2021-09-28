package de.ollie.blueprints.codereader.java;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import de.ollie.blueprints.codereader.java.antlr.Java8BaseListener;
import de.ollie.blueprints.codereader.java.antlr.Java8Lexer;
import de.ollie.blueprints.codereader.java.antlr.Java8Parser;
import de.ollie.blueprints.codereader.java.model.CompilationUnit;

/**
 * A reader for Java source code files.
 *
 * @author ollie (13.04.2020)
 */
public class JavaCodeConverter {

	/**
	 * Converts a Java source file content into a compilation unit object.
	 * 
	 * @param javaSourceFileContent The Java source file content which is to convert to a compilation unit object.
	 * @return The compilation unit object which is made from the passed Java source file content.
	 */
	public CompilationUnit convert(String javaSourceFileContent) {
		if (javaSourceFileContent == null) {
			return null;
		}
		CompilationUnit compilationUnit = new CompilationUnit();
		Java8Lexer lexer = new Java8Lexer(CharStreams.fromString(javaSourceFileContent));
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		Java8Parser parser = new Java8Parser(tokens);
		ParseTree tree = parser.compilationUnit();
		ParseTreeWalker walker = new ParseTreeWalker();
		Java8BaseListener listener = new JavaCodeConverterListener(compilationUnit);
		walker.walk(listener, tree);
		return compilationUnit;
	}

}
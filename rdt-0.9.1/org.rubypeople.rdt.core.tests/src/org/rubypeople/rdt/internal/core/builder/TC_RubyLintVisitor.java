package org.rubypeople.rdt.internal.core.builder;

import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;

import org.jruby.ast.Node;
import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.core.parser.warnings.DelegatingVisitor;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public class TC_RubyLintVisitor extends TestCase {
	
	private List<CategorizedProblem> problems;
	
	public void testUnlessModififerDoesntCreateEmptyConditionalWarning() throws Exception {
		runLint("@var = 3 unless @blah");
		assertEquals(0, problems.size());
	}
	

	public void testUnlessConditionalDoesntCreateEmptyConditionalWarning() throws Exception {
		runLint("unless @blah\n  @var = 3\nend");
		assertEquals(0, problems.size());
	}

	private void runLint(String contents) {
		RubyParser parser = new RubyParser();
		Node rootNode = parser.parse(new ShamFile("fake/path.rb"), new StringReader(contents));
		List<RubyLintVisitor> visitors = DelegatingVisitor.createVisitors(contents);
		DelegatingVisitor visitor = new DelegatingVisitor(visitors);
		rootNode.accept(visitor);
		problems = visitor.getProblems();
	}
}

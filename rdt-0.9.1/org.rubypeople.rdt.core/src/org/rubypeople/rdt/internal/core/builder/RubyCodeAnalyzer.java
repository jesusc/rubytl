package org.rubypeople.rdt.internal.core.builder;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.jruby.ast.Node;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.compiler.BuildContext;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.core.compiler.CompilationParticipant;
import org.rubypeople.rdt.core.compiler.IProblem;
import org.rubypeople.rdt.core.parser.warnings.DelegatingVisitor;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;
import org.rubypeople.rdt.internal.core.parser.Error;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public class RubyCodeAnalyzer extends CompilationParticipant {

	private RubyParser parser;

	public RubyCodeAnalyzer() {
		this.parser = new RubyParser();
	}
	
	@Override
	public boolean isActive(IRubyProject project) {
		return true;
	}
	
	@Override
	public void buildStarting(BuildContext[] files, boolean isBatch) {
		for (int i = 0; i < files.length; i++) {
			BuildContext context = files[i];
		
			IFile file = context.getFile();		
			String contents = new String(context.getContents());
			Reader reader = new StringReader(contents);
			try {
				Node rootNode = parser.parse(file, reader);
				if (rootNode == null)
					return;
				List<RubyLintVisitor> visitors = DelegatingVisitor.createVisitors(contents);
				DelegatingVisitor visitor = new DelegatingVisitor(visitors);
				rootNode.accept(visitor);
				
				List<CategorizedProblem> problems = visitor.getProblems();		
				context.recordNewProblems(problems.toArray(new CategorizedProblem[problems.size()]));
			} catch (SyntaxException e) {
				context.recordNewProblems(new CategorizedProblem[] {new Error(e.getPosition(), e.getMessage(), IProblem.Syntax) });
			} finally {
				IoUtils.closeQuietly(reader);
			}
		}
	}

}

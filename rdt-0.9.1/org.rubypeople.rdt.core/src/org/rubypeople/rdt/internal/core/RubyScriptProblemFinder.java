/**
 * 
 */
package org.rubypeople.rdt.internal.core;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.jruby.ast.Node;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.IRubyModelMarker;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.core.parser.warnings.DelegatingVisitor;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;
import org.rubypeople.rdt.internal.core.parser.Error;
import org.rubypeople.rdt.internal.core.parser.RdtWarnings;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.parser.TaskParser;
import org.rubypeople.rdt.internal.core.parser.TaskTag;

/**
 * @author Chris
 * 
 */
public class RubyScriptProblemFinder {

    // DSC convert to ImmediateWarnings
    public static void process(RubyScript script, char[] charContents, HashMap problems, IProgressMonitor pm) {
        RdtWarnings warnings = new RdtWarnings();
        RubyParser parser = new RubyParser(warnings);
        String contents = new String(charContents);

        List<CategorizedProblem> generatedProblems = runLint(script, parser, contents); // FIXME Make all these compilationParticipants

        TaskParser taskParser = new TaskParser(script.getRubyProject().getOptions(true));
        taskParser.parse(contents);
        generatedProblems.addAll(warnings.getWarnings());
        List<TaskTag> tasks = taskParser.getTasks();
        problems.put(IRubyModelMarker.RUBY_MODEL_PROBLEM_MARKER, generatedProblems.toArray(new CategorizedProblem[generatedProblems.size()]));
        problems.put(IRubyModelMarker.TASK_MARKER, tasks.toArray(new CategorizedProblem[tasks.size()]));
    }

	private static List<CategorizedProblem> runLint(RubyScript script, RubyParser parser, String contents) {
		try {
            Node node = parser.parse((IFile) script.getUnderlyingResource(), new StringReader(contents));
            if (node == null) return new ArrayList<CategorizedProblem>();
            List<RubyLintVisitor> visitors = DelegatingVisitor.createVisitors(contents);
            DelegatingVisitor visitor = new DelegatingVisitor(visitors);
            node.accept(visitor);
            return visitor.getProblems();
        } catch (SyntaxException e) {
        	List<CategorizedProblem> list = new ArrayList<CategorizedProblem>();
        	list.add(new Error(e.getPosition(), e.getMessage()));
        	return list;
        } catch (RubyModelException e) {
			RubyCore.log(e);
		}
        return new ArrayList<CategorizedProblem>();
	}
}

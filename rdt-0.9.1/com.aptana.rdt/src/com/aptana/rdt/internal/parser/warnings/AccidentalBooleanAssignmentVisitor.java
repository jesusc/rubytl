package com.aptana.rdt.internal.parser.warnings;

import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.GlobalAsgnNode;
import org.jruby.ast.IfNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.Node;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.AptanaRDTPlugin;

public class AccidentalBooleanAssignmentVisitor extends RubyLintVisitor {

	public AccidentalBooleanAssignmentVisitor(String contents) {
		super(AptanaRDTPlugin.getDefault().getOptions(), contents);
	}

	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_POSSIBLE_ACCIDENTAL_BOOLEAN_ASSIGNMENT;
	}
	
	public Instruction visitIfNode(IfNode iVisited) {
		Node condition = iVisited.getCondition();
		if (containsAssignment(condition)) {
			createProblem(condition.getPosition(), "Possible accidental boolean assignment");
		}
		return super.visitIfNode(iVisited);
	}
	
	private boolean containsAssignment(Node condition) {
		// FIXME Dive into this node branch recursively and see if it contains any assignment nodes
		return condition instanceof LocalAsgnNode || condition instanceof GlobalAsgnNode || condition instanceof InstAsgnNode || condition instanceof ClassVarAsgnNode;
	}

}

package com.aptana.rdt.internal.parser.warnings;

import org.jruby.ast.ClassNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.Node;
import org.jruby.ast.SuperNode;
import org.jruby.ast.ZSuperNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.AptanaRDTPlugin;

public class SubclassCallsSuper extends RubyLintVisitor {

	private boolean inConstructor;
	private boolean calledSuper;
	private boolean isSubClass;
	
	public SubclassCallsSuper(String contents) {
		super(AptanaRDTPlugin.getDefault().getOptions(), contents);
	}

	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_SUBCLASS_DOESNT_CALL_SUPER;
	}
	
	@Override
	public Instruction visitClassNode(ClassNode iVisited) {
		Node superNode = iVisited.getSuperNode();
		if (superNode != null) isSubClass = true;
		return super.visitClassNode(iVisited);
	}
	
	@Override
	public void exitClassNode(ClassNode iVisited) {
		isSubClass = false;
		super.exitClassNode(iVisited);
	}

	@Override
	public Instruction visitDefnNode(DefnNode iVisited) {
		if (!isSubClass) return null;
		if (!iVisited.getName().equals("initialize")) return null;
		inConstructor = true;
		calledSuper = false;
		
		return super.visitDefnNode(iVisited);
	}
	
	@Override
	public Instruction visitSuperNode(SuperNode iVisited) {
		if (inConstructor) calledSuper = true;
		return super.visitSuperNode(iVisited);
	}
	
	@Override
	public Instruction visitZSuperNode(ZSuperNode iVisited) {
		if (inConstructor) calledSuper = true;
		return super.visitZSuperNode(iVisited);
	}
	
	@Override
	public void exitDefnNode(DefnNode iVisited) {
		if (!isSubClass) return;
		if (!iVisited.getName().equals("initialize")) return;
		if (!calledSuper) {
			createProblem(iVisited.getNameNode().getPosition(), "Subclass does not call super in constructor");
		}
		inConstructor = false;
		super.exitDefnNode(iVisited);
	}
	
}

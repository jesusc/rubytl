package com.aptana.rdt.internal.parser.warnings;

import java.util.ArrayList;
import java.util.HashSet;

import org.jruby.ast.ClassNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.AptanaRDTPlugin;

public class LocalsMaskingMethodsVisitor extends RubyLintVisitor {

	private ArrayList<LocalAsgnNode> locals;
	private HashSet<String> methods;
	
	public LocalsMaskingMethodsVisitor(String contents) {
		super(AptanaRDTPlugin.getDefault().getOptions(), contents);
		locals = new ArrayList<LocalAsgnNode>();
		methods = new HashSet<String>();
	}

	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_LOCAL_MASKS_METHOD;
	}
	
	public Instruction visitClassNode(ClassNode iVisited) {
		methods.clear();
		locals.clear();
		return null;
	}
	
	@Override
	public void exitClassNode(ClassNode iVisited) {
		findMaskingLocals();
	}
	
	public Instruction visitDefnNode(DefnNode iVisited) {
		methods.add(iVisited.getName());
		return null;
	}
	
	public Instruction visitLocalAsgnNode(LocalAsgnNode iVisited) {
		locals.add(iVisited);
		return null;
	}
	
	private void findMaskingLocals() {
		for (LocalAsgnNode local : locals) {
			if (methods.contains(local.getName())) {
				createProblem(local.getPosition(), "Local variable hides method");
			}
		}
	}

}

package com.aptana.rdt.internal.core.parser.warnings;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.jruby.ast.Node;
import org.rubypeople.rdt.core.compiler.IProblem;
import org.rubypeople.rdt.core.parser.warnings.DelegatingVisitor;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public abstract class WarningVisitorTest extends TestCase {

	private RubyParser parser;
	private DelegatingVisitor visitor;

	@Override
	protected void setUp() throws Exception {
		super.setUp();		
		parser = new RubyParser();
	}

	protected void parse(String code) {
		Node root = parser.parse(code);
		List<RubyLintVisitor> visitors = new ArrayList<RubyLintVisitor>();
		visitors.add(createVisitor(code));
		visitor = new DelegatingVisitor(visitors);
		root.accept(visitor);
	}

	public int numberOfProblems() {
		return visitor.getProblems().size();
	}
	
	protected IProblem getProblemAtLine(int i) {
		return visitor.getProblems().get(i);
	}
	
	abstract protected RubyLintVisitor createVisitor(String code);
}

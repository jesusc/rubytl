package com.aptana.rdt.internal.core.parser.warnings;

import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.internal.parser.warnings.TooManyLocalsVisitor;


public class TC_CodeComplexity extends WarningVisitorTest {

	private static final int MAX_LINES = 20;
	
	@Override
	protected RubyLintVisitor createVisitor(String code) {
		return new TooManyLocalsVisitor(code);
	}
	
	// TODO Add tests for max branches
	// TODO Add tests for max method params
	// TODO Add tests for max locals
	// TODO Add tests for max returns
	
	public void testTooManyLines() throws Exception {
		StringBuffer buffer = new StringBuffer();
		buffer.append("def method\n");
		for (int i = 1; i <= MAX_LINES + 1; i++) {
			buffer.append("  # line ");
			buffer.append(i);
			buffer.append("\n");
		}
		buffer.append("end\n");
		parse(buffer.toString());
		assertEquals(1, numberOfProblems());
	}
	
	public void testEqualToMaxLines() throws Exception {
		StringBuffer buffer = new StringBuffer();
		buffer.append("def method\n");
		for (int i = 1; i <= MAX_LINES; i++) {
			buffer.append("  # line ");
			buffer.append(i);
			buffer.append("\n");
		}
		buffer.append("end\n");
		parse(buffer.toString());
		assertEquals(0, numberOfProblems());
	}
	
	public void testLessThanMaxLines() throws Exception {
		StringBuffer buffer = new StringBuffer();
		buffer.append("def method\n");
		for (int i = 1; i <= MAX_LINES - 1; i++) {
			buffer.append("  # line ");
			buffer.append(i);
			buffer.append("\n");
		}
		buffer.append("end\n");
		parse(buffer.toString());
		assertEquals(0, numberOfProblems());
	}

}

package com.aptana.rdt.internal.core.parser.warnings;


import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_ParserWarnings {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Parser warnings");
		//$JUnit-BEGIN$
		suite.addTestSuite(TC_SimilarVariableNameVisitor.class);
		suite.addTestSuite(TC_UnecessaryElseVisitor.class);
		suite.addTestSuite(TC_CodeComplexity.class);
		suite.addTestSuite(TC_ComparableInclusionVisitor.class);
		suite.addTestSuite(TC_EnumerableInclusionVisitor.class);
		//$JUnit-END$
		return suite;
	}

}

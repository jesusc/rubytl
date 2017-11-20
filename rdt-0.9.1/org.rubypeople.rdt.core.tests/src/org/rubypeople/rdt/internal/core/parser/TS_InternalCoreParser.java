package org.rubypeople.rdt.internal.core.parser;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_InternalCoreParser {

    public static Test suite() {
        TestSuite suite = new TestSuite("Parser");
    
        suite.addTestSuite(TC_TaskParser.class);
        suite.addTestSuite(TC_ImmediateWarnings.class);
        suite.addTestSuite(TC_RubyParser.class);

        return suite;
    }
}

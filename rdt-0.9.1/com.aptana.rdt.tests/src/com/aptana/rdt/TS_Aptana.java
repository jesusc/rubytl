package com.aptana.rdt;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.rdt.internal.core.parser.warnings.TS_ParserWarnings;

public class TS_Aptana {
    public static Test suite() {
        TestSuite suite = new TestSuite("Test for com.aptana.rdt");
        suite.addTest(TS_ParserWarnings.suite());
        return suite;
    }
}

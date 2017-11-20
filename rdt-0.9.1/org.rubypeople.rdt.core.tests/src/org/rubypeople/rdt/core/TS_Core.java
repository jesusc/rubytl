/*
 * Author: 
 *
 * Copyright (c) 2005 RubyPeople.
 *
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
 */
package org.rubypeople.rdt.core;


import junit.framework.Test;
import junit.framework.TestSuite;

import org.rubypeople.rdt.core.tests.model.BufferTests;
import org.rubypeople.rdt.internal.core.TS_InternalCore;
import org.rubypeople.rdt.internal.core.builder.TS_InternalCoreBuilder;
import org.rubypeople.rdt.internal.core.parser.TS_InternalCoreParser;
import org.rubypeople.rdt.internal.core.util.TS_Util;
import org.rubypeople.rdt.internal.formatter.TS_InternalFormatter;
import org.rubypeople.rdt.internal.ti.TS_TypeInference;

public class TS_Core {

	public static Test suite() {
		TestSuite suite = new TestSuite("Core");
		suite.addTest(TS_InternalCoreBuilder.suite());
		suite.addTest(TS_InternalCoreParser.suite());
		suite.addTest(TS_InternalCore.suite());
		suite.addTest(TS_InternalFormatter.suite());
		suite.addTest(TS_Util.suite());
		suite.addTest(TS_TypeInference.suite());
		suite.addTestSuite(BufferTests.class);
		return suite;
	}
}
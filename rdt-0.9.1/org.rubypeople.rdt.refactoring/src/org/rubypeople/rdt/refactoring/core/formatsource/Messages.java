package org.rubypeople.rdt.refactoring.core.formatsource;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.rubypeople.rdt.refactoring.core.formatsource.messages"; //$NON-NLS-1$

	public static String FormatSourceConditionChecker_NothingToDo;

	public static String FormatSourceRefactoring_Name;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}

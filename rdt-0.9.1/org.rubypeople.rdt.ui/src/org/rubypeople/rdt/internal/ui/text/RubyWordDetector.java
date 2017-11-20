package org.rubypeople.rdt.internal.ui.text;

import org.eclipse.jface.text.rules.IWordDetector;

public class RubyWordDetector implements IWordDetector {

	public RubyWordDetector() {
		super();
	}

	public boolean isWordStart(char c) {
		return Character.isLetter(c) || '?' == c || '_' == c;
	}

	public boolean isWordPart(char c) {
		return Character.isLetter(c) || '?' == c || '_' == c;
	}
}

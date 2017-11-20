/**
 * Copyright (c) 2007 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl -v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.rubypeople.rdt.refactoring.core.extractconstant;

import org.jruby.ast.Node;
import org.rubypeople.rdt.refactoring.editprovider.ReplaceEditProvider;

public class ExtractedConstantCall extends ReplaceEditProvider {

	private ExtractConstantConfig config;

	public ExtractedConstantCall(ExtractConstantConfig config) {
		super(false);
		this.config = config;
	}

	protected int getOffsetLength() {
		return getEndOffset() - getStartOffset();
	}

	private int getStartOffset() {
		return config.getSelectedNodes().getPosition().getStartOffset();
	}

	private int getEndOffset() {
		return config.getSelectedNodes().getPosition().getEndOffset();
	}

	protected Node getEditNode(int offset, String document) {
		return config.getConstantCallNode();
	}

	protected int getOffset(String document) {
		return getStartOffset();
	}
}

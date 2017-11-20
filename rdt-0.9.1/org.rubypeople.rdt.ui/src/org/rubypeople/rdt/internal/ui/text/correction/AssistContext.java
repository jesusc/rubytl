/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.text.correction;

import org.jruby.ast.Node;
import org.jruby.ast.RootNode;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.ti.util.OffsetNodeLocator;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.text.ruby.IInvocationContext;

/**
  */
public class AssistContext implements IInvocationContext {

	private IRubyScript fRubyScript;
	private int fOffset;
	private int fLength;

	private RootNode fASTRoot;

	/*
	 * Constructor for CorrectionContext.
	 */
	public AssistContext(IRubyScript cu, int offset, int length) {
		fRubyScript= cu;
		fOffset= offset;
		fLength= length;

		fASTRoot= null;
	}

	/**
	 * Returns the compilation unit.
	 * @return Returns a IRubyScript
	 */
	public IRubyScript getRubyScript() {
		return fRubyScript;
	}

	/**
	 * Returns the length.
	 * @return int
	 */
	public int getSelectionLength() {
		return fLength;
	}

	/**
	 * Returns the offset.
	 * @return int
	 */
	public int getSelectionOffset() {
		return fOffset;
	}

	public RootNode getASTRoot() {
		if (fASTRoot == null) {
			try {
				RubyParser parser = new RubyParser();
				fASTRoot= (RootNode) parser.parse(fRubyScript.getSource());
			} catch (RubyModelException e) {
				RubyPlugin.log(e);
			}
		}
		return fASTRoot;
	}


	/**
	 * @param root The ASTRoot to set.
	 */
	public void setASTRoot(RootNode root) {
		fASTRoot= root;
	}

	/*(non-Javadoc)
	 * @see org.eclipse.jdt.ui.text.java.IInvocationContext#getCoveringNode()
	 */
	public Node getCoveringNode() {
		return OffsetNodeLocator.Instance().getNodeAtOffset(getASTRoot(), fOffset);		
	}

	/*(non-Javadoc)
	 * @see org.eclipse.jdt.ui.text.java.IInvocationContext#getCoveredNode()
	 */
	public Node getCoveredNode() {
		// FIXME Figure out what the difference is between these two!
		return getCoveringNode();
	}

}

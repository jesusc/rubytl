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
package org.rubypeople.rdt.internal.ui.text.ruby;


import java.io.IOException;
import java.io.Reader;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.util.RDocUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

public class ProposalInfo {

	private boolean fRubydocResolved= false;
	private String fRubydoc= null;

	protected IRubyElement fElement;

	public ProposalInfo(IMember member) {
		fElement= member;
	}
	
	protected ProposalInfo() {
		fElement= null;
	}

	public IRubyElement getRubyElement() throws RubyModelException {
		return fElement;
	}

	/**
	 * Gets the text for this proposal info formatted as HTML, or
	 * <code>null</code> if no text is available.
	 *
	 * @param monitor a progress monitor
	 * @return the additional info text
	 */
	public final String getInfo(IProgressMonitor monitor) {
		if (!fRubydocResolved) {
			fRubydocResolved= true;
			fRubydoc= computeInfo(monitor);
		}
		return fRubydoc;
	}

	/**
	 * Gets the text for this proposal info formatted as HTML, or
	 * <code>null</code> if no text is available.
	 *
	 * @param monitor a progress monitor
	 * @return the additional info text
	 */
	private String computeInfo(IProgressMonitor monitor) {
		try {
			final IRubyElement javaElement= getRubyElement();
			if (javaElement instanceof IMember) {
				IMember member= (IMember) javaElement;
				return extractRubydoc(member, monitor);
			}
		} catch (RubyModelException e) {
			RubyPlugin.log(e);
		} catch (IOException e) {
			RubyPlugin.log(e);
		}
		return null;
	}

	/**
	 * Extracts the javadoc for the given <code>IMember</code> and returns it
	 * as HTML.
	 *
	 * @param member the member to get the documentation for
	 * @param monitor a progress monitor
	 * @return the javadoc for <code>member</code> or <code>null</code> if
	 *         it is not available
	 * @throws RubyModelException if accessing the javadoc fails
	 * @throws IOException if reading the javadoc fails
	 */
	private String extractRubydoc(IMember member, IProgressMonitor monitor) throws RubyModelException, IOException {
		if (member != null && member.getRubyScript() != null) {
//			Reader reader=  getHTMLContentReader(member, monitor);
//			if (reader != null)
//				return getString(reader);
			return RDocUtil.getHTMLDocumentation(member);
		}
		return null;
	}

	private Reader getHTMLContentReader(IMember member, IProgressMonitor monitor) throws RubyModelException {
		// TODO Actually get an HTML Content Reader
//	    Reader contentReader= RubydocContentAccess.getContentReader(member, true);
//        if (contentReader != null)
//        	return new RubyDoc2HTMLTextReader(contentReader);
//        
//        if (true && member.getOpenable().getBuffer() == null) { // only if no source available
//        	String s= member.getAttachedRubydoc(monitor);
//        	if (s != null)
//        		return new StringReader(s);
//        }
        return null;
    }
	
	/**
	 * Gets the reader content as a String
	 */
	private static String getString(Reader reader) {
		StringBuffer buf= new StringBuffer();
		char[] buffer= new char[1024];
		int count;
		try {
			while ((count= reader.read(buffer)) != -1)
				buf.append(buffer, 0, count);
		} catch (IOException e) {
			return null;
		}
		return buf.toString();
	}
}

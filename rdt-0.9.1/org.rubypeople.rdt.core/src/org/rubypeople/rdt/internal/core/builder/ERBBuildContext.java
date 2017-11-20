package org.rubypeople.rdt.internal.core.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.rubypeople.rdt.core.compiler.BuildContext;

public class ERBBuildContext extends BuildContext {

	public ERBBuildContext(IFile resource) {
		super(resource);
	}
	
	/**
	 * Returns the contents of the compilation unit.
	 * 
	 * @return the contents of the compilation unit
	 */
	public char[] getContents() {
		char[] contents = super.getContents();
		// Strip to only ruby portions!
		String stringContents = new String(contents);
		List<String> code = new ArrayList<String>();
		String[] pieces = stringContents.split("(<%%)|(%%>)|(<%=)|(<%#)|(<%)|(%>)");
		for (int i = 0; i < pieces.length; i++) {
			if ((i % 2) == 1) {
				code.add(pieces[i]);
			}
		}
		StringBuffer buffer = new StringBuffer();
		int lastIndex = 0;
		for (String string : code) {
			int index = stringContents.indexOf(string);
			String portion = stringContents.substring(lastIndex, index);
			for (int j = 0; j < portion.length(); j++) {
				char chr = portion.charAt(j);
				if (Character.isWhitespace(chr)) {
					buffer.append(chr);
				} else {
					buffer.append(' ');
				}
			}
			buffer.append(string);
			lastIndex = index + string.length();
		}		
		return buffer.toString().toCharArray();
	}

}

package org.rubypeople.rdt.core.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.jruby.Ruby;
import org.jruby.ast.CommentNode;
import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.runtime.builtin.IRubyObject;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public class RDocUtil {
	private static Ruby fgRuby;
	private static String fgRdocScriptPath;

	private RDocUtil() {}
	
	public static String getDocumentation(IRubyElement element) {
		if (element instanceof IMember) {
			return getContents((IMember)element);
		}
		return "";
	}
	
	public static String getHTMLDocumentation(IRubyElement element) {
		return getHTMLDocumentation(getDocumentation(element));
	}
	
	private static String getContents(IMember member) {
		String src = "";
		int elementOffset = -1;
		try {
			src = member.getRubyScript().getSource();
			elementOffset = member.getSourceRange().getOffset();
		} catch (RubyModelException e) {
			return null;
		}
		RubyParser parser = new RubyParser();		
		parser.parse(src); // parse so we can grab the comment nodes
		Collection<CommentNode> comments = parser.getComments();		
		if (member.isType(IRubyElement.TYPE) || member.isType(IRubyElement.METHOD)) {
			return getPrecedingComment(comments, elementOffset, src);
		}
		// for variables try to get the following comment, if it's null grab the leading/preceding comment
		if (member.isType(IRubyElement.CLASS_VAR) || member.isType(IRubyElement.INSTANCE_VAR) || 
				member.isType(IRubyElement.LOCAL_VARIABLE) || member.isType(IRubyElement.CONSTANT)) {
			String comment = getFollowingComment(comments, elementOffset, src);
			if (comment != null) return comment;
			return getPrecedingComment(comments, elementOffset, src);
		}
		return getFollowingComment(comments, elementOffset, src);
	}
	
	/**
	 * Grabs and merges together all comment nodes which immediately preced the elementStart offset.
	 * @param comments
	 * @param elementStart
	 * @param src
	 * @return a combined string of all immediately preceding comments
	 */
	private static String getPrecedingComment(Collection<CommentNode> comments, int elementStart, String src) {
		for (CommentNode comment : comments) {
			ISourcePosition pos = comment.getPosition();
			if (pos.getEndOffset() > elementStart) continue;
			String between = src.substring(pos.getEndOffset(), elementStart);
			if (between.trim().length() > 0)
				continue; // if there's anything but whitespace between (\n\r\t ), move to next comment			
			String preceding = getPrecedingComment(comments, pos.getStartOffset(), src);
			if (preceding == null) {
				preceding = removePrecedingHashes(comment.getContent());
			} else {
				preceding += "\n" + removePrecedingHashes(comment.getContent());
			}
			return preceding;
		}
		return null;
	}
	
	/**
	 * Grabs the comment from any comment node that follows this element (has to be on the same line)
	 * @param comments
	 * @param elementStart
	 * @param src
	 * @return
	 */
	private static String getFollowingComment(Collection<CommentNode> comments, int elementStart, String src) {
		for (CommentNode comment : comments) {
			ISourcePosition pos = comment.getPosition();
			if (pos.getStartOffset() < elementStart) continue;
			String between = src.substring(elementStart, pos.getStartOffset());
			if (between.contains("\n"))	continue;	// if there's a newline between the positions - it's not on same line
			String com = comment.getContent();
			if (com != null && com.length() > 0)
				return removePrecedingHashes(com);
		}
		return null;
	}
	
	/**
	 * Trims the string and drops the beginning hash mark (#)
	 * @param comment
	 * @return
	 */
	private static String removePrecedingHashes(String comment) {
		return comment.trim().substring(1);
	}

	public static String getHTMLDocumentation(String docs) {
		if (docs == null) return null;
		docs = removeUnecessaryIndent(docs);
		String script = "require 'rdoc/markup/simple_markup'\n" +
						"require 'rdoc/markup/simple_markup/to_html'\n" +
						"p = SM::SimpleMarkup.new\n" +
						"h = SM::ToHtml.new\n" +
						"input_string =<<EOF\n" + docs + "\nEOF\n" +
						"p.convert(input_string, h)\n";
		Ruby ruby = getJRubyInstance();		
		try {			
			ruby.setCurrentDirectory(getRDocScriptPath());
			IRubyObject object = ruby.evalScriptlet(script);
			docs = object.toString();
		} catch (Exception e) {
			// ignore
		}
		return docs;		
	}

	private static String removeUnecessaryIndent(String docs) {
		int count = 0;
		String[] lines = docs.split("\n");
		if (lines == null || lines.length == 0) return docs;
		String tmp = lines[0];
		if (tmp != null && tmp.length() > 0) {
			while(tmp.charAt(0) == ' ') {
				count++;
				if (tmp.length() == 1) break;
				tmp = tmp.substring(1);			
			}
		}
		StringBuffer modified = new StringBuffer();
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if (line.length() > count) {			
				if (line.substring(0, count).trim().length() == 0) {
					line = line.substring(count);
				}
			}
			modified.append(line);
			modified.append("\n");
		}
		modified.deleteCharAt(modified.length() - 1); // remove last newline
		return modified.toString();
	}

	private static Ruby getJRubyInstance() {
		if (fgRuby == null)
			fgRuby = Ruby.getDefaultInstance();
		return fgRuby;
	}

	private static String getRDocScriptPath() throws IOException {
		if (fgRdocScriptPath == null) {
			URL installURL = new URL(RubyCore.getPlugin().getBundle().getEntry("/"),new Path("ruby").toString()); //$NON-NLS-1$
			URL localURL = FileLocator.toFileURL(installURL);
			File file = new File(localURL.getFile());
			fgRdocScriptPath = file.getAbsolutePath();
		}			
		return fgRdocScriptPath;
	}

	public static IRegion getDocumentationRegion(IRubyElement element) {
		IMember member = (IMember) element;
		String src = "";
		int elementOffset = -1;
		try {
			src = member.getRubyScript().getSource();
			elementOffset = member.getSourceRange().getOffset();
		} catch (RubyModelException e) {
			return null;
		}
		RubyParser parser = new RubyParser();		
		parser.parse(src); // parse so we can grab the comment nodes
		Collection<CommentNode> comments = parser.getComments();		
		if (member.isType(IRubyElement.TYPE) || member.isType(IRubyElement.METHOD)) {
			return getPrecedingCommentRegion(comments, elementOffset, src);
		}
		return null;
	}
	
	private static IRegion getPrecedingCommentRegion(Collection<CommentNode> comments, int elementStart, String src) {
		for (CommentNode comment : comments) {
			ISourcePosition pos = comment.getPosition();
			if (pos.getEndOffset() > elementStart) continue;
			String between = src.substring(pos.getEndOffset(), elementStart);
			if (between.trim().length() > 0)
				continue; // if there's anything but whitespace between (\n\r\t ), move to next comment			
			IRegion preceding = getPrecedingCommentRegion(comments, pos.getStartOffset(), src);
			if (preceding == null) {
				preceding = new Region(pos.getStartOffset(), pos.getEndOffset() - pos.getStartOffset());
			} else {
				preceding = new Region(preceding.getOffset(), pos.getEndOffset() - preceding.getOffset());
			}
			return preceding;
		}
		return null;
	}
}

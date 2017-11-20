/*
 * Author: Chris
 *
 * Copyright (c) 2005 RubyPeople.
 *
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
 */

package org.rubypeople.rdt.internal.core.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.jruby.ast.CommentNode;
import org.jruby.ast.Node;
import org.jruby.common.NullWarnings;
import org.jruby.lexer.yacc.LexerSource;
import org.jruby.lexer.yacc.SyntaxException;
import org.jruby.parser.DefaultRubyParser;
import org.jruby.parser.ParserConfiguration;
import org.jruby.parser.RubyParserPool;
import org.jruby.parser.RubyParserResult;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.core.builder.IoUtils;

/**
 * @author cawilliams
 */
public class RubyParser {

    private final RubyParserPool pool;
	private IRdtWarnings warnings;
	private static boolean isDebug;
	private RubyParserResult result;

	public RubyParser() {
		this(new NullRdtWarnings());
	}

	public RubyParser(IRdtWarnings warnings) {
        this.warnings = warnings;
        this.pool = RubyParserPool.getInstance();
	}
    
	public Node parse(IFile file, Reader content) {
		DefaultRubyParser parser = null;
        result = null;
        try {
            warnings.setFile(file);
        	parser = getDefaultRubyParser();
        	parser.setWarnings(warnings);
        	String fileName = "";
        	if (file != null) fileName = file.getName();
        	ParserConfiguration config = new ParserConfiguration(0, true, false);
        	LexerSource lexerSource = LexerSource.getSource(fileName, content, null, config);
        	result = parser.parse(config, lexerSource);
        } catch (SyntaxException e) {
        	throw e;
        } finally {
        	returnBorrowedParser(parser);
        }
        return result.getAST();
	}
	
	public List<CommentNode> getComments() {
		return result.getCommentNodes();
	}

	protected void returnBorrowedParser(DefaultRubyParser parser) {
		pool.returnParser(parser);
	}

    protected DefaultRubyParser getDefaultRubyParser() {
        return pool.borrowParser();
    }

	public static void setDebugging(boolean b) {
		isDebug = b;
	}

	public static boolean isDebugging() {
		return isDebug;
	}

    static class NullRdtWarnings extends NullWarnings implements IRdtWarnings {
        public void setFile(IFile file) {
        }
    }

    public Node parse(IFile file) throws CoreException {
        InputStream contents = null;
        try {
            contents = file.getContents();
            return parse(file, new InputStreamReader(contents, file.getCharset()));
        } catch (UnsupportedEncodingException e) {
			RubyCore.log(e);
			return parse(file, new InputStreamReader(contents));
		} finally {
            IoUtils.closeQuietly(contents);
        }
    }

	public Node parse(String source) {
		return parse(null, new StringReader(source));
	}


}
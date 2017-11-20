/*
 * Author: David Corbin
 *
 * Copyright (c) 2005 RubyPeople.
 *
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
 */
package org.rubypeople.rdt.internal.core.builder;

import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.jruby.ast.Node;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public class ShamRubyParser extends RubyParser {

    private Map parsingsWithContent = new HashMap();
    private Set parsingsWithoutContent = new HashSet();
    private SyntaxException syntaxException;
    private Map parseResults = new HashMap();

    public Node parse(IFile file, Reader reader) {
        parsingsWithContent.put(file, IoUtils.readAllQuietly(reader));
        if (syntaxException != null)
            throw syntaxException;
        return (Node) parseResults.get(file);
    }

    public void assertParsed(IFile expectedFile, String expectedContent) {
        Assert.assertTrue("File " + expectedFile + " should have been parsed", 
                parsingsWithContent.containsKey(expectedFile));
        Assert.assertEquals("Content", expectedContent, 
                parsingsWithContent.get(expectedFile));
    }

    public void setExceptionToThrow(SyntaxException syntaxException) {
        this.syntaxException = syntaxException;
    }

    public Node parse(IFile file) {
        parsingsWithoutContent.add((file));
        return (Node) parseResults.get(file);
    }
    
    public void assertParsed(IFile expectedFile) {
        Assert.assertTrue("expected to have parsed "+ expectedFile, 
                parsingsWithoutContent.contains(expectedFile));
    }

    public void addParseResult(IFile file, Node node) {
        parseResults.put(file,node);
    }
}

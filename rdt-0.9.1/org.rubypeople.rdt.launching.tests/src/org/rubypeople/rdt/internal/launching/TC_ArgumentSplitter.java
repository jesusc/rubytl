package org.rubypeople.rdt.internal.launching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.rubypeople.rdt.internal.launching.ArgumentSplitter;

public class TC_ArgumentSplitter extends TestCase {

    public void testNoArgs() {
        verifySplit(new String[]{}, "");
        verifySplit(new String[]{}, " ");
        verifySplit(new String[]{}, "  ");
    }
    
    public void testSimpleSplit() {
        verifySplit(new String[]{"foo"}, "foo");
        verifySplit(new String[]{"foo"}, " foo ");
        verifySplit(new String[]{"foo", "bar"}, "foo bar");
        verifySplit(new String[]{"foo", "bar"}, " foo  bar ");
    }
    
    public void testSimpleQuotes() {
        verifySplit(new String[]{"foo"}, "'foo'");
        verifySplit(new String[]{"foo bar"}, "'foo bar'");
        verifySplit(new String[]{"foo bar","red blue"}, "'foo bar' 'red blue'");
        verifySplit(new String[]{"foo"}, "\"foo\"");
    }
    
    public void testEmptyQuotes() {
        verifySplit(new String[]{""}, "''");
        verifySplit(new String[]{"F", "", "B", "X"}, "F '' B \"X");
        verifySplit(new String[]{"F", "", "B"}, "F '' B \"");
    }
    
    public void testMixedQuotes() {
        verifySplit(new String[]{"f\"oo"}, "'f\"oo'");
        verifySplit(new String[]{"f'oo"}, "\"f'oo\"");
        
    }

    private void verifySplit(String[] expected, String input) {
        List<String> args = ArgumentSplitter.split(input);
        assertEquals("For input: "+input, new ArrayList<String>(Arrays.asList(expected)), args);
    }
}

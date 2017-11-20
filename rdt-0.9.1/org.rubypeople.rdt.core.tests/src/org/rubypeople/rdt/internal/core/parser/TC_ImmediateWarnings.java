package org.rubypeople.rdt.internal.core.parser;

import junit.framework.TestCase;

import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.rdt.internal.core.builder.ShamMarkerManager;

public class TC_ImmediateWarnings extends TestCase {

    private ImmediateWarnings warnings;
    private ShamMarkerManager markerManager;
    private ShamFile file;
    
    public void setUp() {
        markerManager = new ShamMarkerManager();
        warnings = new ImmediateWarnings(markerManager);
        file = new ShamFile("test.rb");
        warnings.setFile(file);
    }

    public void testTextOnlyWarn() throws Exception {
        warnings.warn("testMessage");
        markerManager.assertWarningAdded(file, "testMessage");
    }

    public void testTextOnlyWarning() throws Exception {
        warnings.warn("testMessage");
        markerManager.assertWarningAdded(file, "testMessage");
    }
    
    public void testWarn() {
        ISourcePosition position = new RdtPosition(1, 2, 3);
        warnings.warn(position, "another Message");
        
        markerManager.assertWarningAdded(file, "another Message", 1, 2, 3);
    }
}


package org.rubypeople.rdt.cruisecontrol.plugin.tests;

import org.rubypeople.rdt.cruisecontrol.plugin.EclipsePluginLabelProvider;

import junit.framework.TestCase;

public class EclipseLabelIncrementerTest extends TestCase {

    private EclipsePluginLabelProvider incrementer;

    public EclipseLabelIncrementerTest(String name) {
        super(name);
    }

    public void setUp() {
        incrementer = new EclipsePluginLabelProvider();
    }

    public void testIsValidLabel() {
        assertEquals(incrementer.isValidLabel("abc"), true);

    }

    public void testIncrementLabel() {
    	incrementer.setPrefix("1.0.0") ;
    	String label = incrementer.incrementLabel("doesntMatter", null) ;
    	assertTrue(label.startsWith("1.0.0.")) ;
    	String numberPart = label.substring(6) ;
    	assertTrue(numberPart.endsWith("NGT")) ;
    	numberPart = numberPart.substring(0, numberPart.length()-3) ;
        assertTrue(!numberPart.startsWith("0") );
        int number = Integer.parseInt(numberPart) ;
        // may be in range 501010000 -> Integer.Max ( = 2147483647 , sorry, no more builds in the year 2022 :-) 
        					 
        assertTrue(number > 501010000 );

    }

    
    public void testGetDefaultLabel() {
        assertEquals("0.1.0", incrementer.getDefaultLabel());
        assertTrue(incrementer.isValidLabel(incrementer.getDefaultLabel()));
    }
}

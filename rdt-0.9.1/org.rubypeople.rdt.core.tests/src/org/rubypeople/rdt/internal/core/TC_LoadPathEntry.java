package org.rubypeople.rdt.internal.core;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;
import org.rubypeople.eclipse.shams.resources.ShamProject;

public class TC_LoadPathEntry extends TestCase {

	public TC_LoadPathEntry(String name) {
		super(name);
	}

	public void testToXml() {
		ShamProject project = new ShamProject(new Path("myLocation"), "MyProject");
		LoadpathEntry entry = new LoadpathEntry(project);
		
		String expected = "<pathentry type=\"project\" path=\"myLocation\"/>";
		assertEquals(expected, entry.toXML());
	}
}

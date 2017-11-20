package gts.rubytl.launching.core;

import gts.rubytl.launching.core.configuration.data.RakefileConfigurationData;
import gts.rubytl.launching.core.launcher.RakefileLauncher;
import junit.framework.TestCase;

public class TestRakefileLauncher extends TestCase {
	RakefileConfigurationData data;
	final String RUBYTL_TEST_PATH = "/home/jesus/usr/dltk/workspace/rubytl";
	//TODO: Create a mock for RubyTL
	
	protected void setUp() throws Exception {
		data = new RakefileConfigurationData("ruby", RUBYTL_TEST_PATH, projectTestPath(), rakefileTestPath());
	}
	
	public void testRakefile() {
		RakefileLauncher launcher = new RakefileLauncher(data);
		launcher.execute("test");
	}
	
	private String projectTestPath() {
		return "./test/gts/rubytl/launching/core/data";
	}
	
	private String rakefileTestPath() {
		return "test.rakefile";
	}
	
}

package gts.rubytl.launching.core;

import gts.rubytl.launching.core.configuration.Binding;
import gts.rubytl.launching.core.configuration.data.ModelConfigurationData;
import gts.rubytl.launching.core.configuration.data.TaskM2MConfigurationData;
import gts.rubytl.launching.core.configuration.data.TaskM2TConfigurationData;
import gts.rubytl.launching.core.launcher.RtlLauncher;
import junit.framework.TestCase;

public class TestRtlLauncher extends TestCase {
	TaskM2MConfigurationData m2mData;
	TaskM2TConfigurationData m2tData;
	ModelConfigurationData modelData;
	
	final String RUBYTL_TEST_PATH = "/home/jesus/usr/dltk/workspace/rubytl";
	//final String RUBYTL_TEST_PATH = "/tmp/espacios con/rtl con espacios/";
	//TODO: Create a mock for RubyTL
	
	protected void setUp() throws Exception {
		Binding sourceBinding = new Binding();
		sourceBinding.addModel("source.xmi");
		sourceBinding.addMetamodel("ClassM", "ClassM.ecore");
		Binding targetBinding = new Binding();
		//targetBinding.addModel("/tmp/target.xmi");
		targetBinding.addModel("/tmp/target.xmi");
		targetBinding.addMetamodel("TableM", "TableM.ecore");
				
		m2mData = new TaskM2MConfigurationData("ruby", RUBYTL_TEST_PATH, projectTestPath(), "class2table.rb");
		m2mData.addSourceBinding(sourceBinding);
		m2mData.addTargetBinding(targetBinding);
				
		m2tData = new TaskM2TConfigurationData("ruby", RUBYTL_TEST_PATH, projectTestPath(), "class.2code", "/tmp/generated");
		m2tData.addSourceBinding(sourceBinding);
		
		modelData = new ModelConfigurationData();
		modelData.addURIMapping("http://gts.inf.um.es/examples/class", "ClassM.ecore");
	}
	
	public void testLaunchM2MTask() {
		RtlLauncher launcher = new RtlLauncher(m2mData, modelData);
		launcher.execute("test_m2m");
	}

	public void testLaunchM2tTask() {
		RtlLauncher launcher = new RtlLauncher(m2tData, modelData);
		launcher.execute("test_m2t");
	}

	private String projectTestPath() {
		return "./test/gts/rubytl/launching/core/data/class2table";
	}
	
}

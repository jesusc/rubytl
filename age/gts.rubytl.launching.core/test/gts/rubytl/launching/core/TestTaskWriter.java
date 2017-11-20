package gts.rubytl.launching.core;

import gts.rubytl.launching.core.configuration.Binding;
import gts.rubytl.launching.core.configuration.TaskM2MWriter;
import gts.rubytl.launching.core.configuration.data.TaskM2MConfigurationData;
import junit.framework.TestCase;

public class TestTaskWriter extends TestCase {

	TaskM2MConfigurationData m2mConf;
	
	protected void setUp() throws Exception {
		Binding sourceBinding = new Binding();
		sourceBinding.addModel("testSourceModel.xmi");
		sourceBinding.addMetamodel("Source", "SourceTest.ecore");
		Binding targetBinding = new Binding();
		targetBinding.addModel("testTargetModel.xmi");
		targetBinding.addMetamodel("Target", "TargetTest.ecore");
		
		m2mConf = new TaskM2MConfigurationData("ruby", "rubytlpath", ".", "mydef.rb");
		m2mConf.addSourceBinding(sourceBinding);
		m2mConf.addTargetBinding(targetBinding);
	}
	
	public void testTaskM2MWriter() {	
		TaskM2MWriter writer = new TaskM2MWriter();
		String result = writer.writeConfiguration(m2mConf, "testTask");
		System.out.println(result);
	}
	
}

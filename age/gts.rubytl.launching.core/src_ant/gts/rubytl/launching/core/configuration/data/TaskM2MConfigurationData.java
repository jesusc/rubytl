package gts.rubytl.launching.core.configuration.data;

import gts.rubytl.launching.core.configuration.ITaskM2MConfiguration;
import gts.rubytl.launching.core.configuration.ITaskWriter;
import gts.rubytl.launching.core.configuration.TaskM2MWriter;

public class TaskM2MConfigurationData extends AbstractTaskConfigurationData implements ITaskM2MConfiguration {
	
	private String transformationDefinition;
	private String trace;

	public TaskM2MConfigurationData(String rubyPath, String rubytlPath,
			String projectPath, String transformationDefinition) {
		super(rubyPath, rubytlPath, projectPath);
		this.transformationDefinition = transformationDefinition;
	}

	public String getTransformationDefinition() {
		return transformationDefinition;
	}

	public String getTransformationTraceOutputPath() {
		return trace;
	}

	public void setTransformationTraceOutputPath(String traceFile) {
		this.trace = traceFile;
	}

	public ITaskWriter<ITaskM2MConfiguration> getTaskWriter() {
		return new TaskM2MWriter();
	}



}

package gts.rubytl.launching.core.configuration.data;

import gts.rubytl.launching.core.configuration.ITaskM2TConfiguration;
import gts.rubytl.launching.core.configuration.ITaskWriter;
import gts.rubytl.launching.core.configuration.TaskM2TWriter;

public class TaskM2TConfigurationData extends AbstractTaskConfigurationData implements ITaskM2TConfiguration {
	
	private String transformationDefinition;
	private String codebase;

	public TaskM2TConfigurationData(String rubyPath, String rubytlPath,
			String projectPath, String transformationDefinition, String codebase) {
		super(rubyPath, rubytlPath, projectPath);
		this.transformationDefinition = transformationDefinition;
		this.codebase = codebase;
	}

	public String getTransformationDefinition() {
		return transformationDefinition;
	}


	public ITaskWriter<ITaskM2TConfiguration> getTaskWriter() {
		return new TaskM2TWriter();
	}

	public String getGenerationPath() {
		return codebase;
	}

}

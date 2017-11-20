package gts.rubytl.launching.core.configuration;

public class TaskM2TWriter extends AbstractTaskWriter<ITaskM2TConfiguration> implements ITaskWriter<ITaskM2TConfiguration> {

	public String getRakeTaskName(ITaskM2TConfiguration configuration) {
		return "model_to_code";
	}

	protected String completeTaskWriting(ITaskM2TConfiguration configuration) {
		String result = "t.generate '" + configuration.getTransformationDefinition() + "'";
		result += "\n" + "t.codebase = '" + configuration.getGenerationPath() + "'";
		return result;
	}


}

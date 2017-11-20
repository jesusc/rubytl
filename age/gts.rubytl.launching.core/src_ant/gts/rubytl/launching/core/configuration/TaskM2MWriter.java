package gts.rubytl.launching.core.configuration;

public class TaskM2MWriter extends AbstractTaskWriter<ITaskM2MConfiguration> implements ITaskWriter<ITaskM2MConfiguration> {

	public String getRakeTaskName(ITaskM2MConfiguration configuration) {
		return "model_to_model";
	}

	protected String completeTaskWriting(ITaskM2MConfiguration configuration) {
		String result = "t.transformation '" + configuration.getTransformationDefinition() + "'";
		if ( configuration.getTransformationTraceOutputPath() != null ) {
			result += "\n" + "t.trace '" + configuration.getTransformationTraceOutputPath() + "'";
		}
		return result;
	}


}

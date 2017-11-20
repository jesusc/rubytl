package gts.rubytl.launching.core.configuration;

public interface ITaskM2MConfiguration extends ITaskConfiguration {
	/**
	 * The transformation definition file.
	 * 
	 * @return file transformation definition file.
	 */
	public String getTransformationDefinition();
	
		
	/**
	 * The file where the transformation trace will be stored.
	 * 
	 * @return file where the transformation trace will be stored.
	 */
	public String getTransformationTraceOutputPath();
	
}

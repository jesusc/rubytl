package gts.rubytl.launching.core.configuration;

public interface ITaskM2TConfiguration extends ITaskConfiguration {
	/**
	 * The transformation definition file.
	 * 
	 * @return file transformation definition file.
	 */
	public String getTransformationDefinition();
	
		
	/**
	 * The directory where the generated will be placed.
	 * 
	 * @return directory where the generated will be placed.
	 */
	public String getGenerationPath();
	
}

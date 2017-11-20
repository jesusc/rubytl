package gts.rubytl.launching.core.configuration;


public interface IRakefileConfiguration extends IRubyConfiguration {
	
	/**
	 * The path to the rakefile (.rake, .rakefile, etc.) to be launched.
	 * 
	 * @return path to the rakefile.
	 */
	String getRakefilePath();
	
	/**
	 * The name of the default task to be launched, or null if
	 * the Rake's default task name must be used. 
	 * 
	 * @return name of the default task to 
	 */
	String getDefaultTask();
}

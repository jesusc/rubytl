package gts.rubytl.launching.core.configuration;


import java.util.Collection;
import java.util.Map;

public interface ITaskConfiguration extends IRubyConfiguration {
	
	/**
	 * Returns the list of source bindings between models and metamodels.
	 * 
	 * @return the list of source bindings between models and metamodels.
	 */
	public Collection<Binding> getSourceBindings();
	
	/**
	 * Returns the list of target bindings between models and metamodels.
	 * The restriction is that each binding cannot specify more than one
	 * model (i.e. only one model will be eventually serialized).
	 * 
	 * @return the list of target bindings between models and metamodels.
	 */	
	public Collection<Binding> getTargetBindings();

	/**
	 * Return the task writer in charge of writing this configuration.
	 * @return the task writer.
	 */
	//public ITaskWriter<ITaskConfiguration> getTaskWriter(); 
	public ITaskWriter getTaskWriter();
	
	/**
	 * Returns the a map containing the of parameters of a task.
	 * 
	 * @return the parameters of a task
	 */	
	public Map<String, String> getParameters();	
}

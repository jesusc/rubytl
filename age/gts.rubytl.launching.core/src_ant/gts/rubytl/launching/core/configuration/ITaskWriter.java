package gts.rubytl.launching.core.configuration;

public interface ITaskWriter<T extends ITaskConfiguration> {

	/**
	 * Returns a string representing the Rake task that is able to 
	 * launch the transformation. For instance,
	 * 
	 * <code>
	 *   model_to_model_transformation :mytask do |t|
	 *     t.sources :model => 'mymodel.xmi',
	 *               :namespaces => { 
	 *                   'Pkg' => 'mymetamodel.ecore'
	 *               }
	 *     ...
	 *   end
	 * </code>
	 * 
	 * @return the Rake task.
	 */
	String writeConfiguration(T configuration, String taskName);
}

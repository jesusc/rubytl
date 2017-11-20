package gts.rubytl.launching.core.configuration;

import gts.rubytl.launching.core.Util;
import gts.rubytl.launching.core.configuration.Binding.NamespacePair;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class AbstractTaskWriter<T extends ITaskConfiguration> implements ITaskWriter<T> {
	public String writeConfiguration(T conf, String taskName) {
		return getRakeTaskName(conf) + " :" + taskName + " do |t|" + "\n" +
					computeBindings(conf.getSourceBindings(), "sources") + "\n" +
					computeBindings(conf.getTargetBindings(), "targets") + "\n" +
					writeParameters(conf.getParameters()) + "\n" +
					completeTaskWriting(conf) + "\n" +
			   "end";

	}
	
	private String writeParameters(Map<String, String> parameters) {	
		String result = "";
		Set<String> keys = parameters.keySet();
		for (String key : keys) {
			String keyAsExpected = "'" + key + "'";
			if ( key.startsWith(":") ) keyAsExpected = key;
			
			result += "t.parameters " + keyAsExpected + " => " +  "'" + parameters.get(key) +  "'"; 
		}
		return result;
	}

	protected abstract String getRakeTaskName(T configuration);
	protected abstract String completeTaskWriting(T configuration);
	
	private String computeBindings(Collection<Binding> bindings, String kind) {
		String result = "";
		for (Binding binding : bindings) {			
			String models     = Util.join(
					Util.map(binding.getModels(), new Util.IMap<String, String>() {
						public String map(String t) {						
							return "'" + t + "'";
						}						
					}),	", ");
			String namespaces = Util.join(
					Util.map(binding.getMetamodels(),
					new Util.IMap<Binding.NamespacePair, String>() {
						public String map(NamespacePair t) {
							return "'" + t.getNamespace() + "' => '" + t.getMetamodel() + "'";
						} 
					}), ",\n");
			
			result += "t." + kind + " :model => [" + models + "]," + "\n" +
			                        " :namespaces => {" + namespaces + "}" + "\n";
			
		}		
		return result;
	}

}

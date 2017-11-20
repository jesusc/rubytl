package gts.rubytl.launching.core.configuration.data;

import java.util.Collection;
import java.util.LinkedList;

import gts.rubytl.launching.core.configuration.IModelConfiguration;
import gts.rubytl.launching.core.configuration.ModelMapping;

public class ModelConfigurationData implements IModelConfiguration {

	private LinkedList<ModelMapping> mappings;

	public ModelConfigurationData() {
		this.mappings = new LinkedList<ModelMapping>();
	}
	
	public void addURIMapping(String uri, String filename) {
		this.mappings.add(new ModelMapping(uri, filename));
	}
	
	public Collection<ModelMapping> getMappings() {
		return new LinkedList<ModelMapping>(this.mappings);
	}
	
	
	
}

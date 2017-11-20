package gts.rubytl.launching.core.configuration.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import gts.rubytl.launching.core.configuration.Binding;
import gts.rubytl.launching.core.configuration.ITaskConfiguration;

public abstract class AbstractTaskConfigurationData implements ITaskConfiguration {
	private String rubyPath;
	private String rubytlPath;
	private String projectPath;
	private LinkedList<Binding> sourceBindings;
	private LinkedList<Binding> targetBindings;
	private HashMap<String, String> parameters = new HashMap<String, String>(); 

	public AbstractTaskConfigurationData(
			String rubyPath, String rubytlPath, 
			String projectPath) {
		this.rubyPath     = rubyPath;
		this.rubytlPath   = rubytlPath;
		this.projectPath  = projectPath;

		this.sourceBindings = new LinkedList<Binding>();
		this.targetBindings = new LinkedList<Binding>();
	}
	
	public String getProjectPath() { return projectPath; }
	public String getRtlPath()     { return rubytlPath;	 }
	public String getRubyPath()    { return rubyPath;	 }
	
	public void addSourceBinding(Binding b) { this.sourceBindings.add(b); }
	public void addTargetBinding(Binding b) { this.targetBindings.add(b); }
	public void addParameter(String key, String value) {
		parameters.put(key, value);
	}
	
	public Collection<Binding> getSourceBindings() { return new LinkedList<Binding>(sourceBindings); }	
	public Collection<Binding> getTargetBindings() { return new LinkedList<Binding>(targetBindings); }
	public Map<String, String> getParameters() { return new HashMap<String, String>(this.parameters); }
}

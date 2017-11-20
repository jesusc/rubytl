package gts.rubytl.launching.core.ant;

import gts.rubytl.launching.core.configuration.data.ModelConfigurationData;

import java.util.Collection;
import java.util.HashMap;

public class Configuration {
	private String ruby;
	private String rubytl;
	private ModelConfigurationData configurationData;

	private static HashMap<String, Configuration> configurations = new HashMap<String, Configuration>();

	public static Configuration findConfiguration(String name) {
		return configurations.get(name);
	}
	
	public static boolean hasConfiguration(String name) {
		return configurations.containsKey(name);
	}
	
	public static void putConfiguration(String name, Configuration c) {
		configurations.put(name, c);
	}	
	
	public Configuration(String ruby, String rubytl, Collection<TaskElements.ModelMapping> mappings) {
		this.ruby = ruby;
		this.rubytl = rubytl;
		configurationData = new ModelConfigurationData();
		for (TaskElements.ModelMapping modelMapping : mappings) {
			configurationData.addURIMapping(modelMapping.getUri(), modelMapping.getFile());
		}
	}
	
	public void setRuby(String ruby) {
		this.ruby = ruby;
	}
	
	public void setRubytl(String rubytl) {
		this.rubytl = rubytl;
	}
	
	public String getRuby() {
		return ruby;
	}
	
	public String getRubytl() {
		return rubytl;
	}
	
	public ModelConfigurationData getModelConfiguration() {
		return configurationData;
	}
	
}

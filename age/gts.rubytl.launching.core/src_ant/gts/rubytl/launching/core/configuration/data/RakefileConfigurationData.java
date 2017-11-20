package gts.rubytl.launching.core.configuration.data;

import gts.rubytl.launching.core.configuration.IRakefileConfiguration;


public class RakefileConfigurationData implements IRakefileConfiguration {

	private String rubyPath;
	private String rubytlPath;
	private String projectPath;
	private String rakefilePath;

	public RakefileConfigurationData(
			String rubyPath, String rubytlPath, 
			String projectPath, String rakefilePath) {
		this.rubyPath     = rubyPath;
		this.rubytlPath   = rubytlPath;
		this.projectPath  = projectPath;
		this.rakefilePath = rakefilePath;
	}
	
	public String getDefaultTask() {
		return null;
	}

	public String getRakefilePath() {
		return rakefilePath;
	}

	public String getProjectPath() {
		return projectPath;
	}

	public String getRtlPath() {
		return rubytlPath;
	}

	public String getRubyPath() {
		return rubyPath;
	}

}

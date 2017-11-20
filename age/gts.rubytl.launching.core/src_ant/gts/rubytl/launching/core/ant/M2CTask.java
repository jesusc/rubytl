package gts.rubytl.launching.core.ant;

import gts.rubytl.launching.core.configuration.data.TaskM2TConfigurationData;
import gts.rubytl.launching.core.launcher.RtlLauncher;

import org.apache.tools.ant.BuildException;

/**
 * Definition of an Ant task for launching m2c tasks.
 * 
 * <code>
 * </code>
 * 
 * @author jesus
 */
public class M2CTask extends AbstractExtendedRubytlTask {
	protected TaskElements.PathElement transformationDefinition;
	protected TaskElements.PathElement codebase;

	public TaskElements.PathElement createTransformation() {
		this.transformationDefinition= new TaskElements.PathElement();
		return this.transformationDefinition;
	}		

	public TaskElements.PathElement createCodebase() {
		this.codebase = new TaskElements.PathElement();
		return this.codebase;
	}		

	protected void executeRubyTL(String projectPath, Configuration configuration) {
		try {			
			TaskM2TConfigurationData taskData = new TaskM2TConfigurationData(configuration.getRuby(), configuration.getRubytl(), projectPath, this.transformationDefinition.getPath(), this.codebase.getPath());			
			configureModels(taskData);
			
			RtlLauncher launcher = new RtlLauncher(taskData, configuration.getModelConfiguration()); // TODO: Allow model mappings
			launcher.execute("ant_task");
		} catch ( Exception e ) {
			e.printStackTrace();
			throw new BuildException(e);
		}		
	}

	protected void checkTaskValues() throws BuildException { 
		super.checkTaskValues();
		if ( this.codebase == null ) {
			throw new BuildException("'codebase' required");
		}
	}

}

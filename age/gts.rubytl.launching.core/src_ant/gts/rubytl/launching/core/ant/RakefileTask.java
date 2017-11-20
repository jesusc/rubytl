package gts.rubytl.launching.core.ant;

import gts.rubytl.launching.core.configuration.data.RakefileConfigurationData;
import gts.rubytl.launching.core.launcher.RakefileLauncher;

import org.apache.tools.ant.BuildException;

/**
 * Definition of an Ant task for launching a rakefile.
 * 
 * <code>
 *   <rubytl.rakefile name="mytask" ruby="ruby" rubytl="/home/jesus/mdd/rubytl">
 *     <rakefile path="myrakefile.rake">
 *   </rubytl.rakefile>
 * </code>
 * 
 * @author jesus
 */
public class RakefileTask extends AbstractRubytlTask {
	private TaskElements.PathElement rakefilePath;
	private TaskElements.NameElement rakeTaskName;

	
	public TaskElements.PathElement createRakefile() {
		this.rakefilePath = new TaskElements.PathElement();
		return this.rakefilePath;
	}

	public TaskElements.NameElement createRaketask() {
		this.rakeTaskName = new TaskElements.NameElement();
		return this.rakeTaskName;
	}

	protected void executeRubyTL(String projectPath, Configuration configuration) {
		try {
			RakefileConfigurationData taskData = new RakefileConfigurationData(configuration.getRuby(), configuration.getRubytl(), projectPath, this.rakefilePath.getPath());
			RakefileLauncher launcher = new RakefileLauncher(taskData);
			
			launcher.execute(this.rakeTaskName.getName());
		} catch ( Exception e ) {
			e.printStackTrace();
			throw new BuildException(e);
		}
		
	}	

	protected void checkTaskValues() throws BuildException {
		if ( this.rakeTaskName == null || this.rakeTaskName.getName() == null ) {
			throw new BuildException("'raketask' required"); 
		}
		if ( this.rakefilePath == null || this.rakefilePath.getPath() == null ) {
			throw new BuildException("'rakefile' required"); 
		}
	}
}

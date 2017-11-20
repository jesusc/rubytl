package gts.rubytl.launching.core.launcher;

import gts.rubytl.launching.core.Util;
import gts.rubytl.launching.core.configuration.IRakefileConfiguration;

/**
 * Launcher for Rakefile tasks.  
 * 
 * @author jesus
 */
public class RakefileLauncher extends AbstractRubyLauncher<IRakefileConfiguration> {

	public RakefileLauncher(IRakefileConfiguration configuration) {
		super(configuration);
	}

	/**
	 * Executes a given task for the loaded rakefile.
	 * 
	 * @param task The name of the task.
	 */
	public void execute(String task) {
		String ruby      = Util.changeOSSlashes(configuration.getRubyPath());
		String rubytl    = computeRubytlCommand(Util.changeOSSlashes(configuration.getRtlPath()));
		String taskFile  = Util.changeOSSlashes(configuration.getRakefilePath());
		String taskName  = task;
		String project   = Util.changeOSSlashes(configuration.getProjectPath());


		//Ejemplo de ejecuci�n correcta de rake
		/*
		String ejecucion_rake = 
			"ruby C:\\ruby\\rubytl\\lib\\rubytl.rb C:\\age\\workspace\\Class2Table\\tasks\\run_class2table.rake Class2Table C:\\age\\workspace\\Class2Table\\";
		*/
		String rakeExec = ruby + " " + rubytl + " " + taskFile + " " + taskName + " " + project;
		launchCommand(rakeExec);
	}
	
}

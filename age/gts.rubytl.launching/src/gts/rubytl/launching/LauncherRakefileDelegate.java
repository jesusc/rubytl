package gts.rubytl.launching;

import gts.age.preferences.PreferenceHelper;
import gts.eclipse.core.resources.ResourcesUtil;
import gts.rubytl.core.RubytlCore;
import gts.rubytl.core.resources.RubytlPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.rubypeople.rdt.internal.launching.LaunchingMessages;
import org.rubypeople.rdt.internal.launching.LaunchingPlugin;
import org.rubypeople.rdt.launching.IVMRunner;
import org.rubypeople.rdt.launching.RubyRuntime;
import org.rubypeople.rdt.launching.VMRunnerConfiguration;

public class LauncherRakefileDelegate implements
		ILaunchConfigurationDelegate {

	IProject project = null;
	IPath rakefile = null;
	String rakeTask = null;
	
	ILaunch launch;
	List<RubytlPlugin> plugins = new LinkedList<RubytlPlugin>();
	private String executionMode;
	
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {

		String strProject = getValue(configuration, ILauncherConstants.PROJECT);
		String strRakefile = getValue(configuration, ILauncherConstants.RAKEFILE);
		String strRakeTask = getValue(configuration, ILauncherConstants.RAKE_TASK);		

		if ( strRakeTask.trim().length() == 0 ) {
			strRakeTask = "generate";
		}
		
		this.project = RubytlCore.getProjectByName(strProject);
		if ( new Path(strRakefile).isAbsolute() ) {
			this.rakefile = new Path(strRakefile);
		} else {
			this.rakefile = ResourcesUtil.getFile(project, strRakefile).getProjectRelativePath();
		}
		this.rakeTask = strRakeTask;
		this.launch = launch;
		this.executionMode = mode;
		
		execute(rakefile, monitor);
	}

	

	public void execute(IPath rakefile, IProgressMonitor monitor) throws CoreException {
		delegateInNativeRubyInterpreter(monitor);
	}
	

	
	private void delegateInNativeRubyInterpreter(IProgressMonitor monitor) throws CoreException  {
		IVMRunner runner = RubyRuntime.getDefaultVMInstall().getVMRunner(executionMode);
	
		String path = getRubyTL();
		// A hack to allow Win32 to call the Ruby interpreters 
		if ( File.separatorChar == '\\' ) {
			path = path.substring(1).replace('/', '\\');		
		}
		
		VMRunnerConfiguration configuration = new VMRunnerConfiguration(path, new String[0]);
		File workingDirectory = this.rakefile.removeLastSegments(1).toFile();
		String[] args;
		if ( this.project != null ) {
			args = new String[4];
			args[3] = this.project.getLocation().toOSString();
			workingDirectory = project.getLocation().toFile();
		} else {
			args = new String[2];
		}

		args[0] = path;
		args[1] = this.rakefile.toOSString();
		args[2] = this.rakeTask;

		configuration.setVMArguments(args);
		configuration.setWorkingDirectory(workingDirectory.getAbsolutePath());
		runner.run(configuration, launch, monitor);
		
		
		
		/*
		RubyInterpreter interpreter = RubyRuntime.getDefault().getSelectedInterpreter();
		
		String path = getRubyTL();
				
		// A hack to allow Win32 to call the Ruby interpreters 
		if ( File.separatorChar == '\\' ) {
			path = path.substring(1).replace('/', '\\');		
		}
		
		File workingDirectory = this.rakefile.removeLastSegments(1).toFile();
		List<String> commandLine = new ArrayList<String>(); 
		commandLine.add(path);
		commandLine.add(this.rakefile.toOSString());
		commandLine.add(this.rakeTask);
		
		if ( this.project != null ) {
			commandLine.add(this.project.getLocation().toOSString());
			workingDirectory = project.getLocation().toFile();
		}
		
		if (interpreter == null) {
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, IStatus.OK, LaunchingMessages.RdtLaunchingPlugin_interpreterNotFound, null));
        }
		
		try {	
			Process nativeRubyProcess = interpreter.exec(commandLine, workingDirectory);
	        Map defaultAttributes = new HashMap();
	        defaultAttributes.put(IProcess.ATTR_PROCESS_TYPE, "rubytl");
	        @SuppressWarnings("unused") IProcess process = DebugPlugin.newProcess(launch, nativeRubyProcess, "rakefile", defaultAttributes);
			// return process ;
		} catch ( Exception e ) {
			// Avisar de lo que pase...
			e.printStackTrace();
		}
		*/
	}


	
	private String getValue(ILaunchConfiguration configuration, String constant) {
		try {
			String value = configuration.getAttribute(constant, "");
			return value;
		} catch (CoreException e) { 
			throw new RuntimeException("Error en el atributo " + constant);			
		}		 
	}

	private String getRubyTL() {
		if ( PreferenceHelper.useAlternativePath() ) {
			return ResourcesUtil.joinPath(PreferenceHelper.alternativePath(), "lib", "rubytl.rb");
		} 
		else {
			String root = RubytlCore.getDefault().getLibraryPath();	
			return ResourcesUtil.joinPath(root, "rubytl", "lib", "rubytl.rb");		
		}
	}
}

package gts.rubytl.launching;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

public class LauncherHelper {

	public static void transformRuby2XMI(IProject project, String rubyModel) {
		// TODO Auto-generated method stub		
		try {
			File tempfile = File.createTempFile("ruby2xmi", ".temp.rakefile");
		    
			
	        BufferedWriter out = new BufferedWriter(new FileWriter(tempfile));
	        out.write("ruby2xmi :ruby2xmi do |t| \n");
	        out.write("	t.model '" + rubyModel + "' \n");
	        out.write("end\n");
	        out.close();
   
	        ILaunchConfiguration config = null;
			try {
				ILaunchConfigurationType configType = getLaunchConfigType();
				ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, "temp-ruby2xmi");
				wc.setAttribute(ILauncherConstants.PROJECT, project.getName());
				wc.setAttribute(ILauncherConstants.RAKEFILE, tempfile.getAbsolutePath());
				wc.setAttribute(ILauncherConstants.RAKE_TASK, "ruby2xmi");
				config = wc;
				//config = wc.doSave();
			} catch (CoreException ce) {
				// TODO: Show error message
				throw new RuntimeException(ce);
			}
			config.launch("run", null);

	        //tempfile.delete();
		} catch (CoreException e) {
			// TODO: Show error message
			throw new RuntimeException(e);
		} catch (IOException e) {
			// TODO: Show error message
			throw new RuntimeException(e);
	    }		
	}
	
	/**
	 * TODO: Duplicate code from RakefileLaunchShortcut.
	 * @return
	 */
	protected static ILaunchConfigurationType getLaunchConfigType() {
		return getLaunchManager().getLaunchConfigurationType(ILauncherConstants.RAKEFILE_LAUNCH_CONFIGURATION_TYPE);
	}

	/**
	 * TODO: Duplicate code from RakefileLaunchShortcut.
	 * @return
	 */
	protected static ILaunchManager getLaunchManager() {		
		return DebugPlugin.getDefault().getLaunchManager();
	}


}

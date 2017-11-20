package gts.rubytl.launching;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class LauncherPlugin extends Plugin {

	public static final String PLUGIN_ID = "gts.rubytl.launching";
	
	//The shared instance.
	private static LauncherPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public LauncherPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		// Para que funcione el MDR
		Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader() );			
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static LauncherPlugin getDefault() {
		return plugin;
	}


}

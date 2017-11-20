package gts.rubytl.debug.ui;

import gts.rubytl.core.i18n.BundleManager;

import org.eclipse.ui.plugin.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class DebugUIPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "gts.rubytl.debug.ui";
	//The shared instance.
	private static DebugUIPlugin plugin;
	public static BundleManager MESSAGES;

	
	/**
	 * The constructor.
	 */
	public DebugUIPlugin() {
		plugin = this;
		DebugUIPlugin.MESSAGES = new BundleManager("gts.rubytl.debug.ui.Messages", DebugUIPlugin.class);
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
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
	public static DebugUIPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("gts.rubytl.debug.ui", path);
	}
}

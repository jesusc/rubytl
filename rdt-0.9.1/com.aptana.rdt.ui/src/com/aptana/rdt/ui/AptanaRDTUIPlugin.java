package com.aptana.rdt.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.rubypeople.rdt.core.RubyModelException;

/**
 * The activator class controls the plug-in life cycle
 */
public class AptanaRDTUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.rdt.ui";

	// The shared instance
	private static AptanaRDTUIPlugin plugin;
	
	/**
	 * The constructor
	 */
	public AptanaRDTUIPlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AptanaRDTUIPlugin getDefault() {
		return plugin;
	}

	public static void log(RubyModelException e) {
		getDefault().getLog().log(e.getStatus());		
	}

}

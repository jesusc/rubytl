package org.rubypeople.rdt.internal.cheatsheets;

import org.eclipse.ui.plugin.AbstractUIPlugin;


public class RdtPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.rubypeople.rdt"; //$NON-NLS-1$
	protected static RdtPlugin plugin;
	
	public RdtPlugin() {
		super();
		plugin = this;
	}
	
	public static RdtPlugin getDefault() {
		return plugin;
	}
}


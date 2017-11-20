package gts.rubytl.core.resources;

import gts.eclipse.core.resources.ResourcesUtil;

import java.io.File;

/**
 * Class to define a RubyTL plugin.
 * 
 * @author jesus
 */
public class RubytlPlugin {
	protected String name;
	protected String path;
	protected boolean belongsToDefaultPath;
	
	/**
	 * Creates a new definition of a RubyTL plugin.
	 * @param name The plugin name.
	 * @param path The plugin directory path.
	 */
	public RubytlPlugin(String name, String path) {
		this.name = name;
		this.path = path;
		this.belongsToDefaultPath = false;
	}
	
	/**
	 * Returns the plugin name.
	 * @return The plugin name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * The path of the directory where the plugin is defined.
	 * @return The plugin directory.
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Returns an unique name for the plugin. It is composed of
	 * the plugin's name plus a colon followed by its path.
	 * 
	 * @return An unique name for the plugin.
	 */
	public String uniqueName() {
		return name + ":" + path;
	}
	
	/**
	 * Parses a string a returns a serialized RubytlPlugin.
	 * @return a RubytlPlugin corresponding to the information serialized in str.
	 */
	public static RubytlPlugin fromString(String str) {
		String[] parts = str.split(":");
		return new RubytlPlugin(parts[0], parts[1]);
	}
	
	/**
	 * Return the plugin name as a Ruby symbol.
	 * @return The plugin name as a Ruby symbol.
	 */
	public String getRubyName() {
		return ":" + name;
	}

	/**
	 * Returns the path of the file where the plugin UI is
	 * defined (using the UI definition DSL). The the plugin does not
	 * provide any ui definition, null is returend.
	 * 
	 * @return The file path where the plugin UI is defined or null
	 * if no definition exists.
	 */
	public String getUIDefinitionPath() {
		String uiPath = ResourcesUtil.joinPath(getPath(), "ui.rb");
		File f = new File(uiPath);
		if ( f.exists() && f.canRead() ) 
			return uiPath;
		return null;
	}
	
	/**
	 * Sets whether the plugin is stored in the RubyTL default path
	 * for plugins, instead in a user specific location.
	 * @param b true if the plugin is in the RubyTL default plugin path.
	 */
	public void setBelongsToDefaultPath(boolean b) {
		this.belongsToDefaultPath = b;
	}

	/**
	 * Returns whether the plugin is stored in the RubyTL default path
	 * for plugins, instead in a user specific location.
	 * @return true if the plugin is in the RubyTL default plugin path.
	 */
	public boolean getBelongsToDefaultPath() {
		return this.belongsToDefaultPath;
	}

	/**
	 * Two plugins are the same if their unique name is the same.
	 */
	public boolean equals(Object obj) {
		return (obj != null) && this.uniqueName().equals(((RubytlPlugin) obj).uniqueName());
	}

}


package gts.rubytl.core.resources;

import gts.eclipse.core.resources.NamedResourceMatcher;
import gts.eclipse.core.resources.ResourceMatcher;
import gts.eclipse.core.resources.ResourcesUtil;
import gts.rubytl.core.RubytlCore;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * A class to manage all information related to plugins: available plugins,
 * default plugin path, plugin loading, etc. 
 * 
 * @author jesus
 */
public class PluginManager {

	/**
	 * Returns the list of available plugins in the configured plugin paths
	 * and in the default plugin path.
	 * 
	 * @param paths Paths added by the user
	 * @return A List of RubyTL plugins in the path
	 */
	public List<RubytlPlugin> getAvailablePlugins(String[] paths) {
		ArrayList<RubytlPlugin> result = new ArrayList<RubytlPlugin>();
		
		result.addAll(findPlugins(defaultPluginsPath()));
		for (RubytlPlugin plugin : result) {
			plugin.setBelongsToDefaultPath(true);
		}
		
		for (String path : paths) {
			List<RubytlPlugin> foundPlugins = findPlugins(path);
			foundPlugins.removeAll(result); // Remove duplicates
			result.addAll(foundPlugins);
		}
				
		return result;
	}

	/**
	 * The path where default plugins are placed. Usually related to 
	 * RubyTL library path.
	 * @return
	 */
	public String defaultPluginsPath() {
		return ResourcesUtil.joinPath(RubytlCore.getDefault().getLibraryPath(), "rubytl", "plugins");
	}

	private List<RubytlPlugin> findPlugins(String path) {
		List<RubytlPlugin> plugins = new LinkedList<RubytlPlugin>();
		List<File> pluginDirectories = traverseFileSystem(new File(path), new PluginMatcher(), 1);		
		
		for (File file : pluginDirectories) {
			plugins.add(new RubytlPlugin(file.getName(), file.getAbsolutePath()));
		}
		
		return plugins;
	}
	
	private List<File> traverseFileSystem(File f, ResourceMatcher matcher, int deepLimit) {
		List<File> result = new LinkedList<File>();		
		if ( f.exists() ) {			
			if ( matcher.match(f) ) {
				result.add(f);
			}			
			if ( f.isDirectory() ) {
				if ( deepLimit == 0 ) return result;		

				for (File child : f.listFiles()) {
					List<File> childs = traverseFileSystem(child, matcher, deepLimit - 1);
					result.addAll(childs);
				}
			}
		}		
		return result;
	}
	
	private class PluginMatcher extends ResourceMatcher {
		public boolean match(File fileResource) {
			if ( fileResource.isDirectory() ) {
				String rbPluginName = fileResource.getName() + ".rb"; 
				List<File> files = traverseFileSystem(fileResource, new NamedResourceMatcher(rbPluginName), 1);
				if ( files.size() == 1 ) 
					return true;
			}
			return false;
		}
		
	}
}

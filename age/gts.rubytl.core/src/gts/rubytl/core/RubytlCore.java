package gts.rubytl.core;

import gts.eclipse.core.PluginUtil;
import gts.eclipse.core.resources.ResourcesUtil;
import gts.rubytl.core.resources.PluginManager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
// public class CorePlugin extends AbstractUIPlugin {
public class RubytlCore extends Plugin {
    public final static String PLUGIN_ID = "gts.rubytl.core";
    public final static String NATURE_ID = PLUGIN_ID + ".rubytlnature";
    // Ojo! En el plugin el id es simplemente rubyatlnature (¿lo añade luego eclipse?)
    
	//The shared instance.
	private static RubytlCore plugin;
	private PluginManager pluginManager;
	
	/**
	 * The constructor.
	 */
	public RubytlCore() {
		plugin = this;
		pluginManager = new PluginManager();
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
	public static RubytlCore getDefault() {
		return plugin;
	}
	
	/**
	 * Returns the current workspace
	 * @return the current workspace
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Add the ruby nature to an existing project
	 * @param project
	 * @param monitor
	 * @throws CoreException 
	 */
	public static void addRubyNature(IProject project, SubProgressMonitor monitor) throws CoreException {
		/*
		if (! project.hasNature(RubytlCore.NATURE_ID)) {
            IProjectDescription description = project.getDescription();
            String[] prevNatures = description.getNatureIds();
            String[] newNatures = new String[prevNatures.length + 1];
            System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
            newNatures[prevNatures.length] = RubytlCore.NATURE_ID;
            description.setNatureIds(newNatures);
            project.setDescription(description, monitor);
        }
        */
		throw new RuntimeException("Deprecated");
	}

	/**
	 * Get all RubyAtl projects
	 * @return
	 */
	public static IProject[] getRubytlProjects() {
        List rubyProjectsList = new ArrayList();
        IProject[] workspaceProjects = RubytlCore.getWorkspace().getRoot().getProjects();

        for (int i = 0; i < workspaceProjects.length; i++) {
            IProject iProject = workspaceProjects[i];
            if (isRubyAtlProject(iProject)) rubyProjectsList.add(iProject);
        }

        IProject[] rubyProjects = new IProject[rubyProjectsList.size()];
        return (IProject[]) rubyProjectsList.toArray(rubyProjects);        
	}
	
    /**
     * Get a project given its name (only projects with RubyAtl nature)
     * @param projectName The name of the project
     * @return the project 
     */
	public static IProject getProjectByName(String projectName) {
		IProject[] projects = getRubytlProjects();
		for(int i = 0; i < projects.length; i++) {
			if ( projects[i].getName().equals(projectName) ) {
				return projects[i];
			}
		}
		return null;
	}


	
	public static boolean isRubyAtlProject(IProject aProject) {
        try {
        	return aProject.hasNature(RubytlCore.NATURE_ID);
        } catch (CoreException e) {}
        
        return false;
    }

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public String getLibraryPath() {
		return ResourcesUtil.joinPath(PluginUtil.getInstalledPath(this.getBundle()), "lib");
	}

	
	
}

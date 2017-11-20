package gts.eclipse.core.resources;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

/**
 * A class which can be configured to create certain kinds
 * of project.
 * 
 * Example:
 * <pre>
 * 		// First, the project is configured
 * 		prj = new ProjectCreator();
 * 		prj.hasFolder("models")
 * 		prj.hasFolder("metamodels")
 * 		prj.hasFolder("transformations")
 * 
 * 		prj.hasNature("yourNatureId")
 * 		
 * 		// Finally, the project is created
 * 		prj.createProject(aWorkspace)
 * </pre>
 * 
 * @author jesus
 */
public class ProjectCreator {
	ArrayList<String> folders = new ArrayList<String>();
	ArrayList<String> natureIds = new ArrayList<String>();
	String name = null;
	IPath customPath = null;
	
	/**
	 * Creates a new object to configure the project creation.
	 */
	public ProjectCreator() {
		
	}
	
	/**
	 * Sets the name of the project.
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Sets the custom path for the project. If <code>path</code>
	 * is <code>null</code> a default path relative to the current
	 * workspace is used.
	 * 
	 * @param path A custom path for the project.
	 */
	public void setCustomPath(IPath path) {
		this.customPath = path;
	}
	
	/**
	 * Configuration method to state that the project has
	 * a method called <code>folder</code>.
	 * 
	 * @param folder The folder name
	 */
	public void hasFolder(String folder) {
		folders.add(folder);
	}

	/**
	 * Configuration method to add natures to the project.
	 * 
	 * @param natureId The ID of the nature being added
	 */
	public void hasNature(String natureId) {
		natureIds.add(natureId);
	}

	/**
	 * Creates a new project based on the previously established
	 * configuration.  
	 * It uses a progress monitor to report the UI about progress. 
	 * @throws CoreException 
	 *  
	 * @params workspace The workspace where the project is created.
	 * @params monitor A progress monitor
	 */
	public void createProject(IWorkspace workspace, IProgressMonitor monitor) throws CoreException {
        IProject newProject = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
        IProjectDescription description = workspace.newProjectDescription(newProject.getName());
        IPath path = Platform.getLocation();
        if ( customPath != null && ! path.equals(customPath)) {
        	path = customPath;
        	description.setLocation(path);
        }					

        if (! newProject.exists()) {
            newProject.create(description, monitor);
        }
        
        if (! newProject.isOpen()) {
            newProject.open(monitor);
        }
        
        for (int i = 0; i < natureIds.size(); i++) {
        	ResourcesUtil.addNature(newProject, natureIds.get(i));	
		}
        
        for (String folder : folders) {
			ResourcesUtil.createFolder(newProject, folder);
		}
	}
	
	/**
	 * Creates a new project based on the previously established
	 * configuration. 
	 * @throws CoreException 
	 * 
	 * @params workspace The workspace where the project is created.
	 */
	public void createProject(IWorkspace workspace) throws CoreException {
		createProject(workspace, null);
	}
	
}

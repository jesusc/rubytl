
package gts.eclipse.core.resources;

import java.io.File;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


/**
 * A helper class with static methods to perform common tasks over
 * resources (files, folders, projects) 
 * 
 * @author jesus
 */
public class ResourcesUtil {

	/**
	 * Refreshes a file placed in a container (a project, a folder)
	 * @param container The parent container
	 * @param resourceName The resource name (a file, a folder...)
	 * @throws CoreException 
	 */
	public static void refreshFile(IContainer container, String resourceName) throws CoreException {		
		IWorkspaceRoot workspaceRoot = container.getWorkspace().getRoot();
        IPath path = container.getFullPath().append(resourceName);
        IFile file = workspaceRoot.getFile(path);
        file.refreshLocal(1, null);
	}
	
	/**
	 * Creates a  new folder under a given container (a project, another folder...).
	 * 
	 * @param container The parent container 
	 * @param folderName The folder name
	 * @throws CoreException 
	 */
	public static void createFolder(IContainer container, String folderName) throws CoreException {
		IWorkspaceRoot workspaceRoot = container.getWorkspace().getRoot();
        IPath folderPath = container.getFullPath().append(folderName);
        IFolder folderHandle = workspaceRoot.getFolder(folderPath);

        folderHandle.create(true, true, null);
	}
	
	public static void addNature(IProject project, String natureId) throws CoreException {
        if (! project.hasNature(natureId)) {
            IProjectDescription description = project.getDescription();
            String[] prevNatures = description.getNatureIds();
            String[] newNatures = new String[prevNatures.length + 1];
            System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
            newNatures[prevNatures.length] = natureId;
            description.setNatureIds(newNatures);
            // project.setDescription(description, monitor);
            project.setDescription(description, null);
        }		
	}
	
	public static IFile getFile(IProject project, String fileName) {
		IPath filePath = new Path(fileName);
		return project.getFile(filePath);
	}

	/**
	 * Return a path string composed by one or more elements joined
	 * by the OS dependent separator (obtained by means of <code>File.separator</code>).
	 * 
	 * @param root The first path segment
	 * @param files One or more path segments
	 * @return A string with all the strings joined
	 */
	public static String joinPath(String root, String... files) {
		String result = root;
		for (String file : files) {
			result += File.separator + file;
		}
		return result;
	}
}

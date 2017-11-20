package gts.rubytl.ui.wizards;

import gts.eclipse.core.resources.ProjectCreator;
import gts.rubytl.core.RubytlCore;
import gts.rubytl.ui.PluginImages;
import gts.rubytl.ui.i18n.RubytlMessages;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "rbt". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */
public class NewProjectCreationWizard extends Wizard implements INewWizard {
	public static final String ID = "gts.rubytl.ui.wizards.NewRubyTLProjectCreationWizard";
	
	WizardNewProjectCreationPage page;
	IProject newProject;
	
	/**
	 * Constructor for NewProjectCreationWizard.
	 */
	public NewProjectCreationWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	
	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
		// page = new NewProjectCreationPage(selection);		
		page = new WizardNewProjectCreationPage(RubytlMessages.getString("WizardNewProjectCreationPage.pageName"));
		page.setTitle(RubytlMessages.getString("WizardNewProjectCreationPage.pageTitle"));
		page.setDescription(RubytlMessages.getString("WizardNewProjectCreationPage.pageDescription"));
		page.setInitialProjectName(getDefaultProjectName());
		page.setImageDescriptor(PluginImages.DESC_NEW_PROJECT_WIZARD);

		
		addPage(page);
	}

	private String getDefaultProjectName() {
		return "meta2meta";
	}


	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					doFinish(monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			getContainer().run(false, true, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			realException.printStackTrace();
			MessageDialog.openError(getShell(), "Error!!", realException.getMessage());
			return false;
		}
		return true;
	}
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 * @throws InvocationTargetException 
	 */
	private void doFinish(IProgressMonitor monitor) throws CoreException, InvocationTargetException {
		ProjectCreator prj = new ProjectCreator();

		prj.setName(page.getProjectName());
		prj.setCustomPath(page.getLocationPath());

		prj.hasFolder("metamodels");
		prj.hasFolder("models");
		prj.hasFolder("helpers");
		prj.hasFolder("transformations");
		prj.hasFolder("tasks");
		prj.hasFolder("validations");

		
		prj.hasNature(RubytlCore.NATURE_ID);
		
		prj.createProject(RubytlCore.getWorkspace(), new SubProgressMonitor(monitor, 1));
		
		/*		
		int remainingWorkUnits = 3;
		monitor.beginTask(RubytlMessages.getString("NewProjectCreationWizard.projectCreationMessage"), remainingWorkUnits);
		
		IWorkspace workspace = RubytlCore.getWorkspace();
		newProject = page.getProjectHandle();
		


        try {
            if (! newProject.exists()) {
                    newProject.create(description, new SubProgressMonitor(monitor, 1));
                    remainingWorkUnits--;
            }
            if (! newProject.isOpen()) {
                    newProject.open(new SubProgressMonitor(monitor, 1));
                    remainingWorkUnits--;
            }

            ResourcesUtil.createFolder(newProject, "metamodels");
            ResourcesUtil.createFolder(newProject, "models");
            ResourcesUtil.createFolder(newProject, "transformations");
            
            RubytlCore.addRubyNature(newProject, new SubProgressMonitor(monitor, remainingWorkUnits));
       	} catch (CoreException e) {
       		throw new InvocationTargetException(e);
       	}
       	*/
        
		
		/*
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.");
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		try {
			InputStream stream = openContentStream();
			if (file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (IOException e) {
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
		*/
	}
	

	
	/**
	 * We will initialize file contents with a sample text.
	 */

	private InputStream openContentStream() {
		String contents =
			"This is the initial file contents for *.rbt file that should be word-sorted in the Preview page of the multi-page editor";
		return new ByteArrayInputStream(contents.getBytes());
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "gts.rubytl.ui", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
	
}
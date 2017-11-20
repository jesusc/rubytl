package gts.age.ui.wizards;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;



public class NewMetamodelWizard extends Wizard implements INewWizard {
	public static final String ID = "gts.age.ui.NewMetamodelWizard";
	
	WizardNewFileCreationPage page;
	IStructuredSelection selection;
	
	/**
	 * Constructor for NewProjectCreationWizard.
	 */
	public NewMetamodelWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	
	/**
	 * Adding the page to the wizard.
	 */
	public void addPages() {
		// TODO: Internalizacionalizar
		page = new WizardNewFileCreationPage("New metamodel", selection);		
		page.setTitle("New metamodel");
		page.setDescription("New metamodel");
		// page.setImageDescriptor(PluginImages.DESC_NEW_TRANSFORMATION_WIZARD);
		page.setFileName("Metamodel.ecore");
		addPage(page);
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
		IFile file = page.createNewFile();
		if ( file.isLinked() == false ) {
			InputStream is = openContentStream(file);
			file.appendContents(is, false, false, monitor);
		}
	}
	

	
	/**
	 * We will initialize file contents with a sample text.
	 * @param file 
	 */
	private InputStream openContentStream(IFile file) {
		String name = file.getName();
		name = name.substring(0, name.indexOf("." + file.getFileExtension()));
//		String contents = "<?xml version='1.0' encoding='UTF-8'?>" + "\n" +
//							"<emof:Package xmi:version='2.0' " + "\n\t" +
//							"xmlns:xmi='http://www.omg.org/XMI' xmlns:emof='http://schema.omg.org/spec/mof/2.0/emof.xmi' " + 
//							"xmi:id='" + name + "' name='"  + name + "'" + ">" + "\n" +
//							"</emof:Package>";
		String contents = "<?xml version='1.0' encoding='UTF-8'?>" + "\n" +
				"<ecore:EPackage xmi:version='2.0' " + "\n" +
				"xmlns:xmi='http://www.omg.org/XMI' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' " +
				"xmlns:ecore='http://www.eclipse.org/emf/2002/Ecore' name='" + name + "' " + "/>";

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
		this.selection = selection;
	}
	
}
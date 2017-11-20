package gts.age.ui.popup.actions;

import gts.rubytl.launching.LauncherHelper;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.dialogs.FileSelectionDialog;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;
import org.eclipse.ui.internal.PluginAction;

public class Ruby2XMI implements IObjectActionDelegate {

	private StructuredSelection selection;
	private Shell shell;
	
	/**
	 * Constructor for Action1.
	 */
	public Ruby2XMI() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
		selection = (StructuredSelection) targetPart.getSite().getSelectionProvider().getSelection();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		//MessageDialog.openInformation(shell, "Not implemented", "This feature is planned for the next version");
		/*		
		FileDialog dialog = new FileDialog(shell);
		dialog.setFilterPath(file.getProject().getLocation().toOSString());
		String filename = dialog.open();

		if ( filename != null ) {
			LauncherHelper.transformRuby2XMI(file.getProject(),
											 file.getLocation().toOSString(),
											 filename);
		}
		 */
		IFile file = (IFile) selection.getFirstElement();
		LauncherHelper.transformRuby2XMI(file.getProject(),
				 file.getLocation().toOSString());
		
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}

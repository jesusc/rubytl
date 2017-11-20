package gts.age.ui.popup.actions;

import gts.eclipse.core.resources.ResourcesUtil;
import gts.eclipse.emf.tasks.MetamodelConverter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.PluginAction;

public class Emof2Ecore implements IObjectActionDelegate {

	private StructuredSelection selection;
	
	/**
	 * Constructor for Action1.
	 */
	public Emof2Ecore() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		selection = (StructuredSelection) targetPart.getSite().getSelectionProvider().getSelection();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		IFile file = (IFile) selection.getFirstElement();
		String target = new MetamodelConverter().convertToEcore(file.getLocation().toFile());
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}

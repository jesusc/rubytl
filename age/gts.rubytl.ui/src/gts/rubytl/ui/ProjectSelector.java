package gts.rubytl.ui;

import gts.rubytl.core.RubytlCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class ProjectSelector extends ResourceSelector {

	public ProjectSelector(Composite parent, String label) {
		super(parent, label);
		
		browseDialogTitle = "Project Selection";
	}

	public IProject getSelection() {
		String projectName = getSelectionText();
		if (projectName != null && !projectName.equals(""))
			return RubytlCore.getWorkspace().getRoot().getProject(projectName);
			
		return null;
	}

	protected void handleBrowseSelected() {
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new WorkbenchLabelProvider());
		dialog.setTitle(browseDialogTitle);
		dialog.setMessage(browseDialogMessage);
		dialog.setElements(RubytlCore.getRubytlProjects());

		if (dialog.open() == Dialog.OK) {
			textField.setText(((IProject) dialog.getFirstResult()).getName());
		}
	}

	protected String validateResourceSelection() {
		IProject project = getSelection();
		return project == null ? EMPTY_STRING : project.getName();
	}
}
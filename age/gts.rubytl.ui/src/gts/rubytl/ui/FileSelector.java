package gts.rubytl.ui;

import gts.rubytl.core.resources.AbstractElementVisitor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public abstract class FileSelector extends ResourceSelector {

	public boolean mustExists;
	protected ProjectSelector projectSelector;
	
	public FileSelector(Composite parent, ProjectSelector aProjectSelector, String label) {
		super(parent, label);

		projectSelector = aProjectSelector;
		mustExists = true;
	}	
	
	public Object[] getFiles() {
        IProject project = projectSelector.getSelection();
        if (project == null)
     		return new Object[0];

        AbstractElementVisitor visitor = getVisitor();
        try {
			project.accept(visitor);
		} catch (CoreException e) {
			// TODO: Poner un log en RubytlCore
			e.printStackTrace();
		}
        return visitor.getCollectedFiles();				
	}
	
	public abstract AbstractElementVisitor getVisitor();
	
	protected void handleBrowseSelected() {
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new WorkbenchLabelProvider());
		dialog.setTitle(browseDialogTitle);
		dialog.setMessage(browseDialogMessage);
		dialog.setElements(getFiles());

		if (dialog.open() == Dialog.OK) {
			textField.setText(((IFile) dialog.getFirstResult()).getProjectRelativePath().toString());
		}
	}
	
	public IFile getSelection() {
		String fileName = getSelectionText();
	    if (fileName != null && !fileName.equals("")) {
	    	IPath filePath = new Path(fileName);
	    	IProject project = projectSelector.getSelection();
	    	if (project != null && project.exists(filePath))
	    		return project.getFile(filePath);
	    }
	
	    return null;
	}
	
	public String validateResourceSelection() {
		IFile file = getSelection();
		return file == null ? EMPTY_STRING : file.getProjectRelativePath().toString();
	}

	/**
	 * By default the text is valid if is a not empty string. Subclasses may override
	 * this method to perform a more sofisticated validation
	 * @return
	 */
	public boolean isValid() {
		if ( mustExists ) {
			return super.isValid();
		}
		return getSelectionText().trim().length() > 0;
	}	
}

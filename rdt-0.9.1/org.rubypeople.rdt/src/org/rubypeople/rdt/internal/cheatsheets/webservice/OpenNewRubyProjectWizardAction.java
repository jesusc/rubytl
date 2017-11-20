package org.rubypeople.rdt.internal.cheatsheets.webservice;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.wizards.NewProjectCreationWizard;


/**
 * @author markus
 *
 */
public class OpenNewRubyProjectWizardAction extends Action implements ICheatSheetAction  {
	public OpenNewRubyProjectWizardAction() {
		super("OpenProject"); //$NON-NLS-1$
	}
	
	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run() {
		run(new String [] {}, null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.cheatsheets.ICheatSheetAction#run(java.lang.String[], org.eclipse.ui.cheatsheets.ICheatSheetManager)
	 */
	public void run(String[] params, ICheatSheetManager manager) {
		NewProjectCreationWizard wizard = new NewProjectCreationWizard();
		if (params.length > 0) {
			wizard.setDefaultProjectName(params[0]) ;
		}
		wizard.init(PlatformUI.getWorkbench(), new StructuredSelection());
		WizardDialog dialog = new WizardDialog(RubyPlugin.getActiveWorkbenchShell(), wizard);
		dialog.create();
		//SWTUtil.setDialogSize(dialog, 500, 500);
		dialog.getShell().setText(wizard.getWindowTitle());
		int result = dialog.open();
		notifyResult(result==WizardDialog.OK);
	}
}

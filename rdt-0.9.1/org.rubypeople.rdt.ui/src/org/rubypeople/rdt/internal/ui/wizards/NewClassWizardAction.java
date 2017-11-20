package org.rubypeople.rdt.internal.ui.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;

public class NewClassWizardAction extends AbstractOpenWizardAction {

    public NewClassWizardAction() {
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
                IRubyHelpContextIds.OPEN_CLASS_WIZARD_ACTION);
    }

    public NewClassWizardAction(String label, Class[] acceptedTypes) {
        super(label, acceptedTypes, true);
        PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
                IRubyHelpContextIds.OPEN_CLASS_WIZARD_ACTION);
    }
    
    protected Wizard createWizard() throws CoreException {
        return new NewClassCreationWizard();
    }

}

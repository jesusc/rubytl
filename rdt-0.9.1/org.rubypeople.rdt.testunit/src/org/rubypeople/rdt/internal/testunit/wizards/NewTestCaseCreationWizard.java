package org.rubypeople.rdt.internal.testunit.wizards;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.wizards.NewElementWizard;
import org.rubypeople.rdt.testunit.wizards.RubyNewTestCaseWizardPage;

public class NewTestCaseCreationWizard extends NewElementWizard {
	
	private RubyNewTestCaseWizardPage fPage;
	
	public NewTestCaseCreationWizard(RubyNewTestCaseWizardPage page) {
		setDefaultPageImageDescriptor(RubyPluginImages.DESC_WIZBAN_NEWCLASS);
		setDialogSettings(RubyPlugin.getDefault().getDialogSettings());
		setWindowTitle(WizardMessages.Wizard_title_new_testcase);
		
		fPage= page;
	}
	
	public NewTestCaseCreationWizard() {
		this(null);
	}
	
	/*
	 * @see Wizard#createPages
	 */	
	public void addPages() {
		super.addPages();
		if (fPage == null) {
			fPage= new RubyNewTestCaseWizardPage();
			fPage.init(getSelection());
		}
		addPage(fPage);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		fPage.createType(monitor); // use the full progress monitor
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		boolean res= super.performFinish();
		if (res) {
			IResource resource= fPage.getModifiedResource();
			if (resource != null) {
				selectAndReveal(resource);
				openResource((IFile) resource);
			}	
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#getCreatedElement()
	 */
	public IRubyElement getCreatedElement() {
		return fPage.getCreatedType();
	}
}
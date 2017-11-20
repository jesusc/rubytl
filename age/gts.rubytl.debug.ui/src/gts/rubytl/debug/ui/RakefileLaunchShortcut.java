package gts.rubytl.debug.ui;

import gts.rubytl.launching.ILauncherConstants;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;

public class RakefileLaunchShortcut implements ILaunchShortcut {

	public void launch(ISelection selection, String mode) {
		Object firstSelection = null;
		if (selection instanceof IStructuredSelection) {
			firstSelection = ((IStructuredSelection) selection).getFirstElement();

		}
		if (firstSelection == null) {
			return;
		}
		IRubyElement rubyElement = null;
		if (firstSelection instanceof IAdaptable) {
			rubyElement = (IRubyElement) ((IAdaptable) firstSelection).getAdapter(IRubyElement.class);
			try {
				doLaunch((IFile) rubyElement.getUnderlyingResource(), mode, adaptSelection(selection));
			} catch (RubyModelException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} catch (CoreException e) {
				MessageDialog.openInformation(DebugUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", e.getMessage());
			}
		}
		
	}

	public void launch(IEditorPart editor, String mode) {
		RubyEditor rubyEditor = (RubyEditor) editor;
		TextSelection textSelection = (TextSelection) rubyEditor.getSelectionProvider().getSelection();
		if ( textSelection == null ) {
			return;
		}

		String rakeTask = adaptSelection(textSelection); 
		IEditorInput input = editor.getEditorInput();
		if (input == null) {
			System.err.println("Could not retrieve input from editor: " + editor.getTitle());
			return;
		}
		IRubyElement rubyElement = (IRubyElement) input.getAdapter(IRubyElement.class);
		FileEditorInput fileEditorInput = (FileEditorInput) input.getPersistable();
		IFile fileResource = null;
		if ( rubyElement == null ) {
			fileResource = fileEditorInput.getFile();
		} else {
			try {
				fileResource = (IFile) rubyElement.getUnderlyingResource();
			} catch (RubyModelException e) { }
		}

		if ( fileResource == null ) {
			System.err.println("Can't get a file resource for the editor");
			throw new RuntimeException("Can't get a file resource for the editor");
		}
		
		try {
			doLaunch(fileResource, mode, rakeTask);
		} catch (RubyModelException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (CoreException e) {
			MessageDialog.openInformation(DebugUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", e.getMessage());
			// throw new RuntimeException(e);
		}
	}

	private String adaptSelection(ISelection selection) {
		TextSelection textSelection = (TextSelection) selection;
		String rakeTask = textSelection.getText().trim().replaceFirst("^:", "");
		checkRakeTask(rakeTask);
		return rakeTask;
	}

	protected void doLaunch(IFile fileResource, String mode, String rakeTask) throws CoreException {
		ILaunchConfiguration config = findOrCreateLaunchConfiguration(fileResource, mode, rakeTask);
		if (config != null) {
			config.launch(mode, null);
		}
	}
	
	/** Adapted from RDT */
	protected ILaunchConfiguration findOrCreateLaunchConfiguration(IFile fileResource, String mode, String rakeTask) throws CoreException {
		ILaunchConfigurationType configType = getLaunchConfigType();
		List<ILaunchConfiguration> candidateConfigs = null;

		ILaunchConfiguration[] configs = getLaunchManager().getLaunchConfigurations(configType);
		candidateConfigs = new ArrayList<ILaunchConfiguration>(configs.length);
		for (int i = 0; i < configs.length; i++) {
			ILaunchConfiguration config = configs[i];
			boolean projectsEqual = config.getAttribute(ILauncherConstants.PROJECT, "").equals(fileResource.getProject().getName());
			if (projectsEqual) {
				boolean projectRelativeFileNamesEqual = config.getAttribute(ILauncherConstants.RAKEFILE, "").equals(fileResource.getProjectRelativePath().toString());
				boolean rakeTaskEqual = config.getAttribute(ILauncherConstants.RAKE_TASK, "").equals(rakeTask);
				if (projectRelativeFileNamesEqual && rakeTaskEqual) {
					candidateConfigs.add(config);
				}
			}
		}

		switch (candidateConfigs.size()) {
		case 0:
			return createConfiguration(fileResource, rakeTask);
		case 1:
			return (ILaunchConfiguration) candidateConfigs.get(0);
		default:
			Status status = new Status(Status.WARNING, DebugUIPlugin.PLUGIN_ID, 0, "Multiple configurations", null);
			throw new CoreException(status);
		}
	}	

	protected ILaunchConfiguration createConfiguration(IFile rubyFile, String rakeTask) {
		ILaunchConfiguration config = null;
		try {
			ILaunchConfigurationType configType = getLaunchConfigType();
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, getLaunchManager().generateUniqueLaunchConfigurationNameFrom(rubyFile.getName() + " - " + rakeTask));
			wc.setAttribute(ILauncherConstants.PROJECT, rubyFile.getProject().getName());
			wc.setAttribute(ILauncherConstants.RAKEFILE, rubyFile.getProjectRelativePath().toString());
			wc.setAttribute(ILauncherConstants.RAKE_TASK, rakeTask);
			config = wc.doSave();
		} catch (CoreException ce) {
			throw new RuntimeException(ce);
		}
		return config;
	}
	
	protected ILaunchConfigurationType getLaunchConfigType() {
		return getLaunchManager().getLaunchConfigurationType(ILauncherConstants.RAKEFILE_LAUNCH_CONFIGURATION_TYPE);
	}

	/** Ask Eclipse's Debug plugin for the launch manager */
	protected ILaunchManager getLaunchManager() {		
		return DebugPlugin.getDefault().getLaunchManager();
	}
	
	protected void checkRakeTask(String rakeTask) {
		boolean error = (rakeTask.indexOf(" ") != -1) || ! rakeTask.matches("[a-zA-Z_][a-zA-Z0-9_]*");
		if ( error ) {
			String message = "Invalid identifier for a rake task: " + rakeTask;
			MessageDialog.openInformation(DebugUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", message);
			throw new RuntimeException(message);
		}
	}
}

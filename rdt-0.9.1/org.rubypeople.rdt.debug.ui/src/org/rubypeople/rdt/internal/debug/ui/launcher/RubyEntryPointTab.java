package org.rubypeople.rdt.internal.debug.ui.launcher;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiMessages;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.util.FileSelector;
import org.rubypeople.rdt.internal.ui.util.RubyProjectSelector;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;

public class RubyEntryPointTab extends AbstractLaunchConfigurationTab {
	protected String originalFileName, originalProjectName;
	protected RubyProjectSelector projectSelector;
	protected FileSelector fileSelector;

	public RubyEntryPointTab() {
		super();
	}

	public void createControl(Composite parent) {
		Composite composite = createPageRoot(parent);

		new Label(composite, SWT.NONE).setText(RdtDebugUiMessages.LaunchConfigurationTab_RubyEntryPoint_projectLabel);
		projectSelector = new RubyProjectSelector(composite);
		projectSelector.setBrowseDialogMessage(RdtDebugUiMessages.LaunchConfigurationTab_RubyEntryPoint_projectSelectorMessage);
		projectSelector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		projectSelector.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				updateLaunchConfigurationDialog();
			}
		});

		new Label(composite, SWT.NONE).setText(RdtDebugUiMessages.LaunchConfigurationTab_RubyEntryPoint_fileLabel);
		fileSelector = new FileSelector(composite);
		fileSelector.setBrowseDialogMessage(RdtDebugUiMessages.LaunchConfigurationTab_RubyEntryPoint_fileSelectorMessage);
		fileSelector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fileSelector.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				updateLaunchConfigurationDialog();
			}
		});
	}



	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		IResource selectedResource = RubyPlugin.getDefault().getSelectedResource();
		if (!RubyPlugin.getDefault().isRubyFile(selectedResource)) {
			return ;
		}
		IProject project = selectedResource.getProject() ;
		if (project == null || !RubyCore.isRubyProject(project)) {
			return ;
		}
		configuration.setAttribute(IRubyLaunchConfigurationConstants.ATTR_PROJECT_NAME, project.getName());
		configuration.setAttribute(IRubyLaunchConfigurationConstants.ATTR_FILE_NAME, selectedResource.getProjectRelativePath().toString()) ;
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			originalProjectName = configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
			originalFileName = configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_FILE_NAME, "");
		} catch (CoreException e) {
			log(e);
		}

		projectSelector.setSelectionText(originalProjectName);
		if (originalFileName.length() != 0) {
			fileSelector.setSelectionText(new Path(originalFileName).toOSString());
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(IRubyLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectSelector.getSelectionText());
		String text = fileSelector.getSelectionText();
		File test = new File(text);
		if (!test.exists()) {
			try {
				String workingDirectory = configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, (String)null);
				if (workingDirectory != null && !workingDirectory.trim().equals("")) {
					String blah = workingDirectory + text;
					test = new File(blah);
				}
			} catch (CoreException e) {
				RdtDebugUiPlugin.log(e);
			}
			if (!test.exists()) {
				String projectName = projectSelector.getSelectionText();
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
				if (project != null) {
					test = project.getLocation().append(text).toFile();
				}
			}
			if (!test.exists()) {
				text = "";
			}
		}
		configuration.setAttribute(IRubyLaunchConfigurationConstants.ATTR_FILE_NAME, text);
	}

	protected Composite createPageRoot(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		composite.setLayout(layout);

		setControl(composite);
		return composite;
	}

	public String getName() {
		return RdtDebugUiMessages.LaunchConfigurationTab_RubyEntryPoint_name;
	}

	public boolean isValid(ILaunchConfiguration launchConfig) {
		try {
			String projectName = launchConfig.getAttribute(IRubyLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
			if (projectName.length() == 0) {
				setErrorMessage(RdtDebugUiMessages.LaunchConfigurationTab_RubyEntryPoint_invalidProjectSelectionMessage);
				return false;
			}

			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			if (project == null) {
				setErrorMessage(RdtDebugUiMessages.LaunchConfigurationTab_RubyEntryPoint_invalidProjectSelectionMessage);
				return false;
			}
			if (!project.exists()) {
				setErrorMessage(RdtDebugUiMessages.LaunchConfigurationTab_RubyEntryPoint_invalidProjectSelectionMessage);
				return false;
			}
			
			String fileName = launchConfig.getAttribute(IRubyLaunchConfigurationConstants.ATTR_FILE_NAME, "");
			if (fileName.length() == 0) {
				setErrorMessage(RdtDebugUiMessages.LaunchConfigurationTab_RubyEntryPoint_invalidFileSelectionMessage);
				return false;
			}
			File test = new File(fileName);
			if (!test.exists()) {
				try {
					String workingDirectory = launchConfig.getAttribute(IRubyLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, (String)null);
					if (workingDirectory != null && !workingDirectory.trim().equals("")) {
						String blah = workingDirectory + fileName;
						test = new File(blah);
					}
				} catch (CoreException e) {
					RdtDebugUiPlugin.log(e);
				}
				if (!test.exists()) {
					if (project != null) {
						test = project.getLocation().append(fileName).toFile();
					}
				}
				if (!test.exists()) {
					setErrorMessage(RdtDebugUiMessages.LaunchConfigurationTab_RubyEntryPoint_invalidFileSelectionMessage);
					return false;
				}
			}
			
			setErrorMessage(null);
			return true;
		} catch (CoreException e) {
			setErrorMessage(e.getMessage());
			RdtDebugUiPlugin.log(e);
		}
		return false;
	}

	protected void log(Throwable t) {
		RdtDebugUiPlugin.log(t);
	}

	public boolean canSave() {
		return getErrorMessage() == null;
	}

	public Image getImage() {
		return RubyPluginImages.get(RubyPluginImages.IMG_CTOOLS_RUBY_PAGE);
	}

}
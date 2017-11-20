package org.rubypeople.rdt.internal.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.RubyUIMessages;
import org.rubypeople.rdt.ui.PreferenceConstants;

public class NewProjectCreationWizard extends BasicNewResourceWizard implements INewWizard, IExecutableExtension {
	protected WizardNewProjectCreationPage projectPage;
	protected IConfigurationElement configurationElement;
	protected IProject newProject;
	protected String defaultProjectName = "";
	
	public NewProjectCreationWizard() {
		setDefaultPageImageDescriptor(RubyPluginImages.DESC_WIZBAN_NEWJPRJ);
        setWindowTitle(RubyUIMessages.NewProjectCreationWizard_windowTitle);
	}

	public boolean performFinish() {
		IRunnableWithProgress projectCreationOperation = new WorkspaceModifyDelegatingOperation(getProjectCreationRunnable());

		try {
			getContainer().run(false, true, projectCreationOperation);
		} catch (Exception e) {
			RubyPlugin.log(e);
			return false;
		}

		BasicNewProjectResourceWizard.updatePerspective(configurationElement);
		selectAndReveal(newProject);

		return true;
	}

	protected IRunnableWithProgress getProjectCreationRunnable() {
		return new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				int remainingWorkUnits = 10;
				monitor.beginTask(RubyUIMessages.NewProjectCreationWizard_projectCreationMessage, remainingWorkUnits);

				IWorkspace workspace = RubyPlugin.getWorkspace();
				newProject = projectPage.getProjectHandle();
				
				IProjectDescription description = workspace.newProjectDescription(newProject.getName());
				IPath path = Platform.getLocation();
				IPath customPath = projectPage.getLocationPath();
				if (!path.equals(customPath)) {
					path = customPath;
					description.setLocation(path);
				}

				try {
					if (!newProject.exists()) {
						newProject.create(description, new SubProgressMonitor(monitor, 1));
						remainingWorkUnits--;
					}
					if (!newProject.isOpen()) {
						newProject.open(new SubProgressMonitor(monitor, 1));
						remainingWorkUnits--;
					}
					RubyCore.addRubyNature(newProject, new SubProgressMonitor(monitor, remainingWorkUnits));
					IRubyProject rubyProject = RubyCore.create(newProject);
					configureRubyProject(rubyProject, new SubProgressMonitor(monitor, 6));
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
	}

	protected void configureRubyProject(IRubyProject rubyProject, IProgressMonitor monitor) {
		if (monitor == null) {
			monitor= new NullProgressMonitor();
		}
		ILoadpathEntry[] loadpathEntries = getDefaultLoadpath(rubyProject);
				
		monitor.setTaskName(NewWizardMessages.BuildPathsBlock_operationdesc_java); 
		monitor.beginTask("", loadpathEntries.length * 4 + 4); //$NON-NLS-1$
		try {
			IProject project = rubyProject.getProject();
			IPath projPath= project.getFullPath();			
		
			monitor.worked(1);
			
			IWorkspaceRoot fWorkspaceRoot= RubyPlugin.getWorkspace().getRoot();
			
			monitor.worked(1);
			
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			
//			int nEntries= loadpathEntries.length;		
//			for (int i = 0 ; i < loadpathEntries.length; i++) {
//				ILoadpathEntry entry = loadpathEntries[i];
//				IResource res= entry.getResource();
//				//1 tick
//				if (res instanceof IFolder && entry.getLinkTarget() == null && !res.exists()) {
//					CoreUtility.createFolder((IFolder)res, true, true, new SubProgressMonitor(monitor, 1));
//				} else {
//					monitor.worked(1);
//				}
//				
//				//3 ticks
//				if (entry.getEntryKind() == ILoadpathEntry.CPE_SOURCE) {
//					monitor.worked(1);
//					
//					IPath path= entry.getPath();
//					if (projPath.equals(path)) {
//						monitor.worked(2);
//						continue;	
//					}
//					
//					if (projPath.isPrefixOf(path)) {
//						path= path.removeFirstSegments(projPath.segmentCount());
//					}
//					IFolder folder= project.getFolder(path);
//					IPath orginalPath= entry.getOrginalPath();
//					if (orginalPath == null) {
//						if (!folder.exists()) {
//							//New source folder needs to be created
//							if (entry.getLinkTarget() == null) {
//								CoreUtility.createFolder(folder, true, true, new SubProgressMonitor(monitor, 2));
//							} else {
//								folder.createLink(entry.getLinkTarget(), IResource.ALLOW_MISSING_LOCAL, new SubProgressMonitor(monitor, 2));
//							}
//						}
//					} else {
//						if (projPath.isPrefixOf(orginalPath)) {
//							orginalPath= orginalPath.removeFirstSegments(projPath.segmentCount());
//						}
//						IFolder orginalFolder= project.getFolder(orginalPath);
//						if (entry.getLinkTarget() == null) {
//							if (!folder.exists()) {
//								//Source folder was edited, move to new location
//								IPath parentPath= entry.getPath().removeLastSegments(1);
//								if (projPath.isPrefixOf(parentPath)) {
//									parentPath= parentPath.removeFirstSegments(projPath.segmentCount());
//								}
//								if (parentPath.segmentCount() > 0) {
//									IFolder parentFolder= project.getFolder(parentPath);
//									if (!parentFolder.exists()) {
//										CoreUtility.createFolder(parentFolder, true, true, new SubProgressMonitor(monitor, 1));
//									} else {
//										monitor.worked(1);
//									}
//								} else {
//									monitor.worked(1);
//								}
//								orginalFolder.move(entry.getPath(), true, true, new SubProgressMonitor(monitor, 1));
//							}
//						} else {
//							if (!folder.exists() || !entry.getLinkTarget().equals(entry.getOrginalLinkTarget())) {
//								orginalFolder.delete(true, new SubProgressMonitor(monitor, 1));
//								folder.createLink(entry.getLinkTarget(), IResource.ALLOW_MISSING_LOCAL, new SubProgressMonitor(monitor, 1));
//							}
//						}
//					}
//				} else {
//					monitor.worked(3);
//				}
//				if (monitor.isCanceled()) {
//					throw new OperationCanceledException();
//				}
//			}

			rubyProject.setRawLoadpath(loadpathEntries, new SubProgressMonitor(monitor, 2));
		} catch (RubyModelException e) {
			RubyPlugin.log(e);
		} finally {
			monitor.done();
		}		
	}

	private ILoadpathEntry[] getDefaultLoadpath(IRubyProject rubyProject) {
		ILoadpathEntry[] dflts= PreferenceConstants.getDefaultRubyVMLibrary();
		ILoadpathEntry[] loadpathEntries= new ILoadpathEntry[dflts.length + 1];
		for (int i = 0; i < dflts.length; i ++) {
			loadpathEntries[i] = dflts[i];
		}
		loadpathEntries[dflts.length] = RubyCore.newSourceEntry(rubyProject.getProject().getFullPath());
		return loadpathEntries;
	}

	public void addPages() {
		super.addPages();

		projectPage = new WizardNewProjectCreationPage(RubyUIMessages.WizardNewProjectCreationPage_pageName);
		projectPage.setTitle(RubyUIMessages.WizardNewProjectCreationPage_pageTitle);
		projectPage.setDescription(RubyUIMessages.WizardNewProjectCreationPage_pageDescription);
		projectPage.setInitialProjectName(this.getDefaultProjectName()) ;
		projectPage.setImageDescriptor(RubyPluginImages.DESC_WIZBAN_NEWJPRJ);
        
		addPage(projectPage);
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		configurationElement = config;
	}


	public String getDefaultProjectName() {
		return defaultProjectName;
	}

	public void setDefaultProjectName(String defaultProjectName) {
		this.defaultProjectName = defaultProjectName;
	}
}
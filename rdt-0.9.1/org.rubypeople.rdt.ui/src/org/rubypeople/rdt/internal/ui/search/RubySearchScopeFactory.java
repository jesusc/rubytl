/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.internal.ui.launchConfigurations.WorkingSetComparator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IWorkingSetSelectionDialog;
import org.rubypeople.rdt.core.ILoadpathContainer;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchEngine;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.RubyUI;

public class RubySearchScopeFactory {

	private static RubySearchScopeFactory fgInstance;
	private final IRubySearchScope EMPTY_SCOPE= SearchEngine.createRubySearchScope(new IRubyElement[] {});
	
	private RubySearchScopeFactory() {
	}

	public static RubySearchScopeFactory getInstance() {
		if (fgInstance == null)
			fgInstance= new RubySearchScopeFactory();
		return fgInstance;
	}

	public IWorkingSet[] queryWorkingSets() throws RubyModelException {
		Shell shell= RubyPlugin.getActiveWorkbenchShell();
		if (shell == null)
			return null;
		IWorkingSetSelectionDialog dialog= PlatformUI.getWorkbench().getWorkingSetManager().createWorkingSetSelectionDialog(shell, true);
		if (dialog.open() == Window.OK) {
			IWorkingSet[] workingSets= dialog.getSelection();
			if (workingSets.length > 0)
				return workingSets;
		}
		return null;
	}

	public IRubySearchScope createRubySearchScope(IWorkingSet[] workingSets, boolean includeJRE) {
		if (workingSets == null || workingSets.length < 1)
			return EMPTY_SCOPE;

		Set javaElements= new HashSet(workingSets.length * 10);
		for (int i= 0; i < workingSets.length; i++) {
			IWorkingSet workingSet= workingSets[i];
			if (workingSet.isEmpty() && workingSet.isAggregateWorkingSet()) {
				return createWorkspaceScope(includeJRE);
			}
			addRubyElements(javaElements, workingSet);
		}
		return createRubySearchScope(javaElements, includeJRE);
	}
	
	public IRubySearchScope createRubySearchScope(IWorkingSet workingSet, boolean includeJRE) {
		Set javaElements= new HashSet(10);
		if (workingSet.isEmpty() && workingSet.isAggregateWorkingSet()) {
			return createWorkspaceScope(includeJRE);
		}
		addRubyElements(javaElements, workingSet);
		return createRubySearchScope(javaElements, includeJRE);
	}

	public IRubySearchScope createRubySearchScope(IResource[] resources, boolean includeJRE) {
		if (resources == null)
			return EMPTY_SCOPE;
		Set javaElements= new HashSet(resources.length);
		addRubyElements(javaElements, resources);
		return createRubySearchScope(javaElements, includeJRE);
	}
		
	
	public IRubySearchScope createRubySearchScope(ISelection selection, boolean includeJRE) {
		return createRubySearchScope(getRubyElements(selection), includeJRE);
	}
		
	public IRubySearchScope createRubyProjectSearchScope(String[] projectNames, boolean includeJRE) {
		ArrayList res= new ArrayList();
		IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		for (int i= 0; i < projectNames.length; i++) {
			IRubyProject project= RubyCore.create(root.getProject(projectNames[i]));
			if (project.exists()) {
				res.add(project);
			}
		}
		return createRubySearchScope(res, includeJRE);
	}

	public IRubySearchScope createRubyProjectSearchScope(IRubyProject project, boolean includeJRE) {
		return SearchEngine.createRubySearchScope(new IRubyElement[] { project }, getSearchFlags(includeJRE));
	}
	
	public IRubySearchScope createRubyProjectSearchScope(IEditorInput editorInput, boolean includeJRE) {
		IRubyElement elem= RubyUI.getEditorInputRubyElement(editorInput);
		if (elem != null) {
			IRubyProject project= elem.getRubyProject();
			if (project != null) {
				return createRubyProjectSearchScope(project, includeJRE);
			}
		}
		return EMPTY_SCOPE;
	}
	
	public String getWorkspaceScopeDescription(boolean includeJRE) {
		return includeJRE ? SearchMessages.WorkspaceScope : SearchMessages.WorkspaceScopeNoJRE; 
	}
	
	public String getProjectScopeDescription(String[] projectNames, boolean includeJRE) {
		if (projectNames.length == 0) {
			return SearchMessages.RubySearchScopeFactory_undefined_projects;
		}
		String scopeDescription;
		if (projectNames.length == 1) {
			String label= includeJRE ? SearchMessages.EnclosingProjectScope : SearchMessages.EnclosingProjectScopeNoJRE;
			scopeDescription= Messages.format(label, projectNames[0]);
		} else if (projectNames.length == 2) {
			String label= includeJRE ? SearchMessages.EnclosingProjectsScope2 : SearchMessages.EnclosingProjectsScope2NoJRE;
			scopeDescription= Messages.format(label, new String[] { projectNames[0], projectNames[1]});
		} else {
			String label= includeJRE ? SearchMessages.EnclosingProjectsScope : SearchMessages.EnclosingProjectsScopeNoJRE;
			scopeDescription= Messages.format(label, new String[] { projectNames[0], projectNames[1]});
		}
		return scopeDescription;
	}
	
	public String getProjectScopeDescription(IRubyProject project, boolean includeJRE) {
		if (includeJRE) {
			return Messages.format(SearchMessages.ProjectScope, project.getElementName());
		} else {
			return Messages.format(SearchMessages.ProjectScopeNoJRE, project.getElementName());
		}
	}
	
	public String getProjectScopeDescription(IEditorInput editorInput, boolean includeJRE) {
		IRubyElement elem= RubyUI.getEditorInputRubyElement(editorInput);
		if (elem != null) {
			IRubyProject project= elem.getRubyProject();
			if (project != null) {
				return getProjectScopeDescription(project, includeJRE);
			}
		}
		return Messages.format(SearchMessages.ProjectScope, "");  //$NON-NLS-1$
	}
	
	public String getHierarchyScopeDescription(IType type) {
		return Messages.format(SearchMessages.HierarchyScope, new String[] { type.getElementName() }); 
	}


	public String getSelectionScopeDescription(IRubyElement[] javaElements, boolean includeJRE) {
		if (javaElements.length == 0) {
			return SearchMessages.RubySearchScopeFactory_undefined_selection;
		}
		String scopeDescription;
		if (javaElements.length == 1) {
			String label= includeJRE ? SearchMessages.SingleSelectionScope : SearchMessages.SingleSelectionScopeNoJRE;
			scopeDescription= Messages.format(label, javaElements[0].getElementName());
		} else if (javaElements.length == 1) {
			String label= includeJRE ? SearchMessages.DoubleSelectionScope : SearchMessages.DoubleSelectionScopeNoJRE;
			scopeDescription= Messages.format(label, new String[] { javaElements[0].getElementName(), javaElements[1].getElementName()});
		}  else {
			String label= includeJRE ? SearchMessages.SelectionScope : SearchMessages.SelectionScopeNoJRE;
			scopeDescription= Messages.format(label, new String[] { javaElements[0].getElementName(), javaElements[1].getElementName()});
		}
		return scopeDescription;
	}
	
	public String getWorkingSetScopeDescription(IWorkingSet[] workingSets, boolean includeJRE) {
		if (workingSets.length == 0) {
			return SearchMessages.RubySearchScopeFactory_undefined_workingsets;
		}
		if (workingSets.length == 1) {
			String label= includeJRE ? SearchMessages.SingleWorkingSetScope : SearchMessages.SingleWorkingSetScopeNoJRE;
			return Messages.format(label, workingSets[0].getLabel());
		}
		// jesusc: WorkingSetComparator now compares strings!
		// Arrays.sort(workingSets, new WorkingSetComparator());
		if (workingSets.length == 2) {
			String label= includeJRE ? SearchMessages.DoubleWorkingSetScope : SearchMessages.DoubleWorkingSetScopeNoJRE;
			return Messages.format(label, new String[] { workingSets[0].getLabel(), workingSets[1].getLabel()});
		}
		String label= includeJRE ? SearchMessages.WorkingSetsScope : SearchMessages.WorkingSetsScopeNoJRE;
		return Messages.format(label, new String[] { workingSets[0].getLabel(), workingSets[1].getLabel()});
	}
	
	public IProject[] getProjects(IRubySearchScope scope) {
		IPath[] paths= scope.enclosingProjectsAndJars();
		HashSet temp= new HashSet();
		for (int i= 0; i < paths.length; i++) {
			IResource resource= ResourcesPlugin.getWorkspace().getRoot().findMember(paths[i]);
			if (resource != null && resource.getType() == IResource.PROJECT)
				temp.add(resource);
		}
		return (IProject[]) temp.toArray(new IProject[temp.size()]);
	}

	public IRubyElement[] getRubyElements(ISelection selection) {
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			return getRubyElements(((IStructuredSelection)selection).toArray());
		} else {
			return new IRubyElement[0];
		}
	}

	private IRubyElement[] getRubyElements(Object[] elements) {
		if (elements.length == 0)
			return new IRubyElement[0];
		
		Set result= new HashSet(elements.length);
		for (int i= 0; i < elements.length; i++) {
			Object selectedElement= elements[i];
			if (selectedElement instanceof IRubyElement) {
				addRubyElements(result, (IRubyElement) selectedElement);
			} else if (selectedElement instanceof IResource) {
				addRubyElements(result, (IResource) selectedElement);
			} else if (selectedElement instanceof IWorkingSet) {
				IWorkingSet ws= (IWorkingSet)selectedElement;
				addRubyElements(result, ws);
			} else if (selectedElement instanceof IAdaptable) {
				IResource resource= (IResource) ((IAdaptable) selectedElement).getAdapter(IResource.class);
				if (resource != null)
					addRubyElements(result, resource);
			}
			
		}
		return (IRubyElement[]) result.toArray(new IRubyElement[result.size()]);
	}
	
	public IRubySearchScope createRubySearchScope(IRubyElement[] javaElements, boolean includeJRE) {
		if (javaElements.length == 0)
			return EMPTY_SCOPE;
		return SearchEngine.createRubySearchScope(javaElements, getSearchFlags(includeJRE));
	}

	private IRubySearchScope createRubySearchScope(Collection javaElements, boolean includeJRE) {
		if (javaElements.isEmpty())
			return EMPTY_SCOPE;
		IRubyElement[] elementArray= (IRubyElement[]) javaElements.toArray(new IRubyElement[javaElements.size()]);
		return SearchEngine.createRubySearchScope(elementArray, getSearchFlags(includeJRE));
	}
	
	private static int getSearchFlags(boolean includeJRE) {
		int flags= IRubySearchScope.SOURCES | IRubySearchScope.APPLICATION_LIBRARIES;
		if (includeJRE)
			flags |= IRubySearchScope.SYSTEM_LIBRARIES;
		return flags;
	}

	private void addRubyElements(Set javaElements, IResource[] resources) {
		for (int i= 0; i < resources.length; i++)
			addRubyElements(javaElements, resources[i]);
	}

	private void addRubyElements(Set javaElements, IResource resource) {
		IRubyElement javaElement= (IRubyElement)resource.getAdapter(IRubyElement.class);
		if (javaElement == null)
			// not a Ruby resource
			return;
		
		if (javaElement.getElementType() == IRubyElement.SOURCE_FOLDER) {
			// add other possible source folder
			try {
				addRubyElements(javaElements, ((IFolder)resource).members());
			} catch (CoreException ex) {
				// don't add elements
			}
		}
			
		javaElements.add(javaElement);
	}

	private void addRubyElements(Set javaElements, IRubyElement javaElement) {
		javaElements.add(javaElement);
	}
	
	private void addRubyElements(Set javaElements, IWorkingSet workingSet) {
		if (workingSet == null)
			return;
		
		if (workingSet.isAggregateWorkingSet() && workingSet.isEmpty()) {
			try {
				IRubyProject[] projects= RubyCore.create(ResourcesPlugin.getWorkspace().getRoot()).getRubyProjects();
				javaElements.addAll(Arrays.asList(projects));
			} catch (RubyModelException e) {
				RubyPlugin.log(e);
			}
			return;
		}
		
		IAdaptable[] elements= workingSet.getElements();
		for (int i= 0; i < elements.length; i++) {
			IRubyElement javaElement=(IRubyElement) elements[i].getAdapter(IRubyElement.class);
			if (javaElement != null) { 
				addRubyElements(javaElements, javaElement);
				continue;
			}
			IResource resource= (IResource)elements[i].getAdapter(IResource.class);
			if (resource != null) {
				addRubyElements(javaElements, resource);
			}
			
			// else we don't know what to do with it, ignore.
		}
	}

//	public void addRubyElements(Set javaElements, LogicalPackage selectedElement) {
//		ISourceFolder[] packages= selectedElement.getFragments();
//		for (int i= 0; i < packages.length; i++)
//			addRubyElements(javaElements, packages[i]);
//	}
	
	public IRubySearchScope createWorkspaceScope(boolean includeJRE) {
		if (!includeJRE) {
			try {
				IRubyProject[] projects= RubyCore.create(ResourcesPlugin.getWorkspace().getRoot()).getRubyProjects();
				return SearchEngine.createRubySearchScope(projects, getSearchFlags(includeJRE));
			} catch (RubyModelException e) {
				// ignore, use workspace scope instead
			}
		}
		return SearchEngine.createWorkspaceScope();
	}

	public boolean isInsideJRE(IRubyElement element) {
		ISourceFolderRoot root= (ISourceFolderRoot) element.getAncestor(IRubyElement.SOURCE_FOLDER_ROOT);
		if (root != null) {
			try {
				ILoadpathEntry entry= root.getRawLoadpathEntry();
				if (entry.getEntryKind() == ILoadpathEntry.CPE_CONTAINER) {
					ILoadpathContainer container= RubyCore.getLoadpathContainer(entry.getPath(), root.getRubyProject());
					return container != null && container.getKind() == ILoadpathContainer.K_DEFAULT_SYSTEM;
				}
				return false;
			} catch (RubyModelException e) {
				RubyPlugin.log(e);
			}
		}
		return true; // include JRE in doubt
	}
}

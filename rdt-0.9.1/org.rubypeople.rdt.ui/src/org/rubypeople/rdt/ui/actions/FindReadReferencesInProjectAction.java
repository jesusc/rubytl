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
package org.rubypeople.rdt.ui.actions;

import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.internal.ui.search.RubySearchScopeFactory;
import org.rubypeople.rdt.internal.ui.search.SearchMessages;
import org.rubypeople.rdt.ui.search.ElementQuerySpecification;
import org.rubypeople.rdt.ui.search.QuerySpecification;

/**
 * Finds field read accesses of the selected element in the enclosing project.
 * The action is applicable to selections representing a Ruby field.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 3.0
 */
public class FindReadReferencesInProjectAction extends FindReadReferencesAction {

	/**
	 * Creates a new <code>FindReadReferencesInProjectAction</code>. The action 
	 * requires that the selection provided by the site's selection provider is of type 
	 * <code>IStructuredSelection</code>.
	 * 
	 * @param site the site providing context information for this action
	 */
	public FindReadReferencesInProjectAction(IWorkbenchSite site) {
		super(site);
	}

	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 * @param editor the Ruby editor
	 */
	public FindReadReferencesInProjectAction(RubyEditor editor) {
		super(editor);
	}

	void init() {
		setText(SearchMessages.Search_FindReadReferencesInProjectAction_label); 
		setToolTipText(SearchMessages.Search_FindReadReferencesInProjectAction_tooltip); 
		setImageDescriptor(RubyPluginImages.DESC_OBJS_SEARCH_REF);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.FIND_READ_REFERENCES_IN_PROJECT_ACTION);
	}
	
	QuerySpecification createQuery(IRubyElement element) throws RubyModelException {
		RubySearchScopeFactory factory= RubySearchScopeFactory.getInstance();
		RubyEditor editor= getEditor();
		
		IRubySearchScope scope;
		String description;
		boolean isInsideJRE= factory.isInsideJRE(element);
		if (editor != null) {
			scope= factory.createRubyProjectSearchScope(editor.getEditorInput(), isInsideJRE);
			description= factory.getProjectScopeDescription(editor.getEditorInput(), isInsideJRE);
		} else {
			scope= factory.createRubyProjectSearchScope(element.getRubyProject(), isInsideJRE);
			description=  factory.getProjectScopeDescription(element.getRubyProject(), isInsideJRE);
		}
		return new ElementQuerySpecification(element, getLimitTo(), scope, description);
	}

}

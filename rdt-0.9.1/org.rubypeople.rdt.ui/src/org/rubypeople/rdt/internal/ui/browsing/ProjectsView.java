package org.rubypeople.rdt.internal.ui.browsing;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.part.IShowInTargetList;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.RubyUI;

public class ProjectsView extends RubyBrowsingPart {

	/**
	 * Creates the the content provider of this part.
	 */
	protected IContentProvider createContentProvider() {
		return new ProjectContentProvider(this);
	}

	protected void setInitialInput() {
		IRubyElement root = RubyCore
				.create(RubyPlugin.getWorkspace().getRoot());
		getViewer().setInput(root);
		// TODO Update the title
		// updateTitle();
	}
	
	protected String getLinkToEditorKey() {
		return PreferenceConstants.LINK_BROWSING_PROJECTS_TO_EDITOR;
	}
	
	/**
	 * Answer the property defined by key.
	 */
	public Object getAdapter(Class key) {
		if (key == IShowInTargetList.class) {
			return new IShowInTargetList() {
				public String[] getShowInTargetIds() {
					return new String[] { RubyUI.ID_TYPES_VIEW, IPageLayout.ID_RES_NAV  };
				}

			};
		}
		return super.getAdapter(key);
	}
	
	/**
	 * Adds additional listeners to this view.
	 */
	protected void hookViewerListeners() {
		super.hookViewerListeners();
		getViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				TreeViewer viewer= (TreeViewer)getViewer();
				Object element= ((IStructuredSelection)event.getSelection()).getFirstElement();
				if (viewer.isExpandable(element))
					viewer.setExpandedState(element, !viewer.getExpandedState(element));
			}
		});
	}

	/**
	 * Answers if the given <code>element</code> is a valid input for this
	 * part.
	 * 
	 * @param element
	 *            the object to test
	 * @return <true> if the given element is a valid input
	 */
	protected boolean isValidInput(Object element) {
		return element instanceof IRubyModel;
	}

	/**
	 * Answers if the given <code>element</code> is a valid element for this
	 * part.
	 * 
	 * @param element
	 *            the object to test
	 * @return <true> if the given element is a valid element
	 */
	protected boolean isValidElement(Object element) {
		return element instanceof IRubyProject;
	}
	
	/*
	 * @see RubyBrowsingPart#setInput(Object)
	 */
	protected void setInput(Object input) {
		// Don't allow to clear input for this view
		if (input != null)
			super.setInput(input);
		else
			getViewer().setSelection(null);
	}

	/**
	 * Finds the element which has to be selected in this part.
	 *
	 * @param je	the Ruby element which has the focus
	 */
	protected IRubyElement findElementToSelect(IRubyElement je) {
		if (je == null)
			return null;

		switch (je.getElementType()) {
			case IRubyElement.RUBY_MODEL :
				return null;
			case IRubyElement.RUBY_PROJECT:
				return je;
			default :
				return findElementToSelect(je.getParent());
		}
	}

}

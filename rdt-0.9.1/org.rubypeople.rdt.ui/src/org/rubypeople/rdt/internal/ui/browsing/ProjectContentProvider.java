package org.rubypeople.rdt.internal.ui.browsing;

import java.util.Iterator;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.rubypeople.rdt.core.IRubyProject;

public class ProjectContentProvider extends RubyBrowsingContentProvider
		implements IContentProvider {

	ProjectContentProvider(RubyBrowsingPart browsingPart) {
		super(false, browsingPart);
	}
	
	/*
	 * (non-Javadoc) Method declared on ITreeContentProvider.
	 */
	public Object[] getChildren(Object element) {
		if (!exists(element))
			return NO_CHILDREN;

		try {
			startReadInDisplayThread();
			if (element instanceof IStructuredSelection) {
				Assert.isLegal(false);
				Object[] result = new Object[0];
				Class clazz = null;
				Iterator iter = ((IStructuredSelection) element).iterator();
				while (iter.hasNext()) {
					Object item = iter.next();
					if (clazz == null)
						clazz = item.getClass();
					if (clazz == item.getClass())
						result = concatenate(result, getChildren(item));
					else
						return NO_CHILDREN;
				}
				return result;
			}
			if (element instanceof IStructuredSelection) {
				Assert.isLegal(false);
				Object[] result = new Object[0];
				Iterator iter = ((IStructuredSelection) element).iterator();
				while (iter.hasNext())
					result = concatenate(result, getChildren(iter.next()));
				return result;
			}
			if (element instanceof IRubyProject)
				return NO_CHILDREN;

			return super.getChildren(element);

		//} catch (RubyModelException e) {
		//	return NO_CHILDREN;
		} finally {
			finishedReadInDisplayThread();
		}
	}

	/*
	 * 
	 * @see ITreeContentProvider
	 */
	public boolean hasChildren(Object element) {
		return element instanceof IRubyProject && super.hasChildren(element);
	}

}

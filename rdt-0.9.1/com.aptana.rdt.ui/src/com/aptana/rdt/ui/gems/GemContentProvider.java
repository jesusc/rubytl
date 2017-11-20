package com.aptana.rdt.ui.gems;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class GemContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object inputElement) {
		Object[] res = null;
		if (inputElement instanceof Collection) {
			res = ((Collection) inputElement).toArray();
		}
		
		return res;
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}

}

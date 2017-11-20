package gts.rubytl.ui;

import gts.rubytl.core.resources.AbstractElementVisitor;
import gts.rubytl.core.resources.RakefileVisitor;

import org.eclipse.swt.widgets.Composite;

public class RakefileSelector extends FileSelector {

	public RakefileSelector(Composite parent, ProjectSelector aProjectSelector, String label) {
		super(parent, aProjectSelector, label);
	}

	public AbstractElementVisitor getVisitor() {
	    return new RakefileVisitor();
	}
}
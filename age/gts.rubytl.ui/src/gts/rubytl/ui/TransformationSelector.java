package gts.rubytl.ui;

import gts.rubytl.core.resources.AbstractElementVisitor;
import gts.rubytl.core.resources.TransformationVisitor;

import org.eclipse.swt.widgets.Composite;

public class TransformationSelector extends FileSelector {

	public TransformationSelector(Composite parent, ProjectSelector aProjectSelector, String label) {
		super(parent, aProjectSelector, label);
	}

	public AbstractElementVisitor getVisitor() {
	    return new TransformationVisitor();
	}
}
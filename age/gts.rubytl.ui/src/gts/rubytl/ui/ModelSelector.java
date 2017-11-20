package gts.rubytl.ui;

import gts.rubytl.core.resources.AbstractElementVisitor;
import gts.rubytl.core.resources.ModelVisitor;

import org.eclipse.swt.widgets.Composite;

public class ModelSelector extends FileSelector {

	public ModelSelector(Composite parent, ProjectSelector aProjectSelector, String label) {
		super(parent, aProjectSelector, label);
	}

	public AbstractElementVisitor getVisitor() {
	    return new ModelVisitor();
	}
}
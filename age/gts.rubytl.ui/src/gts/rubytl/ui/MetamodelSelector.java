package gts.rubytl.ui;

import gts.rubytl.core.resources.AbstractElementVisitor;
import gts.rubytl.core.resources.MetamodelVisitor;

import org.eclipse.swt.widgets.Composite;

public class MetamodelSelector extends FileSelector {
	

	public MetamodelSelector(Composite parent, ProjectSelector aProjectSelector, String label) {
		super(parent, aProjectSelector, label);
	}

	public AbstractElementVisitor getVisitor() {
        return new MetamodelVisitor();
	}
}
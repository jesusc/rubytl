package org.rubypeople.rdt.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class DeprecatedView extends ViewPart {

	@Override
	public void createPartControl(Composite parent) {
		Label label = new Label(parent, SWT.NULL);
		label.setText("This view has been replaced by the new Ruby Explorer view.");
	}

	@Override
	public void setFocus() {
	}

}

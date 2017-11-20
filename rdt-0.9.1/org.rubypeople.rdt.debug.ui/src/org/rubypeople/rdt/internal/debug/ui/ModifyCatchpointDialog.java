/*
 * Author: Markus Barchfeld
 * 
 * Copyright (c) 2005 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. RDT is
 * subject to the "Common Public License (CPL) v 1.0". You may not use RDT except in 
 * compliance with the License. For further information see org.rubypeople.rdt/rdt.license.
 */
package org.rubypeople.rdt.internal.debug.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionStatusDialog;


public class ModifyCatchpointDialog extends SelectionStatusDialog {
	private String exception ;
	private Text exceptionTypeField ;
	
	public ModifyCatchpointDialog(Shell parent) {
		super(parent) ;
	}
	
	protected void computeResult() {
		exception = exceptionTypeField.getText() ;
	}
	
    protected Control createDialogArea(Composite parent) {

        Composite dialogArea = (Composite) super.createDialogArea(parent);
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        dialogArea.setLayout(layout);
        dialogArea.setLayoutData(new GridData(GridData.FILL_VERTICAL)); //GridData.FILL_HORIZONTAL

        // A label
        Label label = new Label(dialogArea, SWT.WRAP);
        if (this.getMessage() != null) {
            label.setText(getMessage());
        }
        label.setFont(dialogArea.getFont());
        GridData labelData = new GridData(GridData.FILL_HORIZONTAL);
        labelData.widthHint = 200;
        label.setLayoutData(labelData);
        
        // A text field
        exceptionTypeField = new Text(dialogArea, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        //data.widthHint = 200;
        exceptionTypeField.setLayoutData(data);
        return dialogArea ;
    }
    
    public String getException() {
    	return exception ;
    }

}

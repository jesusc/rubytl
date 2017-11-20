package gts.rubytl.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public abstract class ResourceSelector {
	protected final static String EMPTY_STRING = "";
	protected Composite composite;
	protected Button browseButton;
	protected Text textField;
	protected String browseDialogMessage = EMPTY_STRING;
	protected String browseDialogTitle = EMPTY_STRING;

	public ResourceSelector(Composite parent, String label) {
		composite = new Composite(parent, SWT.NONE);
		GridLayout compositeLayout = new GridLayout();
		compositeLayout.marginLeft = 10;
		compositeLayout.marginRight = 10;
		compositeLayout.marginWidth = 0;
		compositeLayout.marginHeight = 0;
		compositeLayout.numColumns = 3;
		composite.setLayout(compositeLayout);

		new Label(composite, SWT.NONE).setText(label);
		
		textField = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		browseButton = new Button(composite, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowseSelected();
			}
		});
	}

	protected abstract void handleBrowseSelected();
	protected abstract String validateResourceSelection();

	protected Shell getShell() {
		return composite.getShell();
	}

	public void setLayoutData(Object layoutData) {
		composite.setLayoutData(layoutData);
	}

	public void addModifyListener(ModifyListener aListener) {
		textField.addModifyListener(aListener);
	}

	public void setBrowseDialogMessage(String aMessage) {
		browseDialogMessage = aMessage;
	}

	public void setBrowseDialogTitle(String aTitle) {
		browseDialogTitle = aTitle;
	}

	public void setEnabled(boolean enabled) {
		composite.setEnabled(enabled);
		textField.setEnabled(enabled);
		browseButton.setEnabled(enabled);
	}

	public String getSelectionText() {
		return textField.getText();
	}

	public void setSelectionText(String newText) {
		textField.setText(newText);
	}
	
	public Text getTextField() {
		return textField;
	}
	
	/**
	 * By default the text is valid if is a not empty string. Subclasses may override
	 * this method to perform a more sofisticated validation
	 * @return
	 */
	public boolean isValid() {
		return validateResourceSelection().trim().length() > 0;
	}
}

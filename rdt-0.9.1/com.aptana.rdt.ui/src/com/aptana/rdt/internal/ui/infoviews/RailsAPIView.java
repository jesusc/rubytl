package com.aptana.rdt.internal.ui.infoviews;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class RailsAPIView extends ViewPart {

	private static final String URL = "http://api.rubyonrails.org/";
	private Browser browser;

	@Override
	public void createPartControl(Composite parent) {
		 browser = new Browser(parent, SWT.BORDER);
		 browser.setUrl(URL);
	}

	@Override
	public void setFocus() {
		browser.setFocus();
	}

}

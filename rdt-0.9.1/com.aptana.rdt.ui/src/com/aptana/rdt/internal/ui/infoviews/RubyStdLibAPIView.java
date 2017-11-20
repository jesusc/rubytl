package com.aptana.rdt.internal.ui.infoviews;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class RubyStdLibAPIView extends ViewPart {

	private static final String URL = "http://www.ruby-doc.org/stdlib/";
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

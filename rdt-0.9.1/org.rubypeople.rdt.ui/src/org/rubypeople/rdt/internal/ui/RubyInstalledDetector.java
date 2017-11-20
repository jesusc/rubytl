package org.rubypeople.rdt.internal.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.RubyRuntime;

public class RubyInstalledDetector implements IStartup {
	
	private static final String RUBY_BROWSER_ID = RubyPlugin.getPluginId() + ".ruby.download.browser";
	private static final String INTERPRETER_PREF_PAGE_ID = "org.rubypeople.rdt.debug.ui.preferences.PreferencePageRubyInterpreter";
	private static final String RUBY_DOWNLOAD_PAGE = "http://www.ruby-lang.org/en/downloads/";
		
	private static final String SETUP_PREFERENCES = "Setup preferences";
	private static final String CANCEL = "Cancel";
	private static final String DOWNLOAD_RUBY = "Download Ruby";
	
	private static final String MSG = "We were unable to detect a ruby installation. If you do not have ruby installed, please click the \"" + DOWNLOAD_RUBY + "\" button below. If you have ruby installed and we were simply unable to detect it, please add it to your Installed Interpreter preference page. You can begin by clicking the \"" + SETUP_PREFERENCES + "\" button below";
	private static final String TITLE = "Unable to detect Ruby install";

	public void earlyStartup() {
		IVMInstall vm = RubyRuntime.getDefaultVMInstall();
		if (vm != null) return;
		Display.getDefault().asyncExec(new Runnable() {
		
			public void run() {
				MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), TITLE, null, MSG, MessageDialog.WARNING, new String[]{ DOWNLOAD_RUBY, SETUP_PREFERENCES, CANCEL }, 0);
				int result = dialog.open();
				if (result == 0) { // download ruby
					openBrowser(RUBY_DOWNLOAD_PAGE);
				} else if (result == 1) { // send to interpreter pref page
					openPreferencePage(INTERPRETER_PREF_PAGE_ID);
				}
			}

			private void openPreferencePage(String pageId) {
				PreferenceDialog prefDialog = PreferencesUtil.createPreferenceDialogOn(Display.getDefault().getActiveShell(), pageId, null, null);
				prefDialog.setBlockOnOpen(false);
				prefDialog.open();
			}

			private void openBrowser(String url) {
				try {
					IWorkbenchBrowserSupport support =
						  PlatformUI.getWorkbench().getBrowserSupport();
						IWebBrowser browser = support.createBrowser(RUBY_BROWSER_ID);
						browser.openURL(new URL(url));
				} catch (PartInitException e) {
					RubyPlugin.log(e);
				} catch (MalformedURLException e) {
					RubyPlugin.log(e);
				}
			}
		
		});
	}

}

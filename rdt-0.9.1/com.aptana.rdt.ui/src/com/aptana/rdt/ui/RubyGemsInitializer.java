package com.aptana.rdt.ui;

import org.eclipse.ui.IStartup;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMInstallChangedListener;
import org.rubypeople.rdt.launching.PropertyChangeEvent;
import org.rubypeople.rdt.launching.RubyRuntime;

import com.aptana.rdt.AptanaRDTPlugin;

public class RubyGemsInitializer implements IStartup, IVMInstallChangedListener {

	public void earlyStartup() {
		AptanaRDTPlugin.getDefault().getGemManager().initialize();
		RubyRuntime.addVMInstallChangedListener(this); // when default interpreter changes, refresh local gem listing
	}

	public void defaultVMInstallChanged(IVMInstall previous, IVMInstall current) {
		if (current != null) {
			AptanaRDTPlugin.getDefault().getGemManager().refresh();
		}		
	}

	public void vmAdded(IVMInstall newVm) {		
	}

	public void vmChanged(PropertyChangeEvent event) {		
	}

	public void vmRemoved(IVMInstall removedVm) {		
	}

}

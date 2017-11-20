package org.rubypeople.rdt.internal.launching;

import org.eclipse.debug.core.ILaunchManager;
import org.rubypeople.rdt.launching.IVMInstallType;
import org.rubypeople.rdt.launching.IVMRunner;

public class JRubyVM extends StandardVM {
	
	public JRubyVM(IVMInstallType type, String id) {
		super(type, id);
	}

	@Override
	public IVMRunner getVMRunner(String mode) {
		if (ILaunchManager.RUN_MODE.equals(mode)) {
			return new JRubyVMRunner(this);
		}
		return super.getVMRunner(mode);
	}

}

package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy;
import org.rubypeople.rdt.internal.debug.core.model.IRubyDebugTarget;
import org.rubypeople.rdt.internal.debug.core.model.RubyDebugTarget;
import org.rubypeople.rdt.internal.debug.core.model.RubyProcessingException;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMRunner;

public class TestVMDebugger extends StandardVMDebugger implements IVMRunner {

	public TestVMDebugger(IVMInstall vmInstance) {
		super(vmInstance);
	}
	
	@Override
	protected Process exec(String[] cmdLine, File workingDirectory, String[] envp) throws CoreException {
		return new ShamProcess();
	}
	
	@Override
	protected RubyDebuggerProxy getDebugProxy(RubyDebugTarget debugTarget) {
		return new TestDebuggerProxy(debugTarget, false);
	}

	private static class TestDebuggerProxy extends RubyDebuggerProxy {

		public TestDebuggerProxy(IRubyDebugTarget debugTarget, boolean isRubyDebug) {
			super(debugTarget, isRubyDebug);
		}
		
		@Override
		public void start() throws RubyProcessingException, IOException {
			// intentionally empty
		}
		
	}

}

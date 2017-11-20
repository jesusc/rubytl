package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy;
import org.rubypeople.rdt.internal.debug.core.model.RubyDebugTarget;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.VMRunnerConfiguration;

public class RDebugVMDebugger extends StandardVMDebugger {

	private static final String PORT_SWITCH = "--port";
	private static final String VERBOSE_FLAG = "-d";
	private static final String RDEBUG_EXECUTABLE = "rdebug-ide";

	public RDebugVMDebugger(IVMInstall vmInstance) {
		super(vmInstance);
	}

	@Override
	protected List<String> constructProgramString(VMRunnerConfiguration config) throws CoreException {
		String[] args = config.getProgramArguments();
		List<String> argList = new ArrayList<String>();
		argList.add(StandardVMDebugger.END_OF_OPTIONS_DELIMITER);
		for (int i = 0; i < args.length; i++) {
			argList.add(args[i]);
		}
		config.setProgramArguments(argList.toArray(new String[argList.size()]));
		return super.constructProgramString(config);
	}
	
	@Override
	protected List<String> debugSpecificVMArgs(RubyDebugTarget debugTarget) {
		return new ArrayList<String>();
	}
	
	protected List<String> debugArgs(RubyDebugTarget debugTarget) {
		List<String> arguments = new ArrayList<String>();
		arguments.add(findRDebugExecutable(fVMInstance.getInstallLocation()));
		arguments.add(PORT_SWITCH);
		arguments.add(Integer.toString(debugTarget.getPort()));
		if (isDebuggerVerbose()) {
			arguments.add(VERBOSE_FLAG);
		}
		return arguments;
	}
	
	protected RubyDebuggerProxy getDebugProxy(RubyDebugTarget debugTarget) {
		return new RubyDebuggerProxy(debugTarget, true);
	}

	public static String findRDebugExecutable(File vmInstallLocation) {
		String path = vmInstallLocation.getAbsolutePath();
		if (!vmInstallLocation.getName().equals("bin")) {
			path += File.separator + "bin";
		}
		return path + File.separator + RDEBUG_EXECUTABLE;
	}
	
}

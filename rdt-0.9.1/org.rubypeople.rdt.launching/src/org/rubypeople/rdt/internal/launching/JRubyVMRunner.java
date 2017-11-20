package org.rubypeople.rdt.internal.launching;

import java.util.List;

import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMRunner;

public class JRubyVMRunner extends StandardVMRunner implements IVMRunner {

	public JRubyVMRunner(IVMInstall vmInstance) {
		super(vmInstance);
	}
	
	@Override
	protected void addArguments(String[] args, List<String> v, boolean isVMArgs) {
		if (args == null) {
			return;
		}
		boolean isBatch = false;
		if (!v.isEmpty()) {
			String command = v.get(0);
			if (command.equals("sudo")) {
				command = v.get(1);
			}
			if (command.endsWith(".bat")) isBatch = true;
		}
		
		for (int i= 0; i < args.length; i++) {
			if (isVMArgs && isBatch && args[i].charAt(0) == '-') {
				v.add("\"" + args[i] + " " + args[++i] + "\""); // Only do this if we're launching a .bat file, and only for VM args (like -e)
			} else {
				v.add(args[i]);
			}
		}
	}

}

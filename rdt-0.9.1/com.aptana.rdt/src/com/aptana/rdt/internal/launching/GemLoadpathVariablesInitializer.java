package com.aptana.rdt.internal.launching;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.rubypeople.rdt.core.LoadpathVariableInitializer;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.launching.LaunchingPlugin;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.RubyRuntime;

import com.aptana.rdt.AptanaRDTPlugin;
import com.aptana.rdt.core.gems.IGemManager;
import com.aptana.rdt.launching.IGemRuntime;

public class GemLoadpathVariablesInitializer extends LoadpathVariableInitializer {

	private IProgressMonitor fMonitor;

	@Override
	public void initialize(String variable) {
		IVMInstall vmInstall = RubyRuntime.getDefaultVMInstall();
		if (vmInstall != null) {
			IPath newPath = null;
			IGemManager gemManager = AptanaRDTPlugin.getDefault().getGemManager();
			IPath gemPath = gemManager.getGemInstallPath();
			if (gemPath != null) {
				if (variable.equals(IGemRuntime.GEMLIB_VARIABLE)) {
					newPath = gemPath.append("gems");
				}
				if (newPath == null) {
					return;
				}
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IWorkspaceDescription wsDescription = workspace
						.getDescription();
				boolean wasAutobuild = wsDescription.isAutoBuilding();
				try {
					setAutobuild(workspace, false);
					setRubyVMVariable(newPath, variable);
				} catch (CoreException ce) {
					LaunchingPlugin.log(ce);
					return;
				} finally {
					try {
						setAutobuild(workspace, wasAutobuild);
					} catch (CoreException ce) {
						LaunchingPlugin.log(ce);
					}
				}
			}
		}
	}

	private void setRubyVMVariable(IPath newPath, String var)
			throws CoreException {
		RubyCore.setLoadpathVariable(var, newPath, getMonitor());
	}

	private boolean setAutobuild(IWorkspace ws, boolean newState)
			throws CoreException {
		IWorkspaceDescription wsDescription = ws.getDescription();
		boolean oldState = wsDescription.isAutoBuilding();
		if (oldState != newState) {
			wsDescription.setAutoBuilding(newState);
			ws.setDescription(wsDescription);
		}
		return oldState;
	}

	protected IProgressMonitor getMonitor() {
		if (fMonitor == null) {
			return new NullProgressMonitor();
		}
		return fMonitor;
	}

}

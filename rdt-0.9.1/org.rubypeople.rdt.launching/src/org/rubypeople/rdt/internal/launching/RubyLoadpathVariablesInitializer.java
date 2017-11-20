package org.rubypeople.rdt.internal.launching;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.LoadpathVariableInitializer;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.RubyRuntime;

public class RubyLoadpathVariablesInitializer extends LoadpathVariableInitializer {

	private IProgressMonitor fMonitor;

	@Override
	public void initialize(String variable) {
		IVMInstall vmInstall= RubyRuntime.getDefaultVMInstall();
		if (vmInstall != null) {
			IPath newPath= null;
//			IPath[] locations= RubyRuntime.getLibraryLocations(vmInstall);			
			IPath systemLib = new Path(vmInstall.getInstallLocation().getAbsolutePath()).append("lib");
			if (systemLib != null) {
				if (variable.equals(RubyRuntime.RUBYLIB_VARIABLE)) {
					newPath= systemLib;
				} 
				if (newPath == null) {
					return;
				}
				IWorkspace workspace= ResourcesPlugin.getWorkspace();
				IWorkspaceDescription wsDescription= workspace.getDescription();				
				boolean wasAutobuild= wsDescription.isAutoBuilding();
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
	
	private void setRubyVMVariable(IPath newPath, String var) throws CoreException {
		RubyCore.setLoadpathVariable(var, newPath, getMonitor());
	}
	
	private boolean setAutobuild(IWorkspace ws, boolean newState) throws CoreException {
		IWorkspaceDescription wsDescription= ws.getDescription();
		boolean oldState= wsDescription.isAutoBuilding();
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

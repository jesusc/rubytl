package org.rubypeople.rdt.launching;

import java.io.File;

import org.eclipse.core.runtime.IPath;

public interface IVMInstall {

	public File getInstallLocation();

	public void setInstallLocation(File validInstallLocation);

	public String getName();

	public void setName(String newName);

	public IPath[] getLibraryLocations();

	public String getId();

	public IVMInstallType getVMInstallType();

	public void setLibraryLocations(IPath[] paths);

	public String[] getVMArguments();

	public void setVMArguments(String[] vmArgs);
	
	public IVMRunner getVMRunner(String mode);
}
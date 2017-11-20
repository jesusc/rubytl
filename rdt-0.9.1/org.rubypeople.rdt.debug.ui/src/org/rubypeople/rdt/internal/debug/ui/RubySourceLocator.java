package org.rubypeople.rdt.internal.debug.ui;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IPersistableSourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.ui.ISourcePresentation;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.ui.rubyeditor.ExternalRubyFileEditorInput;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;
import org.rubypeople.rdt.ui.RubyUI;

/**
 * @author Administrator
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of
 * type comments go to Window>Preferences>Java>Code Generation.
 */
public class RubySourceLocator implements IPersistableSourceLocator, ISourcePresentation {
	
	private String absoluteWorkingDirectory;
	private String projectName;

	public RubySourceLocator() {

	}

	public String getAbsoluteWorkingDirectory() {
		return absoluteWorkingDirectory;
	}
	/**
	 * @see org.eclipse.debug.core.model.IPersistableSourceLocator#getMemento()
	 */
	public String getMemento() throws CoreException {
		return null;
	}

	/**
	 * @see org.eclipse.debug.core.model.IPersistableSourceLocator#initializeFromMemento(String)
	 */
	public void initializeFromMemento(String memento) throws CoreException {
	}

	/**
	 * @see org.eclipse.debug.core.model.IPersistableSourceLocator#initializeDefaults(ILaunchConfiguration)
	 */
	public void initializeDefaults(ILaunchConfiguration configuration) throws CoreException {
		this.absoluteWorkingDirectory = configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, ""); //$NON-NLS-1$
		this.projectName = configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_PROJECT_NAME, ""); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.debug.core.model.ISourceLocator#getSourceElement(IStackFrame)
	 */
	public Object getSourceElement(IStackFrame stackFrame) {
		return this.getSourceElement( ((RubyStackFrame) stackFrame).getFileName());
	}
	
	public Object getSourceElement(String pFilename) {
		return new SourceElement(pFilename, this);
	}
	/**
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorId(IEditorInput,
	 *          Object)
	 */
	public String getEditorId(IEditorInput input, Object element) {
		SourceElement sourceElement = (SourceElement) element ;
		return sourceElement.isExternal() ? RubyUI.ID_EXTERNAL_EDITOR : RubyUI.ID_RUBY_EDITOR;
	}

	/**
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorInput(Object)
	 */
	public IEditorInput getEditorInput(Object element) {
		SourceElement sourceElement = (SourceElement) element ;
		if (!sourceElement.isExternal()) {
			return new FileEditorInput(sourceElement.getWorkspaceFile());
		}
		File filesystemFile = new File(sourceElement.getFilename());
		if (filesystemFile.exists()) {
			return new ExternalRubyFileEditorInput(filesystemFile);
		}
		
		// If the file is relative to the working directory, ruby returns a relative path
		filesystemFile = new File(this.absoluteWorkingDirectory + java.io.File.separator + sourceElement.getFilename()) ;
		if (filesystemFile.exists()) {
			return new ExternalRubyFileEditorInput(filesystemFile);
		}
		
		RdtDebugCorePlugin.log(IStatus.INFO, RdtDebugUiMessages.getFormattedString(RdtDebugUiMessages.RdtDebugUiPlugin_couldNotOpenFile, sourceElement.getFilename()));
		return null;
		
	}

	public class SourceElement {
		private String filename;
		private IFile workspaceFile;
		public SourceElement(String aFilename, RubySourceLocator pSourceLocator) {
			filename = aFilename;
			if (filename == null) return;
			if (filename.startsWith("./") && pSourceLocator.projectName != null && pSourceLocator.projectName.trim().length() > 0) {
				filename = "/" + pSourceLocator.projectName + filename.substring(1);
			}
			workspaceFile = RdtDebugCorePlugin.getWorkspace().getRoot().getFileForLocation(new Path(filename));
			if (workspaceFile == null) {
				try {
					workspaceFile = RdtDebugCorePlugin.getWorkspace().getRoot().getFile(new Path(filename));
				} catch (RuntimeException e) {
					workspaceFile = null;
				}
				if (workspaceFile != null && !workspaceFile.exists()) {
					workspaceFile = null;
				}
			}
			
			if (workspaceFile == null) {
				// using slash here is platform independent
				workspaceFile = RdtDebugCorePlugin.getWorkspace().getRoot().getFileForLocation(new Path(pSourceLocator.getAbsoluteWorkingDirectory() + "/" + filename)); //$NON-NLS-1$
				if (workspaceFile != null && !workspaceFile.exists()) {
					workspaceFile = null ;
				}
			}
		}

		public boolean isExternal() {
			return workspaceFile == null;
		}
		public IFile getWorkspaceFile() {
			return workspaceFile;
		}

		public String getFilename() {
			return filename;
		}

	}
}

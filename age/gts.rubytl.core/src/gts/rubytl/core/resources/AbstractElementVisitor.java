package gts.rubytl.core.resources;

import gts.eclipse.core.resources.ResourceMatcher;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

public abstract class AbstractElementVisitor implements IResourceVisitor {

	private ResourceMatcher fileMatcher;

	private ArrayList files;

	public AbstractElementVisitor() {
		fileMatcher = getMatcher();
		files = new ArrayList();
	}

	public boolean visit(IResource resource) throws CoreException {
		switch (resource.getType()) {
		case IResource.PROJECT:
			return true;
		case IResource.FOLDER:
			return true;
		case IResource.FILE:
			IFile fileResource = (IFile) resource;
			if (fileMatcher.match(fileResource)) {
				this.files.add(resource);
				return true;
			}
			return false;

		default:
			return false;
		}
	}

	public Object[] getCollectedFiles() {
		return files.toArray();
	}
	
	public abstract ResourceMatcher getMatcher();
}

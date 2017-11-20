package gts.eclipse.core.resources;

import java.io.File;

import org.eclipse.core.resources.IFile;

public class ResourceMatcher {

	private String[] extensions;

	public ResourceMatcher() {
		this(new String[] { });
	}
	
	public ResourceMatcher(String extension) {
		this(new String[] { extension });
	}
	
	public ResourceMatcher(String[] extensions) {
		this.extensions = extensions;
	}
	
	
	public boolean match(IFile fileResource) {
		for(int i = 0; i < extensions.length; i++) {
			if ( fileResource.getFileExtension() != null && fileResource.getFileExtension().equals(extensions[i]) ) {
				return true;
			}
		}
		return false;
	}

	
	public boolean match(File fileResource) {
		for(int i = 0; i < extensions.length; i++) {
			if ( fileResource.getAbsolutePath().endsWith(extensions[i]) ) {
				return true;
			}
		}
		return false;
	}
	

}

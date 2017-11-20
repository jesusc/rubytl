package gts.eclipse.core.resources;

import java.io.File;

import org.eclipse.core.resources.IFile;

public class NamedResourceMatcher extends ResourceMatcher{

	private String name;


	public NamedResourceMatcher(String name) {
		this.name = name;
	}
	
	public boolean match(IFile fileResource) {
		if ( fileResource.getName().equals(name) ) return true;
		return false;
	}

	
	public boolean match(File fileResource) {
		if ( fileResource.getName().equals(name)) return true;
		return false;
	}
	

}

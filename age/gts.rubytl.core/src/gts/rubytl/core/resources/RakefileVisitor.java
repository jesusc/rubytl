package gts.rubytl.core.resources;

import gts.eclipse.core.resources.ResourceMatcher;

public class RakefileVisitor extends AbstractElementVisitor {

	public ResourceMatcher getMatcher() {
		return new ResourceMatcher(new String[] { "rake", "rb" }); 
	}

}

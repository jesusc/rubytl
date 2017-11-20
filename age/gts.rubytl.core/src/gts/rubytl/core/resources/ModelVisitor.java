package gts.rubytl.core.resources;

import gts.eclipse.core.resources.ResourceMatcher;

public class ModelVisitor extends AbstractElementVisitor {

	public ResourceMatcher getMatcher() {
		return new ResourceMatcher(new String[] { "xmi", "rb" }); 
	}

}

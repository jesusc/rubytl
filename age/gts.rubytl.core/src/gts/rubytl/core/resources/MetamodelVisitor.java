package gts.rubytl.core.resources;

import gts.eclipse.core.resources.ResourceMatcher;

public class MetamodelVisitor extends AbstractElementVisitor {

	public ResourceMatcher getMatcher() {
		return new ResourceMatcher(new String[] { "xmi", "rb", "emof" }); 
	}

}

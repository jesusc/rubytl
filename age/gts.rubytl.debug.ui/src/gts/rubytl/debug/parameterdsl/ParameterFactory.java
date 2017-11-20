package gts.rubytl.debug.parameterdsl;

import gts.age.jruby.integration.IntegrationFactory;

public class ParameterFactory implements IntegrationFactory {

	public ChecklistType newChecklistType() {
		return new ChecklistType();
	}
	
	public IdListFormater newIdListFormater() {
		return new IdListFormater();
	}
	
}

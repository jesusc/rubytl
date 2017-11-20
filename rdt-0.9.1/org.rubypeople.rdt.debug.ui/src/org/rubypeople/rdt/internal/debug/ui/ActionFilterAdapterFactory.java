package org.rubypeople.rdt.internal.debug.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IActionFilter;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;

class ActionFilterAdapterFactory implements IAdapterFactory, IActionFilter {

	public Object getAdapter(Object obj, Class adapterType) {
		if (adapterType.isInstance(obj)) {
			return obj;
		}
		if (adapterType != IActionFilter.class) {
			return null ;
		}
		
		if (obj instanceof RubyVariable) {
				return this;
		}
		return null;
	}

	public Class[] getAdapterList() {
		return new Class[] {
			IActionFilter.class 
		};
	}

    public boolean testAttribute(Object rubyVariable, String name, String value) {
    	if (name.equals("isHashValue")) {    	
        	return ((RubyVariable) rubyVariable).isHashValue() ;
    	}
		return false;       
    }

}



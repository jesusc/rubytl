package org.rubypeople.rdt.internal.compiler;

import java.util.HashMap;
import java.util.Map;

import org.rubypeople.rdt.core.RubyCore;

public class CompilerOptions {

	public static final long EmptyStatement = 0x01;
	public static final long ConstantReassignment = 0x02;
	
	public static final String ERROR = RubyCore.ERROR; //$NON-NLS-1$
	public static final String WARNING = RubyCore.WARNING; //$NON-NLS-1$
	public static final String IGNORE = RubyCore.IGNORE; //$NON-NLS-1$
	
//	 Default severity level for handlers
	public long errorThreshold = 0;
	public long warningThreshold = 
		ConstantReassignment;
	
	public Map getMap() {
		Map optionsMap = new HashMap(30);
		optionsMap.put(RubyCore.COMPILER_PB_EMPTY_STATEMENT, getSeverityString(EmptyStatement));
		optionsMap.put(RubyCore.COMPILER_PB_CONSTANT_REASSIGNMENT, getSeverityString(ConstantReassignment));
		return optionsMap;
	}
	
	public String getSeverityString(long irritant) {
		if((this.warningThreshold & irritant) != 0)
			return WARNING;
		if((this.errorThreshold & irritant) != 0)
			return ERROR;
		return IGNORE;
	}
	
	public void set(Map optionsMap) {
		Object optionValue;
		if ((optionValue = optionsMap.get(RubyCore.COMPILER_PB_EMPTY_STATEMENT)) != null) updateSeverity(EmptyStatement, optionValue);
		if ((optionValue = optionsMap.get(RubyCore.COMPILER_PB_CONSTANT_REASSIGNMENT)) != null) updateSeverity(ConstantReassignment, optionValue);
	}
	
	void updateSeverity(long irritant, Object severityString) {
		if (ERROR.equals(severityString)) {
			this.errorThreshold |= irritant;
			this.warningThreshold &= ~irritant;
		} else if (WARNING.equals(severityString)) {
			this.errorThreshold &= ~irritant;
			this.warningThreshold |= irritant;
		} else if (IGNORE.equals(severityString)) {
			this.errorThreshold &= ~irritant;
			this.warningThreshold &= ~irritant;
		}
	}	

}

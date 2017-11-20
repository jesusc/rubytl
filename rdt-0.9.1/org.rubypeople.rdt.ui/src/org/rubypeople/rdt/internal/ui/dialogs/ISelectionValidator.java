package org.rubypeople.rdt.internal.ui.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.rubypeople.rdt.internal.ui.JDTOriginally;

public interface ISelectionValidator extends JDTOriginally {

	IStatus validate(Object[] selection);

}
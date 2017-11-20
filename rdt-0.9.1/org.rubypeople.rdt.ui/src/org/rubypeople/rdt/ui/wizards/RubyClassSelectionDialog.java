package org.rubypeople.rdt.ui.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.internal.ui.dialogs.TypeSelectionDialog2;

/**
 * 
 * @author Chris Williams
 * @deprecated Please use {@link TypeSelectionDialog2}
 */
public class RubyClassSelectionDialog extends RubyTypeSelectionDialog {
//	 XXX Remve all references to this and remove it and its parent!
	
    public RubyClassSelectionDialog(Shell parent) {
        super(parent);
    }

    protected Object[] getElements() {
        Object[] types = super.getElements();
        List classes = new ArrayList();
        for (int i = 0; i < types.length; i++) {
            IType type = (IType) types[i];
            if (type.isClass()) classes.add(types[i]);
        }
        IRubyElement[] elements = new IRubyElement[classes.size()];
        System.arraycopy(classes.toArray(), 0, elements, 0, elements.length);
        return elements;
    }

}

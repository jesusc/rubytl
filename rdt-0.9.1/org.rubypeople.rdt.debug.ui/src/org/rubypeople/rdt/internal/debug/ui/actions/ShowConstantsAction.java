package org.rubypeople.rdt.internal.debug.ui.actions;

import org.eclipse.jface.viewers.Viewer;
import org.rubypeople.rdt.debug.ui.RdtDebugUiConstants;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;

public class ShowConstantsAction extends VariableFilterAction {

  protected String getPreferenceKey() {
    return RdtDebugUiConstants.SHOW_CONSTANTS_PREFERENCE;
  }

  public boolean select(Viewer viewer, Object parentElement, Object element) {
    if (element instanceof RubyVariable) {
      RubyVariable variable = (RubyVariable) element;
      if (!getValue()) {
        // when not on, filter constants
        return !variable.isConstant();
      }
    }
    return true;
  }
}

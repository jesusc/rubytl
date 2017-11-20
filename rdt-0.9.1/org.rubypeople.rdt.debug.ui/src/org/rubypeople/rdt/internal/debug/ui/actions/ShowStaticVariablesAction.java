package org.rubypeople.rdt.internal.debug.ui.actions;

import org.eclipse.jface.viewers.Viewer;
import org.rubypeople.rdt.debug.ui.RdtDebugUiConstants;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;

public class ShowStaticVariablesAction extends VariableFilterAction {

  protected String getPreferenceKey() {
    return RdtDebugUiConstants.SHOW_STATIC_VARIABLES_PREFERENCE;
  }

  public boolean select(Viewer viewer, Object parentElement, Object element) {
    if (element instanceof RubyVariable) {
      RubyVariable variable = (RubyVariable) element;
      if (!getValue()) {
        // when not on, filter non-static finals
        return !variable.isStatic();
      }
    }
    return true;
  }
}

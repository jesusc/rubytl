package gts.age.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

   public void initializeDefaultPreferences() {
      IPreferenceStore store = PreferencesPlugin.getDefault().getPreferenceStore();
      store.setDefault(PreferenceConstants.PATH_OPTIONS_USE_ALTERNATIVE_PATH, "");
      store.setDefault(PreferenceConstants.PATH_OPTIONS_ALTERNATIVE_PATH, "");
   }
}

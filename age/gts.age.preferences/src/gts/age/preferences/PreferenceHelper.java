package gts.age.preferences;

/**
 * TODO: Autogenerate => The problem is with types
 * 
 * @author jesus
 *
 */
public class PreferenceHelper {

	public static boolean useAlternativePath() {
		return PreferencesPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.PATH_OPTIONS_USE_ALTERNATIVE_PATH);
	}
	
	public static String alternativePath() {
		return PreferencesPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.PATH_OPTIONS_ALTERNATIVE_PATH);		
	}

}

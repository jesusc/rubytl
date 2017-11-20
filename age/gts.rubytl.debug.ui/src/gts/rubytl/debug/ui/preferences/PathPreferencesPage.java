package gts.rubytl.debug.ui.preferences;

import gts.rubytl.debug.ui.DebugUIPlugin;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.PathEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class PathPreferencesPage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public PathPreferencesPage() {
		super(GRID);
		setPreferenceStore(DebugUIPlugin.getDefault().getPreferenceStore());
		setDescription("Path preferences");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		PathEditor pathEditor = new PathEditor(PreferenceConstants.PLUGIN_PATH, 
									"&Plugin paths", "", getFieldEditorParent());
		//addField(new DirectoryFieldEditor(PreferenceConstants.PLUGIN_PATH, 
//					"&Plugin path:", getFieldEditorParent()));
		addField(pathEditor);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}
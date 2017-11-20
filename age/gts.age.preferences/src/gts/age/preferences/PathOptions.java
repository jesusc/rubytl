package gts.age.preferences;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class PathOptions extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

   BooleanFieldEditor useAlternativePath;
   DirectoryFieldEditor alternativePath;

   public PathOptions() {
      super(GRID);
      setPreferenceStore(PreferencesPlugin.getDefault().getPreferenceStore());
      setDescription("Path options");
   }

   public void createFieldEditors() {
      this.useAlternativePath = new BooleanFieldEditor(PreferenceConstants.PATH_OPTIONS_USE_ALTERNATIVE_PATH, "&Use alternative path", getFieldEditorParent());
      addField(useAlternativePath);

      this.alternativePath = new DirectoryFieldEditor(PreferenceConstants.PATH_OPTIONS_ALTERNATIVE_PATH, "&Alternative path", getFieldEditorParent());
      addField(alternativePath);
   }

   public void init(IWorkbench workbench) { }


}

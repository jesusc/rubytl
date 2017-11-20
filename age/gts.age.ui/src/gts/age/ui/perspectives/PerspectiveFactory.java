package gts.age.ui.perspectives;

import gts.age.ui.views.MddResourcesView;
import gts.age.ui.wizards.NewMetamodelWizard;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveFactory implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		// Get the editor area.
		String editorArea = layout.getEditorArea();
		 
		// Top left: Resource Navigator view and Bookmarks view placeholder
		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, editorArea);
		topLeft.addView(MddResourcesView.ID);
		topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);
 
		// Bottom left: Outline view and Property Sheet view
		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.50f, "topLeft");
		bottomLeft.addView(IPageLayout.ID_OUTLINE);
  
		// Bottom right: Task List view
		IFolderLayout bottomRight = layout.createFolder("bottomRight", IPageLayout.BOTTOM, 0.66f, editorArea);
		bottomRight.addView(IPageLayout.ID_PROP_SHEET);
		bottomRight.addView(IPageLayout.ID_TASK_LIST);

  
		// new actions - Create shortcuts
		layout.addNewWizardShortcut("gts.rubytl.ui.wizards.NewRubyTLProjectCreationWizard"); 
		layout.addNewWizardShortcut(NewMetamodelWizard.ID);
		layout.addNewWizardShortcut("gts.rubytl.ui.wizards.NewTransformationWizard");
		layout.addNewWizardShortcut("gts.rubytl.ui.wizards.NewModelToCodeWizard");		
		layout.addNewWizardShortcut("gts.rubytl.ui.wizards.NewRubySourceModelWizard");
		layout.addNewWizardShortcut("gts.rubytl.ui.wizards.NewCodeTemplateWizard");		
		
		// Action Sets
		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);

		// layout.addActionSet(IRubyActionSetIds.RUBY_ACTION_SET_ID);
		// layout.addActionSet(IRubyActionSetIds.ID_ELEMENT_CREATION_ACTION_SET);

	}

}

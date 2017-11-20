package gts.rubytl.debug.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

import gts.age.jruby.integration.JRubyEvaluator;
import gts.rubytl.launching.ILauncherConstants;
import gts.rubytl.ui.MetamodelSelector;
import gts.rubytl.ui.ProjectSelector;
import gts.rubytl.ui.ResourceSelector;
import gts.rubytl.ui.TransformationSelector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class MetamodelsTab extends AbstractLaunchConfigurationTab {
	private Text sourcePackage;
	private Text targetPackage;
	private Text transformationModule;
	private Font parentFont; 
	
	private ProjectSelector projectSelector; 
	private MetamodelSelector sourceMetamodelSelector;
	private MetamodelSelector sourceModelSelector;
	private MetamodelSelector targetModelSelector;
	private MetamodelSelector targetMetamodelSelector;
	private TransformationSelector transformationSelector;
	
	public void createControl(Composite parent) {	
		parentFont = parent.getFont();
		Composite comp = createPageRoot(parent);
               
	    String label = DebugUIPlugin.MESSAGES.getString("LaunchConfigurationTab.projectLabel"); 
        projectSelector = new ProjectSelector(comp, label);
        setSelectorData(projectSelector, "LaunchConfigurationTab.projectSelectorMessage");
	      
        // Source metamodel & package
        label = DebugUIPlugin.MESSAGES.getString("LaunchConfigurationTab.sourceMetamodelLabel"); 
	    sourceMetamodelSelector = new MetamodelSelector(comp, projectSelector, label);
	    setSelectorData(sourceMetamodelSelector, "LaunchConfigurationTab.sourceMetamodelSelectorMessage");
	    sourcePackage = createPackageField(comp, "Source package :");       

	    // Target metamodel & package
        label = DebugUIPlugin.MESSAGES.getString("LaunchConfigurationTab.targetMetamodelLabel"); 
	    targetMetamodelSelector = new MetamodelSelector(comp, projectSelector, label);
	    setSelectorData(targetMetamodelSelector, "LaunchConfigurationTab.targetMetamodelSelectorMessage");        
	    targetPackage = createPackageField(comp, "Source package :");
        
        // Source model
        label = DebugUIPlugin.MESSAGES.getString("LaunchConfigurationTab.sourceModelLabel"); 
	    sourceModelSelector = new MetamodelSelector(comp, projectSelector, label);
	    setSelectorData(sourceModelSelector, "LaunchConfigurationTab.sourceModelSelectorMessage");        
        
	    // Target model
        label = DebugUIPlugin.MESSAGES.getString("LaunchConfigurationTab.targetModelLabel"); 
	    targetModelSelector = new MetamodelSelector(comp, projectSelector, label);
	    targetModelSelector.mustExists = false;
	    setSelectorData(targetModelSelector, "LaunchConfigurationTab.targetModelSelectorMessage");        

	    // Transformation
	    label = DebugUIPlugin.MESSAGES.getString("LaunchConfigurationTab.transformationLabel"); 
	    transformationSelector = new TransformationSelector(comp, projectSelector, label);
	    setSelectorData(transformationSelector, "LaunchConfigurationTab.transformationSelectorMessage");        

	    transformationModule = createPackageField(comp, "Transformation module :");
	}
	

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// De momento nada
		// TODO: Buscar heur�sticas
		//		- Nombre de ficheros xmi
		//		- Buscar paquetes candidatos
		//		- Nombre por defecto para el resultado
	}

	public void initializeFrom(ILaunchConfiguration configuration) {		
		initializeTextValue(configuration, ILauncherConstants.PROJECT, projectSelector.getTextField());
		initializeTextValue(configuration, ILauncherConstants.SOURCE_METAMODEL, sourceMetamodelSelector.getTextField());
		initializeTextValue(configuration, ILauncherConstants.TARGET_METAMODEL, targetMetamodelSelector.getTextField());
		initializeTextValue(configuration, ILauncherConstants.SOURCE_PACKAGE, sourcePackage);
		initializeTextValue(configuration, ILauncherConstants.TARGET_PACKAGE, targetPackage);
		initializeTextValue(configuration, ILauncherConstants.SOURCE_MODEL, sourceModelSelector.getTextField());
		initializeTextValue(configuration, ILauncherConstants.TARGET_MODEL, targetModelSelector.getTextField());		
		initializeTextValue(configuration, ILauncherConstants.TRANSFORMATION, transformationSelector.getTextField());
		initializeTextValue(configuration, ILauncherConstants.TRANSFORMATION_MODULE, transformationModule);
	}
	

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(ILauncherConstants.PROJECT, projectSelector.getSelectionText());
		configuration.setAttribute(ILauncherConstants.SOURCE_METAMODEL, sourceMetamodelSelector.getSelectionText());
		configuration.setAttribute(ILauncherConstants.TARGET_METAMODEL, targetMetamodelSelector.getSelectionText());		
		configuration.setAttribute(ILauncherConstants.SOURCE_PACKAGE, sourcePackage.getText());
		configuration.setAttribute(ILauncherConstants.TARGET_PACKAGE, targetPackage.getText());		
		configuration.setAttribute(ILauncherConstants.SOURCE_MODEL, sourceModelSelector.getSelectionText());
		configuration.setAttribute(ILauncherConstants.TARGET_MODEL, targetModelSelector.getSelectionText());		
		configuration.setAttribute(ILauncherConstants.TRANSFORMATION, transformationSelector.getSelectionText());
		configuration.setAttribute(ILauncherConstants.TRANSFORMATION_MODULE, transformationModule.getText());
	}

	/**
	 * Devuelve true si los datos para lanzar la configuraciÓn son suficientes.
	 * @return true si todos los campos tienen valores no nulos.
	 */
	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage(null);

		if ( ! transformationSelector.isValid() ) setErrorMessage("Transformation is required");
		if ( ! targetModelSelector.isValid() ) setErrorMessage("Target model is required");
		if ( ! sourceModelSelector.isValid() ) setErrorMessage("Source model is required");
		if ( targetPackage.getText().trim().length() == 0 ) setErrorMessage("Target package is required");
		if ( sourcePackage.getText().trim().length() == 0 ) setErrorMessage("Source package is required");
		if ( transformationModule.getText().trim().length() == 0 ) setErrorMessage("Transformation module is required");
		if ( ! targetMetamodelSelector.isValid() ) setErrorMessage("Target Metamodel is required");
		if ( ! sourceMetamodelSelector.isValid() ) setErrorMessage("Source Metamodel is required");			
		if ( ! projectSelector.isValid() ) setErrorMessage("Project is required");			
		
		return getErrorMessage() == null;
	}
	
	/**
	 * Siempre devuelve true, y se asume que se puede guardar cualquier dato
	 * de la configuración.
	 */
	public boolean canSave() {
		setErrorMessage(null);
		return true;
	}
	
	public String getName() {
		return "Input/Output models";
	}

	
	public Text createPackageField(Composite parent, String label) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout compositeLayout = new GridLayout();
		compositeLayout.marginLeft = 10;
		compositeLayout.marginRight = 10;
		compositeLayout.marginWidth = 0;
		compositeLayout.marginHeight = 0;
		compositeLayout.numColumns = 2;
		composite.setLayout(compositeLayout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(composite, SWT.NONE).setText(label);
		
		Text text = new Text(composite, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent evt) {
                    updateLaunchConfigurationDialog();
            }
        });		
		return text;
	}

	
	/**
	 * Create some empty space 
	 */
	private void createVerticalSpacer(Composite comp) {
		new Label(comp, SWT.NONE);
	}


    protected Composite createPageRoot(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        composite.setLayout(layout);

        setControl(composite);
        return composite;
    }

    // TODO: FACTORIZAR
	private void initializeTextValue(ILaunchConfiguration configuration, String constant, Text text) {
		try {
			String value = configuration.getAttribute(constant, "");
			text.setText(value);
		} catch (CoreException e) { }		
	}
	
	private class TabListener extends SelectionAdapter implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			updateLaunchConfigurationDialog();
		}		
	}

	private void setSelectorData(ResourceSelector selector, String browseDialogMessage) {
        selector.setBrowseDialogMessage(DebugUIPlugin.MESSAGES.getString(browseDialogMessage));
        selector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        selector.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent evt) {
                        updateLaunchConfigurationDialog();
                }
        });		
	}
	
}

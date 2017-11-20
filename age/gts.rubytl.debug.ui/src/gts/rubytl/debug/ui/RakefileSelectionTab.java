package gts.rubytl.debug.ui;

import gts.rubytl.launching.ILauncherConstants;
import gts.rubytl.ui.MetamodelSelector;
import gts.rubytl.ui.ProjectSelector;
import gts.rubytl.ui.RakefileSelector;
import gts.rubytl.ui.ResourceSelector;
import gts.rubytl.ui.TransformationSelector;

import org.eclipse.core.runtime.CoreException;
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

public class RakefileSelectionTab extends AbstractLaunchConfigurationTab {
	
	private Text rakeTask;
	private ProjectSelector projectSelector; 
	private RakefileSelector rakefileSelector;
	
	
	public void createControl(Composite parent) {
		Composite comp = createPageRoot(parent);
               
	    String label = DebugUIPlugin.MESSAGES.getString("LaunchConfigurationTab.projectLabel"); 
        projectSelector = new ProjectSelector(comp, label);
        setSelectorData(projectSelector, "LaunchConfigurationTab.projectSelectorMessage");
	      
        // Source metamodel & package
        label = DebugUIPlugin.MESSAGES.getString("RakefileSelectionTab.rakefileLabel"); 
	    rakefileSelector = new RakefileSelector(comp, projectSelector, label);
	    setSelectorData(rakefileSelector, "RakefileSelectionTab.rakefileSelectorMessage");
	    
	    rakeTask = createPackageField(comp, "Task to execute :"); 
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
		initializeTextValue(configuration, ILauncherConstants.RAKEFILE, rakefileSelector.getTextField());
		initializeTextValue(configuration, ILauncherConstants.RAKE_TASK, rakeTask);
	}
	

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(ILauncherConstants.PROJECT, projectSelector.getSelectionText());
		configuration.setAttribute(ILauncherConstants.RAKEFILE, rakefileSelector.getSelectionText());
		configuration.setAttribute(ILauncherConstants.RAKE_TASK, rakeTask.getText());
	}

	/**
	 * Devuelve true si los datos para lanzar la configuraciÓn son suficientes.
	 * @return true si todos los campos tienen valores no nulos.
	 */
	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage(null);

		if ( ! rakefileSelector.isValid() ) setErrorMessage("A rakefile is required");
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
		return "Rakefile";
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

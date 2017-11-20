package gts.rubytl.debug.ui;

import gts.rubytl.core.RubytlCore;
import gts.rubytl.core.resources.RubytlPlugin;
import gts.rubytl.debug.parameterdsl.ParameterDslLoader;
import gts.rubytl.debug.ui.preferences.PreferenceConstants;
import gts.rubytl.launching.ILauncherConstants;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This tab page allows a user to enable and disable plugin and
 * to set global plugin options.
 * Whenever a new plugin is enabled specific plugin options can
 * set into a PluginParametersTab. 
 * 
 * @author jesus
 */
public class PluginsTab extends AbstractLaunchConfigurationTab {
	protected CheckboxTableViewer viewer;
	protected Composite parent;
	protected PluginParametersTab pluginParametersTab;
	
	/**
	 * Create a new tab page where the user can enable or disable
	 * RubyTL plugins. 
	 * 
	 * @param pluginParametersTab The tab page where specific plugin parameters can be set.
	 */
	public PluginsTab(PluginParametersTab pluginParametersTab) {
		this.pluginParametersTab = pluginParametersTab;
	}
	
	public void createControl(Composite parent) {
		this.parent = parent;
		
		Composite root = createPageRoot(parent);
		Table table = createPluginsTable(root);
		viewer = createPluginsTableViewer(table);				
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub
		
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		setAvailablePlugins(viewer);	
		try {
			List saved = configuration.getAttribute(ILauncherConstants.SUITABLE_PLUGINS, new LinkedList());
			if ( saved.size() > 0 ) {
				viewer.setAllChecked(false);
				for (Object str : saved) {
					viewer.setChecked(RubytlPlugin.fromString((String) str), true);
				}	
			}	
		} catch (CoreException e) { e.printStackTrace(); }
		
		// TODO: Check default plugin if no plugin is checked	
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		List<String> plugins = new LinkedList<String>();
		for (Object o : viewer.getCheckedElements()) {
			RubytlPlugin p = (RubytlPlugin) o;
			plugins.add(p.uniqueName());
		}				
		configuration.setAttribute(ILauncherConstants.SUITABLE_PLUGINS, plugins);
	}

	public String getName() {
		return "Plugins";
	}

	protected void setAvailablePlugins(CheckboxTableViewer tableViewer) {
		// Clear the table
		tableViewer.getTable().clearAll();
		tableViewer.refresh();
		
		// Get plugin path list
		String pathsAsString = DebugUIPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.PLUGIN_PATH);
		String paths[] = pathsAsString.split(":");
		
		// Add plugins to the table
		List<RubytlPlugin> plugins = RubytlCore.getDefault().getPluginManager().getAvailablePlugins(paths);
		for (RubytlPlugin plugin : plugins) {
			tableViewer.add(plugin);
			tableViewer.setChecked(plugin, plugin.getBelongsToDefaultPath());				
		}
	}
	
	/**
	 * Factorizar con otros PLUGINS
	 * @param parent
	 * @return
	 */
    protected Composite createPageRoot(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        composite.setLayout(layout);

        setControl(composite);
        return composite;
    }
	
	/**
	 * TODO: Factorizar la creación de tablas de este tipo
	 * COPIADO Y MODIFICADO DE RDT
	 *  
	 * @param composite
	 * @return
	 */
	protected Table createPluginsTable(Composite composite) {
		Table table = new Table(composite, SWT.CHECK | SWT.BORDER | SWT.FULL_SELECTION);

		GridData data = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(data);
		table.setHeaderVisible(true);
		table.setLinesVisible(false);

		TableColumn column = new TableColumn(table, SWT.NULL);
		column.setText(DebugUIPlugin.MESSAGES.getString("PluginsTab.pluginName")); //$NON-NLS-1$
		column.setWidth(125);

		column = new TableColumn(table, SWT.NULL);
		column.setText(DebugUIPlugin.MESSAGES.getString("PluginsTab.pluginPath")); //$NON-NLS-1$
		column.setWidth(350);

		return table;
	}	

	/**
	 * FACTORIZAR JUNTO CON LO DE ARRIBA
	 * @param table
	 * @return 
	 */
	protected CheckboxTableViewer createPluginsTableViewer(Table table) {
		CheckboxTableViewer tableViewer = new CheckboxTableViewer(table);

		//tableViewer.setContentProvider()
		tableViewer.setLabelProvider(new PluginsLabelProvider());
		// tableViewer.setContentProvider(new PluginsContentProvider());

		
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent evt) {
				updateLaunchConfigurationDialog();
			}
		});

		tableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				RubytlPlugin p = (RubytlPlugin) event.getElement();
				if ( event.getChecked() ) {
					loadPluginUI(p);
				} else { 
					unloadPluginUI(p);
				}				
				updateLaunchConfigurationDialog();
			}
		});
		
		return tableViewer;
	}	

	private class PluginsLabelProvider implements ITableLabelProvider {
		
		public Image getColumnImage(Object element, int columnIndex) { return null; }

		public String getColumnText(Object element, int columnIndex) {
			RubytlPlugin p = (RubytlPlugin) element;
			switch(columnIndex) {
			case 0: return p.getName(); 
			case 1: return p.getPath(); 
			default: throw new RuntimeException("Too much columns");
			}
		}

		public void addListener(ILabelProviderListener listener) {	}
		public void dispose() {	}
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}
		public void removeListener(ILabelProviderListener listener) { }			
	}

	/**
	 * Creates a new tab for the options of a RubyTL plugin to be entered.
	 * @param p The plugin whose options are going to be loaded.
	 */
	protected void loadPluginUI(RubytlPlugin p) {
		String path = p.getUIDefinitionPath();
		ParameterDslLoader loader = new ParameterDslLoader(path);
		Composite c = pluginParametersTab.createNewPluginSpace();
		loader.evaluate(c);
	}
	
	/**
	 * Deletes the tab associated with the given plugin.
	 * @param p The plugin whose options are going to be unload.
	 */
	protected void unloadPluginUI(RubytlPlugin p) {
		
	}
	
}

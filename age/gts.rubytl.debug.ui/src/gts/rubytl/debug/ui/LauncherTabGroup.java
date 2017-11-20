package gts.rubytl.debug.ui;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;


public class LauncherTabGroup extends AbstractLaunchConfigurationTabGroup {

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		PluginParametersTab pluginParametersTab = new PluginParametersTab();
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new MetamodelsTab(),
				new PluginsTab(pluginParametersTab),
				new OptionsTab(),
				pluginParametersTab
		};
		setTabs(tabs);		
	}
	
	
	
}


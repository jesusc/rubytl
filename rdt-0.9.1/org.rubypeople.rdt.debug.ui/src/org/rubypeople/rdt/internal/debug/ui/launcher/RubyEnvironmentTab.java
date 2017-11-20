package org.rubypeople.rdt.internal.debug.ui.launcher;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.internal.core.LoadpathEntry;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiMessages;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;
import org.rubypeople.rdt.internal.debug.ui.rubyvms.AddVMDialog;
import org.rubypeople.rdt.internal.debug.ui.rubyvms.IAddVMDialogRequestor;
import org.rubypeople.rdt.internal.debug.ui.rubyvms.RubyVMMessages;
import org.rubypeople.rdt.internal.launching.RubyLaunchConfigurationAttribute;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMInstallType;
import org.rubypeople.rdt.launching.RubyRuntime;
import org.rubypeople.rdt.launching.VMStandin;

public class RubyEnvironmentTab extends AbstractLaunchConfigurationTab {
	protected ListViewer loadPathListViewer;

	protected List installedInterpretersWorkingCopy;

	protected Combo interpreterCombo;

	protected Button loadPathDefaultButton;

	public RubyEnvironmentTab() {
		super();
	}

	public void createControl(Composite parent) {
		Composite composite = createPageRoot(parent);

		TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		tabFolder.setLayoutData(gridData);

		addLoadPathTab(tabFolder);
		addInterpreterTab(tabFolder);
	}

	protected void addLoadPathTab(TabFolder tabFolder) {
		Composite loadPathComposite = new Composite(tabFolder, SWT.NONE);
		loadPathComposite.setLayout(new GridLayout());

		loadPathListViewer = new ListViewer(loadPathComposite, SWT.BORDER
				| SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		loadPathListViewer.setContentProvider(new ArrayContentProvider());
		loadPathListViewer.setLabelProvider(new LoadPathEntryLabelProvider());
		loadPathListViewer.getList().setLayoutData(
				new GridData(GridData.FILL_BOTH));

		TabItem loadPathTab = new TabItem(tabFolder, SWT.NONE, 0);
		loadPathTab
				.setText(RdtDebugUiMessages.LaunchConfigurationTab_RubyEnvironment_loadPathTab_label);
		loadPathTab.setControl(loadPathComposite);
		loadPathTab.setData(loadPathListViewer);

		loadPathDefaultButton = new Button(loadPathComposite, SWT.CHECK);
		loadPathDefaultButton
				.setText(RdtDebugUiMessages.LaunchConfigurationTab_RubyEnvironment_loadPathDefaultButton_label);
		loadPathDefaultButton.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING));
		loadPathDefaultButton
				.addSelectionListener(getLoadPathDefaultButtonSelectionListener());

		loadPathDefaultButton.setEnabled(false); // for now, until the load
													// path is customizable on
													// the configuration
	}

	protected SelectionListener getLoadPathSelectionListener() {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Loadpath list selection occurred: "
						+ e.getSource());
			}
		};
	}

	protected SelectionListener getLoadPathDefaultButtonSelectionListener() {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setUseLoadPathDefaults(((Button) e.getSource()).getSelection());
			}
		};
	}

	protected void addInterpreterTab(TabFolder tabFolder) {
		Composite interpreterComposite = new Composite(tabFolder, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		interpreterComposite.setLayout(layout);
		interpreterComposite.setLayoutData(new GridData(
				GridData.FILL_HORIZONTAL));

		createVerticalSpacer(interpreterComposite, 2);

		interpreterCombo = new Combo(interpreterComposite, SWT.READ_ONLY);
		interpreterCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		initializeInterpreterCombo(interpreterCombo);
		interpreterCombo.addModifyListener(getInterpreterComboModifyListener());

		Button interpreterAddButton = new Button(interpreterComposite, SWT.PUSH);
		interpreterAddButton
				.setText(RdtDebugUiMessages.LaunchConfigurationTab_RubyEnvironment_interpreterAddButton_label);
		interpreterAddButton.addSelectionListener(new AddInterpreterSelectionAdapter(interpreterCombo, getShell()));

		TabItem interpreterTab = new TabItem(tabFolder, SWT.NONE);
		interpreterTab
				.setText(RdtDebugUiMessages.LaunchConfigurationTab_RubyEnvironment_interpreterTab_label);
		interpreterTab.setControl(interpreterComposite);
	}

	private static class AddInterpreterSelectionAdapter extends
			SelectionAdapter implements IAddVMDialogRequestor {

		private Combo fCombo;
		private Shell fShell;

		public AddInterpreterSelectionAdapter(Combo combo, Shell shell) {
			fCombo = combo;
			fShell = shell;
		}
		
		public void widgetSelected(SelectionEvent evt) {
			AddVMDialog dialog = new AddVMDialog(this,
					fShell, RubyRuntime.getVMInstallTypes(),
					null);
			dialog.setTitle(RubyVMMessages.InstalledJREsBlock_7);
			if (dialog.open() != Window.OK) {
				return;
			}
		}
		
		public boolean isDuplicateName(String name) {
			return false;
		}

		public void vmAdded(IVMInstall vm) {
			// FIXME Use something like JREsUpdater?
			if (vm instanceof VMStandin) {
				VMStandin standin = (VMStandin) vm;
				standin.convertToRealVM();
			}
			fCombo.add(vm.getName());
			fCombo.select(fCombo.indexOf(vm.getName()));
		}

	}

	protected ModifyListener getInterpreterComboModifyListener() {
		return new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				updateLaunchConfigurationDialog();
			}
		};
	}

	protected void createVerticalSpacer(Composite comp, int colSpan) {
		Label label = new Label(comp, SWT.NONE);
		GridData gd = new GridData();
		gd.horizontalSpan = colSpan;
		label.setLayoutData(gd);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		IVMInstall defaultInterpreter = RubyRuntime.getDefaultVMInstall();
		if (defaultInterpreter != null) {
			configuration.setAttribute(
					RubyLaunchConfigurationAttribute.SELECTED_INTERPRETER,
					defaultInterpreter.getName());
		}
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		initializeLoadPath(configuration);
		initializeInterpreterSelection(configuration);
	}

	protected void initializeLoadPath(ILaunchConfiguration configuration) {
		boolean useDefaultLoadPath = true;
		try {
			useDefaultLoadPath = configuration.getAttribute(
					RubyLaunchConfigurationAttribute.USE_DEFAULT_LOAD_PATH,
					true);
			setUseLoadPathDefaults(useDefaultLoadPath);
			if (useDefaultLoadPath) {
				String projectName = configuration.getAttribute(
						IRubyLaunchConfigurationConstants.ATTR_PROJECT_NAME, "");
				if (projectName.length() != 0) {
					IRubyProject project = RubyModelManager
							.getRubyModelManager().getRubyModel()
							.getRubyProject(projectName);
					if (project != null) {
						ILoadpathEntry[] entries = project
								.getResolvedLoadpath(true);
						loadPathListViewer.setInput(entries);
					}
				}
			}
		} catch (CoreException e) {
			log(e);
		}
	}

	protected void setUseLoadPathDefaults(boolean useDefaults) {
		loadPathListViewer.getList().setEnabled(!useDefaults);
		loadPathDefaultButton.setSelection(useDefaults);
	}

	protected void initializeInterpreterSelection(
			ILaunchConfiguration configuration) {
		String interpreterName = null;
		try {
			interpreterName = configuration.getAttribute(
					RubyLaunchConfigurationAttribute.SELECTED_INTERPRETER, "");
		} catch (CoreException e) {
			log(e);
		}
		if (interpreterName != null && !interpreterName.equals(""))
			interpreterCombo.select(interpreterCombo.indexOf(interpreterName));
	}

	protected void initializeInterpreterCombo(Combo interpreterCombo) {
		installedInterpretersWorkingCopy = new ArrayList();
		List standins = new ArrayList();
		IVMInstallType[] types = RubyRuntime.getVMInstallTypes();
		for (int i = 0; i < types.length; i++) {
			IVMInstallType type = types[i];
			IVMInstall[] installs = type.getVMInstalls();
			for (int j = 0; j < installs.length; j++) {
				IVMInstall install = installs[j];
				standins.add(new VMStandin(install));
			}
		}
		installedInterpretersWorkingCopy.addAll(standins);	
		
		String[] interpreterNames = new String[installedInterpretersWorkingCopy
				.size()];
		for (int interpreterIndex = 0; interpreterIndex < installedInterpretersWorkingCopy
				.size(); interpreterIndex++) {
			IVMInstall interpreter = (IVMInstall) installedInterpretersWorkingCopy
					.get(interpreterIndex);
			interpreterNames[interpreterIndex] = interpreter.getName();
		}
		interpreterCombo.setItems(interpreterNames);

		IVMInstall selectedInterpreter = RubyRuntime.getDefaultVMInstall();
		if (selectedInterpreter != null)
			interpreterCombo.select(interpreterCombo
					.indexOf(selectedInterpreter.getName()));
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		int selectionIndex = interpreterCombo.getSelectionIndex();
		if (selectionIndex >= 0)
			configuration.setAttribute(
					RubyLaunchConfigurationAttribute.SELECTED_INTERPRETER,
					interpreterCombo.getItem(selectionIndex));

		configuration.setAttribute(
				RubyLaunchConfigurationAttribute.USE_DEFAULT_LOAD_PATH,
				loadPathDefaultButton.getSelection());

		if (!loadPathDefaultButton.getSelection()) {
			List loadPathEntries = (List) loadPathListViewer.getInput();
			List loadPathStrings = new ArrayList();
			for (Iterator iterator = loadPathEntries.iterator(); iterator
					.hasNext();) {
				LoadpathEntry entry = (LoadpathEntry) iterator.next();
				loadPathStrings.add(entry.getPath().toString());
			}
			configuration.setAttribute(
					IRubyLaunchConfigurationConstants.ATTR_LOADPATH,
					loadPathStrings);
		}
	}

	protected Composite createPageRoot(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		createVerticalSpacer(composite, 2);
		setControl(composite);

		return composite;
	}

	public String getName() {
		return RdtDebugUiMessages.LaunchConfigurationTab_RubyEnvironment_name;
	}

	public boolean isValid(ILaunchConfiguration launchConfig) {
		try {
			String selectedInterpreter = launchConfig.getAttribute(
					RubyLaunchConfigurationAttribute.SELECTED_INTERPRETER, "");
			if (selectedInterpreter.length() == 0) {
				setErrorMessage(RdtDebugUiMessages.LaunchConfigurationTab_RubyEnvironment_interpreter_not_selected_error_message);
				return false;
			}
		} catch (CoreException e) {
			log(e);
		}

		setErrorMessage(null);
		return true;
	}

	protected void log(Throwable t) {
		RdtDebugUiPlugin.log(t);
	}

	public Image getImage() {
		return RubyPluginImages.get(RubyPluginImages.IMG_CTOOLS_RUBY);
	}

}
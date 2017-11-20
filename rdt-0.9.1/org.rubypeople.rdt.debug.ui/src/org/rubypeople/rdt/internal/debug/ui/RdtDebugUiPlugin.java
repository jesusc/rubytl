package org.rubypeople.rdt.internal.debug.ui;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.internal.ui.ImageDescriptorRegistry;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.debug.ui.RdtDebugUiConstants;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;
import org.rubypeople.rdt.internal.debug.ui.evaluation.EvaluationExpressionModel;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.text.RubyTextTools;

public class RdtDebugUiPlugin extends AbstractUIPlugin implements RdtDebugUiConstants {


	public static final String PLUGIN_ID = "org.rubypeople.rdt.debug.ui"; //$NON-NLS-1$
	protected static RdtDebugUiPlugin plugin;
    private EvaluationExpressionModel evaluationExpressionModel;
    
    private ImageDescriptorRegistry fImageDescriptorRegistry;
	private RubyTextTools fTextTools;

	public RdtDebugUiPlugin() {
		super();
		plugin = this;
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	public static IWorkbenchPage getActivePage() {
		IWorkbenchWindow w = getActiveWorkbenchWindow();
		if (w != null) {
			return w.getActivePage();
		}
		return null;
	}

	public static RdtDebugUiPlugin getDefault() {
		return plugin;
	}

	public static IWorkspace getWorkspace() {
		return RubyCore.getWorkspace();
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, RdtDebugUiMessages.RdtDebugUiPlugin_internalErrorOccurred, e));
	}
	
	public void start(BundleContext context) throws Exception {
        super.start(context);
		IAdapterManager manager= Platform.getAdapterManager();
		ActionFilterAdapterFactory actionFilterAdapterFactory= new ActionFilterAdapterFactory();
		manager.registerAdapters(actionFilterAdapterFactory, RubyVariable.class);
		new CodeReloader();
		try {
			EvaluationContextManager.startup();
		} catch (Exception e) {
			log(e);
		}
    }

	@Override
	public void stop(BundleContext context) throws Exception {
		try {
			if (fImageDescriptorRegistry != null) {
				fImageDescriptorRegistry.dispose();
			}
		} finally {
			super.stop(context);
		}
	}
	
	public EvaluationExpressionModel getEvaluationExpressionModel() {
		if (evaluationExpressionModel == null) {
            evaluationExpressionModel = new EvaluationExpressionModel() ;  
        }
        return evaluationExpressionModel ;
    }

	public static String getUniqueIdentifier() {
		return PLUGIN_ID;
	}

	/**
	 * Returns the standard display to be used. The method first checks, if
	 * the thread calling this method has an associated display. If so, this
	 * display is returned. Otherwise the method returns the default display.
	 */
	public static Display getStandardDisplay() {
		Display display;
		display= Display.getCurrent();
		if (display == null)
			display= Display.getDefault();
		return display;		
	}

	/**
	 * Returns the image descriptor registry used for this plugin.
	 */
	public static ImageDescriptorRegistry getImageDescriptorRegistry() {
		if (getDefault().fImageDescriptorRegistry == null) {
			getDefault().fImageDescriptorRegistry = new ImageDescriptorRegistry();
		}
		return getDefault().fImageDescriptorRegistry;
	}

	public RubyTextTools getRubyTextTools() {
		if (fTextTools == null) {
			fTextTools = new RubyTextTools(PreferenceConstants.getPreferenceStore());
		}
		return fTextTools;
	}

	/**
	 * Returns the active workbench shell or <code>null</code> if none
	 * 
	 * @return the active workbench shell or <code>null</code> if none
	 */
	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		if (window != null) {
			return window.getShell();
		}
		return null;
	}

		/**
	 * Utility method with conventions
	 */
	public static void errorDialog(String message, Throwable t) {
		log(t);
		Shell shell = getActiveWorkbenchShell();
		if (shell != null) {
			IStatus status= new Status(IStatus.ERROR, getUniqueIdentifier(), RdtDebugUiConstants.INTERNAL_ERROR, "Error logged from RDT Debug UI: ", t); //$NON-NLS-1$	
			ErrorDialog.openError(shell, "Error", message, status); 
		}	
	}
}

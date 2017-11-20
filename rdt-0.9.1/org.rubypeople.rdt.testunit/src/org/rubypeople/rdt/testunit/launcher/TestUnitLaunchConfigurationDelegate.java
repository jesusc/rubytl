package org.rubypeople.rdt.testunit.launcher;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.SocketUtil;
import org.rubypeople.rdt.internal.testunit.ui.TestUnitMessages;
import org.rubypeople.rdt.internal.testunit.ui.TestunitPlugin;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;
import org.rubypeople.rdt.launching.RubyLaunchDelegate;

public class TestUnitLaunchConfigurationDelegate extends RubyLaunchDelegate {
	/**
	 * The single test type, or "" iff running a launch container.
	 */
	public static final String TESTTYPE_ATTR = TestunitPlugin.PLUGIN_ID + ".TESTTYPE"; //$NON-NLS-1$
	/**
	 * The test method, or "" iff running the whole test type.
	 */
	public static final String TESTNAME_ATTR = TestunitPlugin.PLUGIN_ID + ".TESTNAME"; //$NON-NLS-1$
	/**
	 * The launch container, or "" iff running a single test type.
	 */
	public static final String LAUNCH_CONTAINER_ATTR = TestunitPlugin.PLUGIN_ID + ".CONTAINER"; //$NON-NLS-1$

	public static final String ID_TESTUNIT_APPLICATION = "org.rubypeople.rdt.testunit.launchconfig"; //$NON-NLS-1$
	
	private int port = -1;

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {	
		IType[] testTypes = findTestTypes(configuration, monitor);
		
//		setDefaultSourceLocator(launch, configuration);
		launch.setAttribute(TestunitPlugin.TESTUNIT_PORT_ATTR, Integer.toString(getPort()));
		if (testTypes != null && testTypes.length > 0) launch.setAttribute(TESTTYPE_ATTR, testTypes[0].getHandleIdentifier());

		
		super.launch(configuration, mode, launch, monitor);
	}
	
	protected IType[] findTestTypes(ILaunchConfiguration configuration, IProgressMonitor pm) throws CoreException {
		IRubyProject javaProject= getRubyProject(configuration);
		if ((javaProject == null) || !javaProject.exists()) {
			informAndAbort(TestUnitMessages.TestUnitBaseLaunchConfiguration_error_invalidproject, null, IRubyLaunchConfigurationConstants.ERR_NOT_A_RUBY_PROJECT); 
		}
//		if (!TestSearchEngine.hasTestCaseType(javaProject)) {
//			informAndAbort(TestUnitMessages.JUnitBaseLaunchConfiguration_error_junitnotonpath, null, ITestUnitStatusConstants.ERR_JUNIT_NOT_ON_PATH);
//		}

		String containerHandle = configuration.getAttribute(LAUNCH_CONTAINER_ATTR, ""); //$NON-NLS-1$
		if (containerHandle.length() > 0) {
			IRubyElement element = RubyCore.create(containerHandle);
			IRubyScript script = (IRubyScript) element;
			if (script != null) {
				IType type = script.findPrimaryType();
				if (type != null)
					return new IType[] { type };
			}
		}
		String testTypeName= configuration.getAttribute(TESTTYPE_ATTR, (String) null);
		if (testTypeName != null && testTypeName.length() > 0) {
			return new IType[] {javaProject.findType(testTypeName, pm)};
		}
		return new IType[0];
	}
	
	protected void informAndAbort(String message, Throwable exception, int code) throws CoreException {
		IStatus status= new Status(IStatus.INFO, TestunitPlugin.PLUGIN_ID, code, message, exception);
		if (showStatusMessage(status))
			throw new CoreException(status);
		abort(message, exception, code);
	}
	
	private boolean showStatusMessage(final IStatus status) {
		final boolean[] success= new boolean[] { false };
		getDisplay().syncExec(
				new Runnable() {
					public void run() {
						Shell shell= TestunitPlugin.getActiveWorkbenchShell();
						if (shell == null)
							shell= getDisplay().getActiveShell();
						if (shell != null) {
							MessageDialog.openInformation(shell, TestUnitMessages.JUnitBaseLaunchConfiguration_dialog_title, status.getMessage());
							success[0]= true;
						}
					}
				}
		);
		return success[0];
	}
	
	private Display getDisplay() {
		Display display;
		display= Display.getCurrent();
		if (display == null)
			display= Display.getDefault();
		return display;		
	}
	
	private static URL getOriginalTestRunner() {
		IPath path = new Path("ruby").append(TestUnitLaunchShortcut.TEST_RUNNER_FILE);
		URL url = FileLocator.find(TestunitPlugin.getDefault().getBundle(), path, null);
		if (url == null)
			throw new RuntimeException("Expected directory of RemoteTestRunner.rb does not exist: " + path); 
		return url;
	}
	
	public static String getTestRunnerPath() {
		IPath path = TestunitPlugin.getDefault().getStateLocation().append(TestUnitLaunchShortcut.TEST_RUNNER_FILE);
		// FIXME What if the file already exists but we have a newer version of the original script and we want to copy it over top?
		if (!path.toFile().exists()) {
			// copy original over
			Writer writer = null;
			InputStream stream = null;
			try {
				URL url = getOriginalTestRunner();
				stream = url.openStream();
				path.toFile().createNewFile();
				writer = new FileWriter(path.toFile());
				int b = 0; // TODO Copy over on byte buffers rather than per byte to speed this up
				while((b = stream.read()) != -1) {
					writer.write(b);
				}
			} catch (IOException e) {
				// ignore
				e.printStackTrace();
			} finally {
				try {
					if (stream != null) stream.close();
				} catch (IOException e) {
					// ignore
				}
				try {
					if (writer != null) writer.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		
		if (!path.toFile().exists())
			throw new RuntimeException("Expected directory of RemoteTestRunner.rb does not exist: " + path); 
	
		return path.toPortableString();
	}
	
	private int getPort() {
		if (port == -1) {
			port  = SocketUtil.findFreePort();
		}
		return port;
	}

	@Override
	public String getProgramArguments(ILaunchConfiguration configuration) throws CoreException {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getLaunchContainerPath(configuration));
		buffer.append(' ');
		buffer.append(Integer.toString(getPort()));
		buffer.append(' ');
		buffer.append(Boolean.toString(false));
		buffer.append(' ');
		buffer.append(configuration.getAttribute(TestUnitLaunchConfigurationDelegate.TESTTYPE_ATTR, ""));
		buffer.append(' ');
		buffer.append(configuration.getAttribute(TestUnitLaunchConfigurationDelegate.TESTNAME_ATTR, ""));
		return buffer.toString();
	}

	private String getLaunchContainerPath(ILaunchConfiguration configuration) throws CoreException {
		String container = configuration.getAttribute(TestUnitLaunchConfigurationDelegate.LAUNCH_CONTAINER_ATTR, "");
		IRubyElement element = (IRubyElement) RubyCore.create(container);
		if (element != null)
		  container = element.getResource().getProjectRelativePath().toOSString();
		if (!container.startsWith("\"") && container.indexOf(' ') != -1) {
			container = '"' + container + '"';
		}
		return container;
	}
}
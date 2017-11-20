package gts.rubytl.launching;

import gts.eclipse.core.resources.ResourcesUtil;
import gts.rubytl.core.RubytlCore;
import gts.rubytl.core.resources.RubytlPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.rubypeople.rdt.internal.launching.LaunchingMessages;
import org.rubypeople.rdt.internal.launching.LaunchingPlugin;
// jesusc: Changed for IVMInstall
// import org.rubypeople.rdt.internal.launching.RubyInterpreter;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.RubyLaunchDelegate;
import org.rubypeople.rdt.launching.RubyRuntime;


public class LauncherLaunchConfigurationDelegate implements
		ILaunchConfigurationDelegate {

	private static final String ConfigurationClassName = "RubyTL::LaunchConfiguration";
	
	IProject project = null;
	IFile sourceMetamodel = null;
	IFile targetMetamodel = null;
	IFile sourceModel = null;
	IFile targetModel = null;
	IFile transformation = null;
	String transformationModule = null;
	
	ILaunch launch;
	List<RubytlPlugin> plugins = new LinkedList<RubytlPlugin>();

	private String executionMode;
	
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {

		String strProject = getValue(configuration, ILauncherConstants.PROJECT);
		String strSourceMetamodel = getValue(configuration, ILauncherConstants.SOURCE_METAMODEL);
		String strTargetMetamodel = getValue(configuration, ILauncherConstants.TARGET_METAMODEL);
		String strSourcePackage = getValue(configuration, ILauncherConstants.SOURCE_PACKAGE);
		String strTargetPackage = getValue(configuration, ILauncherConstants.TARGET_PACKAGE);
		String strSourceModel = getValue(configuration, ILauncherConstants.SOURCE_MODEL);
		String strTargetModel = getValue(configuration, ILauncherConstants.TARGET_MODEL);
		String strTransformation = getValue(configuration, ILauncherConstants.TRANSFORMATION);
		String strTransformationModule = getValue(configuration, ILauncherConstants.TRANSFORMATION_MODULE);
				
		this.project = RubytlCore.getProjectByName(strProject);
		this.sourceMetamodel = ResourcesUtil.getFile(project, strSourceMetamodel);
		this.targetMetamodel = ResourcesUtil.getFile(project, strTargetMetamodel);
		this.sourceModel = ResourcesUtil.getFile(project, strSourceModel);
		this.targetModel = ResourcesUtil.getFile(project, strTargetModel);
		this.transformation = ResourcesUtil.getFile(project, strTransformation);
		this.plugins = getPlugins(configuration, ILauncherConstants.SUITABLE_PLUGINS);
		this.transformationModule = strTransformationModule;
		this.launch = launch;
		this.executionMode = mode;
		
		execute(sourceMetamodel, strSourcePackage, targetMetamodel, strTargetPackage, sourceModel, transformation, targetModel);
	}

	

	private void execute(IFile sourceMetamodel, String sourcePkg, 
						 IFile targetMetamodel, String targetPkg, 
						 IFile sourceModel, IFile transformation, IFile targetModel) throws CoreException {
			delegateInNativeRubyInterpreter();
	}
	

	
	private void delegateInNativeRubyInterpreter() throws CoreException  {
		// RubyInterpreter is no longer used
		// RubyInterpreter interpreter = RubyRuntime.getDefault().getSelectedInterpreter();
		IVMInstall interpreter = RubyRuntime.getDefaultVMInstall();
		

		String root = RubytlCore.getDefault().getLibraryPath();
		List<String> commandLine = new ArrayList<String>(); 		
		String path = ResourcesUtil.joinPath(root, "rubytl", "lib", "rubytl.rb");
		
		// A hack to allow Win32 to call the Ruby interpreters 
		if ( File.separatorChar == '\\' ) {
			path = path.substring(1).replace('/', '\\');		
		}
		
		commandLine.add(path);
		commandLine.add("notRakefile");
		commandLine.add(createConfiguration());
		commandLine.add(ConfigurationClassName);

		if (interpreter == null) {
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, IStatus.OK, LaunchingMessages.RdtLaunchingPlugin_noInterpreterSelected, null));
        }
		
		System.out.println("TODO: XXX");
		// jesusc: TODO: Adapt to the new type of launchers
		/*
		try {	
			Process nativeRubyProcess = interpreter.exec(commandLine, project.getLocation().toFile());
		    
			String label = "Ruby " + interpreter.getCommand() + " : " + "getFileName";
			
			Map defaultAttributes = new HashMap();
		    defaultAttributes.put(IProcess.ATTR_PROCESS_TYPE, "ruby");
			IProcess process = DebugPlugin.newProcess(launch, nativeRubyProcess, label, defaultAttributes);
			process.setAttribute(LaunchingPlugin.PLUGIN_ID + ".launcher.cmdline", commandLine.toString());
			final String targetFileAttribute = LauncherPlugin.PLUGIN_ID + ".target.file";
			process.setAttribute(targetFileAttribute, this.targetModel.getProjectRelativePath().toString());
			
			// Controlar cuando termina el proceso y entonces refrescar el elemento que se ha creado
			// para que aparezca en pantalla o al menos eclipse no diga que no est� sincronizado
			// con el workspace
			DebugPlugin.getDefault().addDebugEventListener(new IDebugEventSetListener() {
				public void handleDebugEvents(DebugEvent[] events) {
					for (DebugEvent event : events) {
						if ( event.getKind() == DebugEvent.TERMINATE ) {
							RuntimeProcess runtime = (RuntimeProcess) event.getSource();
							String file = runtime.getAttribute(targetFileAttribute);
							if ( file != null ) {
								try {
									ResourcesUtil.refreshFile(project, file);
								} catch (CoreException e) {
									// TODO: Report error
									e.printStackTrace();
								}
							}
						}
					}
				}				
			});
			// return process ;
		} catch ( Exception e ) {
			// Avisar de lo que pase...
			e.printStackTrace();
		}
		*/
	}

	private String createConfiguration() {
		StringBuffer buffer = new StringBuffer(512);
		buffer.append("class " + ConfigurationClassName+ "\n");
		buffer.append("  self.source_metamodel = '" + this.sourceMetamodel.getLocation().toOSString() + "'\n");
		buffer.append("  self.target_metamodel = '" + this.targetMetamodel.getLocation().toOSString() + "'\n");
		buffer.append("  self.source_model     = '" + this.sourceModel.getLocation().toOSString() + "'\n");
		buffer.append("  self.target_model     = '" + this.targetModel.getLocation().toOSString() + "'\n");
		buffer.append("  self.transformation   = '" + this.transformation.getLocation().toOSString() + "'\n");
		buffer.append("  self.tmodule          = '" + this.transformationModule + "'\n");
		buffer.append("  self.allowed_plugins  = [" + convertToRuby(plugins) + "]\n");		
		buffer.append("end\n");
		return buffer.toString();
	}
	

	private String convertToRuby(List<RubytlPlugin> plugins) {
		if ( plugins.size() == 0 ) throw new RuntimeException("At least one plugin must be selected");
		
		String str = plugins.get(0).getRubyName();
		for(int i = 1; i < plugins.size(); i++) {
			str += ", " + plugins.get(i).getRubyName();
		}
		return str;
	}

	
	private String getValue(ILaunchConfiguration configuration, String constant) {
		try {
			String value = configuration.getAttribute(constant, "");
			return value;
		} catch (CoreException e) { 
			throw new RuntimeException("Error en el atributo " + constant);			
		}		 
	}
	
	private List<RubytlPlugin> getPlugins(ILaunchConfiguration configuration, String constant) {
		LinkedList<RubytlPlugin> result = new LinkedList<RubytlPlugin>();
		try {
			List<String> plugins = (List<String>) configuration.getAttribute(constant, new LinkedList<String>());
			for (String str : plugins) {
				result.add(RubytlPlugin.fromString(str));
			}
		} catch (CoreException e) { 
			throw new RuntimeException("Error en el atributo " + constant);			
		}
		return result;
	}
	
	
}

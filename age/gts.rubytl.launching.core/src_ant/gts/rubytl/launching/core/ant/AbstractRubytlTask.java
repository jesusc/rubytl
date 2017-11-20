package gts.rubytl.launching.core.ant;

import gts.rubytl.launching.core.ant.TaskElements.ModelMapping;

import java.util.LinkedList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Base class for all other RubyTL's task.
 * 
 * @author jesus
 */
public abstract class AbstractRubytlTask extends Task {
	protected String ruby;
	protected String rubytl;
	protected String configuration;
	private TaskElements.FilePathElement projectPath;
	
	
	public void setName(String name) { }
	
	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}
	
	public void setRuby(String ruby) {
		this.ruby = ruby;
	}
	
	public void setRubyTL(String rubytl) {
		this.rubytl = rubytl;
	}

	public TaskElements.FilePathElement createProject() {
		this.projectPath = new TaskElements.FilePathElement();
		return this.projectPath;
	}

	public void execute() throws BuildException {
		Configuration configuration = computeConfiguration(this.ruby, this.rubytl);

		String projectPath = this.getProject().getBaseDir().getAbsolutePath();
		if ( this.projectPath != null ) {
			projectPath = this.projectPath.getPath().getAbsolutePath();
		}
		
		if ( configuration.getRuby() == null )   throw new BuildException("'ruby' attribute required");
		if ( configuration.getRubytl() == null ) throw new BuildException("'rubytl' attribute required");

		checkTaskValues();
		
		try {
			executeRubyTL(projectPath, configuration);
		} catch ( Exception e ) {
			e.printStackTrace();
			throw new BuildException(e); 
		}
	}

	protected Configuration computeConfiguration(String ruby, String rubytl) {
		Configuration configuration = null;
		
		if ( this.configuration != null ) {
			Object o = this.getProject().getReference(this.configuration);			
			configuration = (gts.rubytl.launching.core.ant.Configuration) o;
			if ( configuration == null ) {
				throw new BuildException("Configuration '" + this.configuration + "' not defined");
			}
		}		
		
		/*
		Enumeration<?> e = this.getOwningTarget().getDependencies();		
		while ( e.hasMoreElements() ) {
			String targetName = (String) e.nextElement();
			Target target = (Target) this.getProject().getTargets().get(targetName);
			Task[] tasks = target.getTasks();
			
			for (int i = 0; i < tasks.length; i++) {
				Task task = tasks[i];
				System.out.println(task.getTaskName());
				if ( task instanceof UnknownElement ) {
					UnknownElement u = (UnknownElement) task;
					
					//Enumeration e1 = task.getRuntimeConfigurableWrapper().getChildren();
					
					//while ( e1.hasMoreElements() ) {
					//	RuntimeConfigurable o = (RuntimeConfigurable) e1.nextElement();
					//	System.out.println(o.getProxy());					
					//}					
					
					System.out.println(u.getRuntimeConfigurableWrapper());
					Configuration tmp = (Configuration) u.getRuntimeConfigurableWrapper().getAttributeMap().get(ConfigureTask.CONFIGURATION_ATTRIBUTE);
					
					if ( tmp == null ) continue;
					if ( configuration != null )
							this.log("Too many 'configure' task");
					configuration = tmp;
					
					System.out.println(tmp);
				}
				
				//if ( task instanceof ConfigureTask ) {
				//	System.out.println("------------");
				//	if ( configureTask != null )
				//		this.log("Too many 'configure' task");
				//	configureTask = (ConfigureTask) task;
				//}
			}
		}
		*/
		
		if ( configuration == null ) { 
			configuration = new Configuration(ruby, rubytl, new LinkedList<ModelMapping>());
		}
		else {
			if ( ruby != null   ) configuration.setRuby(ruby);
			if ( rubytl != null ) configuration.setRubytl(rubytl);
		}
		
		return configuration;
	}

	protected abstract void checkTaskValues() throws BuildException;
	protected abstract void executeRubyTL(String projectPath, Configuration configuration);

}

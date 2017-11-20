package gts.rubytl.launching.core.ant;

import java.util.LinkedList;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Base class for all other RubyTL's task.
 * 
 * @author jesus
 */
public class ConfigureTask extends Task {
	protected TaskElements.PathElement ruby;
	protected TaskElements.PathElement rubytl;
	protected String name;
	protected LinkedList<TaskElements.ModelMapping> mappings = new LinkedList<TaskElements.ModelMapping>();
			
	public void execute() throws BuildException {
		if ( this.name == null ) throw new BuildException("'name' attribute required");
		if ( this.ruby == null ) throw new BuildException("'ruby' element required");
		if ( this.rubytl == null ) throw new BuildException("'rubytl' element required");

		this.getProject().addReference(this.name, this.getConfiguration());
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public TaskElements.PathElement createRuby() {
		this.ruby = new TaskElements.PathElement();
		return this.ruby;
	}
		
	public TaskElements.PathElement createRubytl() {
		this.rubytl = new TaskElements.PathElement();
		return this.rubytl;
	}
	
	public TaskElements.ModelMapping createMapping() {
		TaskElements.ModelMapping element = new TaskElements.ModelMapping();
		mappings.add(element);
		return element;
	}

	public Configuration getConfiguration() {
		LinkedList<TaskElements.ModelMapping> m = new LinkedList<TaskElements.ModelMapping>(this.mappings);
		return new Configuration(this.ruby.getPath(), this.rubytl.getPath(), m);
	}
}

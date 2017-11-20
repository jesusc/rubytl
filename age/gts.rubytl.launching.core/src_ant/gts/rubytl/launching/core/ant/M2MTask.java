package gts.rubytl.launching.core.ant;

import gts.rubytl.launching.core.configuration.Binding;
import gts.rubytl.launching.core.configuration.data.TaskM2MConfigurationData;
import gts.rubytl.launching.core.launcher.RtlLauncher;

import java.util.LinkedList;

import org.apache.tools.ant.BuildException;

/**
 * Definition of an Ant task for launching a rakefile.
 * 
 * <code>
 *   <rubytl.m2m name="mytask" ruby="ruby" rubytl="/home/jesus/mdd/rubytl">
 *     <transformation path="mytransformation.rb" />
 *     <!-- There are two ways -->
 *     <source>
 *     		<model path="file1.xmi">
 *     		<model path="file2.xmi">
 *     		<namespace name="Source1" metamodel="source1.ecore">
 *          <namespace name="Source2" metamodel="source2.ecore">
 *     </source>
 *     <target model="file.xmi" metamodel="mm.ecore" namespace="MM" >
 *   </rubytl.m2m>
 * </code>
 * 
 * @author jesus
 */
public class M2MTask extends AbstractExtendedRubytlTask {
	protected TaskElements.PathElement transformationDefinition;
	protected TaskElements.PathElement trace;

	public TaskElements.PathElement createTransformation() {
		this.transformationDefinition= new TaskElements.PathElement();
		return this.transformationDefinition;
	}		

	public TaskElements.PathElement createTrace() {
		this.trace = new TaskElements.PathElement();
		return this.trace;
	}		

	protected void executeRubyTL(String projectPath, Configuration configuration) {
		try {			
			TaskM2MConfigurationData taskData = new TaskM2MConfigurationData(configuration.getRuby(), configuration.getRubytl(), projectPath, this.transformationDefinition.getPath());			
			configureModels(taskData);
			if ( this.trace != null ) {
				taskData.setTransformationTraceOutputPath(this.trace.getPath());
			}
			
			RtlLauncher launcher = new RtlLauncher(taskData, configuration.getModelConfiguration()); 
			launcher.execute("ant_task");
		} catch ( Exception e ) {
			e.printStackTrace();
			throw new BuildException(e);
		}		
	}

	protected void checkTaskValues() throws BuildException { }

	public class ModelDefinitionElement {
		protected LinkedList<TaskElements.PathElement> models = new LinkedList<TaskElements.PathElement>();
		protected LinkedList<TaskElements.NamespaceElement> namespaces = new LinkedList<TaskElements.NamespaceElement>();
		private String model;
		private String metamodel;
		private String namespace;
		
		public TaskElements.PathElement createModel() {
			TaskElements.PathElement element = new TaskElements.PathElement();
			models.add(element);
			return element;
		}

		public TaskElements.NamespaceElement createNamespace() {
			TaskElements.NamespaceElement element = new TaskElements.NamespaceElement();
			namespaces.add(element);
			return element;
		}

		public void setModel(String model)         { this.model     = model; }
		public void setMetamodel(String metamodel) { this.metamodel = metamodel; }
		public void setNamespace(String namespace) { this.namespace = namespace; }
		
		protected Binding getRubytlModelBinding() {
			LinkedList<TaskElements.PathElement> models = new LinkedList<TaskElements.PathElement>(this.models);
			LinkedList<TaskElements.NamespaceElement> namespaces = new LinkedList<TaskElements.NamespaceElement>(this.namespaces);
			if ( this.model != null && this.metamodel != null && this.namespace != null ) {
				TaskElements.PathElement element = new TaskElements.PathElement();
				element.setPath(this.model);
				models.add(element);

				TaskElements.NamespaceElement namespace = new TaskElements.NamespaceElement();
				namespace.setNamespace(this.namespace);
				namespace.setMetamodel(this.metamodel);
				namespaces.add(namespace);
			}
			
			Binding result = new Binding();
			for (TaskElements.PathElement m : models) {
				result.addModel(m.getPath());
			}			
			for (TaskElements.NamespaceElement n : namespaces) {
				result.addMetamodel(n.getNamespace(), n.getMetamodel());
			}
			return result;
		}
	}
}

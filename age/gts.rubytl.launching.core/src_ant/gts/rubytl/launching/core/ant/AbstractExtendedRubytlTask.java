package gts.rubytl.launching.core.ant;

import gts.rubytl.launching.core.ant.M2MTask.ModelDefinitionElement;
import gts.rubytl.launching.core.configuration.Binding;
import gts.rubytl.launching.core.configuration.data.AbstractTaskConfigurationData;

import java.util.LinkedList;

import org.apache.tools.ant.BuildException;

public abstract class AbstractExtendedRubytlTask extends AbstractRubytlTask {
	protected LinkedList<ModelDefinitionElement> sources = new LinkedList<ModelDefinitionElement>();
	protected LinkedList<ModelDefinitionElement> targets = new LinkedList<ModelDefinitionElement>();

	public ModelDefinitionElement createSource() {
		ModelDefinitionElement element = new ModelDefinitionElement();
		sources.add(element);
		return element;
	}	

	public ModelDefinitionElement createTarget() {
		ModelDefinitionElement element = new ModelDefinitionElement();
		targets.add(element);
		return element;
	}	

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

	protected void configureModels(AbstractTaskConfigurationData configuration) {
		for (ModelDefinitionElement model : this.sources) {
			configuration.addSourceBinding(model.getRubytlModelBinding());
		}
		for (ModelDefinitionElement model : this.targets) {
			configuration.addTargetBinding(model.getRubytlModelBinding());
		}
	}
	
	protected void checkTaskValues() throws BuildException {
		// TODO: Check
	}
}

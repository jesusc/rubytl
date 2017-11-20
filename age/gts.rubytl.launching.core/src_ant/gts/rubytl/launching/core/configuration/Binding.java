package gts.rubytl.launching.core.configuration;

import gts.rubytl.launching.core.Util;

import java.util.Collection;
import java.util.LinkedList;


/**
 * Represents a binding between a model (or more that one) and the
 * metamodels its conforms to. The metamodels will be considered by
 * the transformation definition.
 * 
 * <pre>
 * 		model     = 'struts-view.xmi'
 *      metamodel = { 'JSP'  => 'JSP.ecore', 
 *                    'Tags' => 'StrutsTags.ecore' 
 *                  } 
 * </pre>
 * 
 * @author jesus
 */
public class Binding {

	protected LinkedList<String> models            = new LinkedList<String>();
	protected LinkedList<NamespacePair> metamodels = new LinkedList<NamespacePair>();
	
	/**
	 * It adds a model.
	 * 
	 * @param model The path to the model to be added.
	 */
	public void addModel(String model) {
		models.add(model);
	}

	/**
	 * It adds a metamodel.
	 * 
	 * @param model The path to the metamodel to be added.
	 */
	public void addMetamodel(NamespacePair metamodel) {
		metamodels.add(metamodel);
	}
	
	/**
	 * It adds a metamodel.
	 * 
	 * @param model The path to the metamodel to be added.
	 */
	public void addMetamodel(String namespace, String metamodel) {
		metamodels.add(new NamespacePair(namespace, metamodel));
	}

	public Collection<String> getModels() {
		return new LinkedList<String>(models);
	}

	public Collection<NamespacePair> getMetamodels() {
		return new LinkedList<NamespacePair>(metamodels);
	}
	
	public static class NamespacePair {
		protected String namespace;
		protected String metamodel;
		
		public NamespacePair(String namespace, String metamodel) {
			this.namespace = namespace;
			this.metamodel = metamodel;
		}
		
		public String getNamespace() {
			return namespace;
		}
		
		public String getMetamodel() {
			return metamodel;
		}
		
		public static final Util.IMap<NamespacePair, String> mapNamespace = new Util.IMap<NamespacePair, String>() {
			public String map(NamespacePair t) { return t.getNamespace(); }
		};

		public static final Util.IMap<NamespacePair, String> mapMetamodel = new Util.IMap<NamespacePair, String>() {
			public String map(NamespacePair t) { return t.getMetamodel(); }
		};
		
	}
	
}


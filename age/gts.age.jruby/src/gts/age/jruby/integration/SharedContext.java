package gts.age.jruby.integration;

import java.util.HashMap;

/**
 * This class holds objects and helper methods to be shared
 * between Java and JRuby. Typically, there will be a global variable
 * called $context to be accessed from JRuby code.
 * 
 * @author jesus
 */
public class SharedContext {
	private HashMap<String, Object> elements;
	private JRubyEvaluator evaluator;
	private IntegrationFactory factory;
	
	public SharedContext(JRubyEvaluator evaluator) {
		this.elements = new HashMap<String, Object>();
		this.evaluator = evaluator;
	}
	
	public void addShared(String name, Object obj) {
		elements.put(name, obj);
	}
	
	public Object getShared(String name) {
		return elements.get(name);
	}

	public void setFactory(IntegrationFactory factory) {
		this.factory = factory;
		this.addShared("factory", factory);
	}
}

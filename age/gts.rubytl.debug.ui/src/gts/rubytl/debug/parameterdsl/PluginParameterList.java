package gts.rubytl.debug.parameterdsl;

import java.util.LinkedList;

import org.eclipse.swt.widgets.Composite;

/**
 * This class simply aggregates ParameterType objects that will
 * be represented as UI widgets.
 * 
 * @author jesus
 */
public class PluginParameterList {
	
	/** List of parameters handled by plugin */
	protected LinkedList<ParameterType> parameters = new LinkedList<ParameterType>();
	
	/**
	 * Add parameters to the list of parameters the plugin supports.
	 * @param p The new parameter.
	 */
	public void addParameter(ParameterType p) {
		parameters.add(p);
	}

	/**
	 * Adds widgets to the composite in order to allow the user to set
	 * the plugin parameters usin such widgets.
	 * 
	 * @param parent The composite where the widgets will be set.
	 */
	public void createUI(Composite parent) {
		for(ParameterType p : parameters) {
			p.createWidget(parent);
		}
		parent.redraw();
	}
}

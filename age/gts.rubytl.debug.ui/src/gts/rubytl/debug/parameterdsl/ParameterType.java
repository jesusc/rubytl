package gts.rubytl.debug.parameterdsl;

import org.eclipse.swt.widgets.Composite;

/**
 * This abstract class represents the interpretation of a 
 * DSL parameter from the UI point of view. Subclasses sets
 * the concrete ui representation of the parameter (a checklist,
 * a input box, etc.)
 * 
 * Each parameter should have associated an {@link InputFormater} in order
 * to extract the information set by the user and make such information
 * understandable by RubyTL (usually in the form of a String)
 * 
 * @author jesus
 */
public abstract class ParameterType {

	/** A formater for this object */ 
	protected InputFormater formater;
	
	/** The description of the parameter */
	protected String description;	
	
	/**
	 * Sets the formater object. A formater extracts data entered by the 
	 * user to format such data in so that is is understandable by RubyTL.
	 * 
	 * @param formater A formater, which is usually dependent of the subclass type.
	 */
	public void setFormater(InputFormater formater) {
		this.formater = formater;
	}	

	/** 
	 * Sets the description of this parameter.
	 * @param description The description string, null if no description exists.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Returns the description of the parameter.
	 * @return The description string, null if no description exists.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Creates the corresponding widget for the user to enter
	 * the plugin parameter. 
	 * 
	 * @param parent The parent object where to place the widget.
	 * @return The new widget.
	 */
	public abstract Composite createWidget(Composite parent);
	
}

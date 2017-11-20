package org.rubypeople.rdt.internal.ui.browsing;

import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.internal.ui.viewsupport.AppearanceAwareLabelProvider;
import org.rubypeople.rdt.internal.ui.viewsupport.RubyElementImageProvider;
import org.rubypeople.rdt.internal.ui.viewsupport.RubyUILabelProvider;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.RubyElementLabels;

public class TypesView extends RubyBrowsingPart {

	/**
	 * Answers if the given <code>element</code> is a valid input for this
	 * part.
	 * 
	 * @param element
	 *            the object to test
	 * @return <true> if the given element is a valid input
	 */
	protected boolean isValidInput(Object element) {
		return element instanceof IRubyProject;
	}

	protected String getLinkToEditorKey() {
		return PreferenceConstants.LINK_BROWSING_TYPES_TO_EDITOR;
	}
	
	protected RubyUILabelProvider createLabelProvider() {
		return new AppearanceAwareLabelProvider(
				AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS | RubyElementLabels.T_POST_QUALIFIED,
				AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS
						| RubyElementImageProvider.SMALL_ICONS);
	}
	
	/**
	 * Finds the element which has to be selected in this part.
	 * 
	 * @param je
	 *            the Ruby element which has the focus
	 */
	protected IRubyElement findElementToSelect(IRubyElement je) {
		if (je == null)
			return null;

		switch (je.getElementType()) {
		case IRubyElement.TYPE:
			IType type = ((IType) je).getDeclaringType();
			if (type == null)
				type = (IType) je;
			return getSuitableRubyElement(type);
		case IRubyElement.SCRIPT:
			return getTypeForRubyScript((IRubyScript) je);
		case IRubyElement.IMPORT_CONTAINER:
		case IRubyElement.IMPORT_DECLARATION:
			return findElementToSelect(je.getParent());
		default:
			if (je instanceof IMember)
				return findElementToSelect(((IMember) je).getDeclaringType());
			return null;

		}
	}
	
	/**
	 * Answers if the given <code>element</code> is a valid
	 * element for this part.
	 *
	 * @param 	element	the object to test
	 * @return	<true> if the given element is a valid element
	 */
	protected boolean isValidElement(Object element) {
		if (element instanceof IRubyScript)
			return super.isValidElement(((IRubyScript)element).getParent());
		else if (element instanceof IType) {
			IType type= (IType)element;
			return type.getDeclaringType() == null && isValidElement(type.getRubyScript());
		}
		return false;
	}

}

package org.rubypeople.rdt.ui.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.ui.RubyElementLabelProvider;

public class RubyTypeSelectionDialog extends ElementListSelectionDialog {

	public RubyTypeSelectionDialog(Shell parent) {
		super(parent, new RubyElementLabelProvider(
				RubyElementLabelProvider.SHOW_DEFAULT
						| RubyElementLabelProvider.SHOW_POST_QUALIFIED));
		setElements(getElements());
		setMultipleSelection(false);
	}

	/**
	 * Meant for subclasses to override what elements to show
	 * 
	 * @return
	 */
	protected Object[] getElements() {
		// FIXME This doesn't pick up all the classes in loadpaths (Ruby
		// library)! We need to search through rubProject.getSourceFolders to do
		// that.
		// FIXME If we move to grabbing all types via source fodlers it gets way
		// too slow. We need to make this set up liek JDT so it runs a search
		// job based on input and caches results.
		return getAllTypes();
	}

	/**
	 * @return
	 */
	private IRubyElement[] getAllTypes() {
		List typeList = new ArrayList();
		IProject[] projects = RubyCore.getRubyProjects();
		for (int i = 0; i < projects.length; i++) {
			IRubyElement[] types = findElements(projects[i], IRubyElement.TYPE);
			typeList.addAll(Arrays.asList(types));
		}
		IRubyElement[] allTypes = new IRubyElement[typeList.size()];
		System.arraycopy(typeList.toArray(), 0, allTypes, 0, allTypes.length);
		return allTypes;
	}

	public static IRubyElement[] findElements(final IRubyElement element, int elementType) {
		List<IRubyElement> allElements = new ArrayList<IRubyElement>();
		if (element.isType(elementType))
			allElements.add(element);
		if (element instanceof IParent) {
			IParent parent = (IParent) element;
			IRubyElement[] fileElements = findElements(element, elementType);
			for (int j = 0; j < fileElements.length; j++) {
				allElements.add(fileElements[j]);
			}
		}
		IRubyElement[] elements = new IRubyElement[allElements.size()];
		elements = allElements.toArray(new IRubyElement[allElements.size()]);
		return elements;
	}

	/**
	 * @param rubyProject
	 * @param elementType
	 */
	public static IRubyElement[] findElements(IProject rubyProject,
			int elementType) {
		if (rubyProject == null) {
			return new IRubyElement[0];
		}
		IRubyProject proj = RubyCore.create(rubyProject);
		return findElements(proj, elementType);
	}

}

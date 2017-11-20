package gts.rubytl.debug.parameterdsl;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;


/**
 * A checklist represents a set of elements, identified by
 * an string (the ID), which can be selected to be either
 * switched on/off.
 * 
 * @author jesus
 */
public class ChecklistType extends ParameterType {

	/** This is the corresponding widget */
	protected CheckboxTableViewer viewer;
	
	/** A map of ids - on/off that will be used to fill the widget */
	protected HashMap<String, Boolean> ids;
	
	public ChecklistType() {
		ids = new HashMap<String, Boolean>();
	}
	
	/**
	 * Adds a new element (id + on/off) to the list of elements
	 * to be shown by the checklist.
	 * 
	 * @param id The id of the element.
	 * @param onOff true is the initial value is "checked"
	 */
	public void addElement(String id, boolean onOff) {
		System.out.println(id + " - " + onOff );
		ids.put(id, onOff);
	}

	/**
	 * Create a new Table widget which checkboxes to enable and 
	 * disable elements.
	 * 
	 * @param parent The parent widget.
	 * @return A new Table widget.
	 */
	public Composite createWidget(Composite parent) {
		Table table = createTable(parent);
		CheckboxTableViewer viewer = createTableViewer(table);
		
		Set<Entry<String, Boolean>> entries = ids.entrySet();
		for (Entry<String, Boolean> entry : entries) {
			CheckedId c = new CheckedId(entry.getKey(), entry.getValue());
			viewer.add(c);
			viewer.setChecked(c, c.checked);
		}
		
		return table;
	}
	
	protected Table createTable(Composite composite) {
		Table table = new Table(composite, SWT.CHECK | SWT.BORDER | SWT.FULL_SELECTION);

		GridData data = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(data);
		table.setHeaderVisible(true);
		table.setLinesVisible(false);

		TableColumn column = new TableColumn(table, SWT.NULL);
		column.setText(this.getDescription());
		column.setWidth(125);

		return table;
	}	

	protected CheckboxTableViewer createTableViewer(Table table) {
		CheckboxTableViewer tableViewer = new CheckboxTableViewer(table);
		tableViewer.setLabelProvider(new LabelProvider());
		
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent evt) {
				// TODO: 
			}
		});

		tableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				// TODO:
			}
		});
		
		return tableViewer;
	}	
	
	private class LabelProvider implements ITableLabelProvider {
		
		public Image getColumnImage(Object element, int columnIndex) { return null; }

		public String getColumnText(Object element, int columnIndex) {
			CheckedId c = (CheckedId) element;
			switch(columnIndex) {
			case 0: return c.id; 
			default: throw new RuntimeException("Too much columns");
			}
		}

		public void addListener(ILabelProviderListener listener) {	}
		public void dispose() {	}
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}
		public void removeListener(ILabelProviderListener listener) { }			
	}
	
	/**
	 * Helper class.
	 */
	class CheckedId {
		String id;
		boolean checked;

		public CheckedId(String id, boolean checked) {
			this.id = id;
			this.checked = checked;
		}
	}
	
}

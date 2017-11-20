package gts.rubytl.debug.ui;

import gts.age.jruby.integration.JRubyEvaluator;
import gts.rubytl.debug.parameterdsl.ParameterFactory;
import gts.rubytl.debug.parameterdsl.PluginParameterList;

import java.io.InputStream;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Text;

/**
 * This tab page contains widget (dynamically generated) to
 * pass parameters to a plugin.
 * 
 * @author jesus
 */
public class PluginParametersTab extends AbstractLaunchConfigurationTab {

	private Font parentFont;
	private Composite bar;

	public void createControl(Composite parent) {
		parentFont = parent.getFont();
		Composite comp = createPageRoot(parent);
		this.bar = new Composite(comp, SWT.NONE);
		//this.bar = new ExpandBar(comp, SWT.V_SCROLL);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub
		
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub
		
	}

	public String getName() {
		return "Plugin options";
	}

    protected Composite createPageRoot(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        composite.setLayout(layout);

        setControl(composite);
        return composite;
    }

	public Composite createNewPluginSpace() {
		/**
		Composite composite = new Composite (bar, SWT.NONE);
		GridLayout layout = new GridLayout ();
		layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 10;
		layout.verticalSpacing = 10;
		composite.setLayout(layout);		

		ExpandItem item0 = new ExpandItem (bar, SWT.NONE, 0);
		item0.setText("Plugin options");
		item0.setHeight(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item0.setControl(composite);
		//item0.setImage(image);
		*/
		//Composite composite = new Composite (bar, SWT.NONE);		
		//return composite;
		bar.setLayout(new GridLayout());
		GridData data = new GridData(GridData.FILL_BOTH);
		bar.setLayoutData(data);
		return this.bar;
	}
}

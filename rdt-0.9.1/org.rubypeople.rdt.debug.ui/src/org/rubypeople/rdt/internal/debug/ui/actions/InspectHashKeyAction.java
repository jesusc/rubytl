package org.rubypeople.rdt.internal.debug.ui.actions;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.rubypeople.rdt.internal.debug.core.model.RubyExpression;
import org.rubypeople.rdt.internal.debug.core.model.RubyProcessingException;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;

public class InspectHashKeyAction extends AbstractInspectAction implements IViewActionDelegate {

    public void run(IAction arg0) {
        if (!(this.selection instanceof StructuredSelection)) {
            return;
        }
        Object selectedObject = ((StructuredSelection) this.selection).getFirstElement();
        if (selectedObject == null) {
            return;
        }
        if (!(selectedObject instanceof RubyVariable)) {
            return;
        }
		final RubyVariable var = ((RubyVariable) selectedObject) ;
        Display.getCurrent().asyncExec(new Runnable() {
            public void run() {
                
                	
                	String hashId = var.getParent().getObjectId() ;
                	String valueId = var.getObjectId() ;
                	String expression = "ObjectSpace._id2ref(" + hashId  + ").index(ObjectSpace._id2ref("+valueId+"))" ;
				try {
                    RubyVariable rubyVariable = var.getStackFrame().getRubyDebuggerProxy().readInspectExpression(var.getStackFrame(), expression);
                    showExpressionView();
                    DebugPlugin.getDefault().getExpressionManager().addExpression(new RubyExpression("key", rubyVariable));
                } catch (RubyProcessingException e) {
                    MessageDialog.openInformation(page.getActivePart().getSite().getShell(), e.getRubyExceptionType(), "Could not inspect '" + expression + "': " + e.getMessage());
                }
            }
        });

    }

}

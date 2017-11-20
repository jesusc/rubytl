package org.rubypeople.rdt.internal.debug.ui;

import java.util.Hashtable;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.rubypeople.rdt.debug.core.RubyLineBreakpoint;
import org.rubypeople.rdt.internal.debug.core.RubyExceptionBreakpoint;
import org.rubypeople.rdt.internal.debug.core.model.RubyThread;
import org.rubypeople.rdt.internal.debug.core.model.RubyValue;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;

public class DebugModelPresentation extends LabelProvider implements IDebugModelPresentation {
    private static Hashtable imageCache = new Hashtable();
	private Boolean isShowTypes  ;
	public String getText(Object item) {
		if (item instanceof RubyLineBreakpoint) {
			RubyLineBreakpoint breakpoint = (RubyLineBreakpoint) item;
			try {
				return breakpoint.getFileName() + ":" + breakpoint.getLineNumber();
			} catch (CoreException e) {
				DebugUIPlugin.log(e) ;
				return "--";
			}
		}
		if (item instanceof RubyExceptionBreakpoint) {
			RubyExceptionBreakpoint exceptionBreakpoint = (RubyExceptionBreakpoint) item ;
			try {
				if (exceptionBreakpoint.getException() == null || exceptionBreakpoint.getException().length() ==0) {
					return "No Catchpoint defined" ;
				}
				else {
					return "Suspend on exception: " + exceptionBreakpoint.getException() ;
				}
			} catch (CoreException e) {
				DebugUIPlugin.log(e) ;
			}
		}
		if (item instanceof RubyVariable) {
			RubyVariable variable = (RubyVariable) item ; 
			if (isShowTypes == Boolean.TRUE) {				
				return ((RubyValue) variable.getValue()).getReferenceTypeName() + " " + variable.toString() ;	
			}
			else {
				return variable.toString() ;					
			}
		}
		return DebugUIPlugin.getDefaultLabelProvider().getText(item) ;
	}

	protected IBreakpoint getBreakpoint(IMarker marker) {
		return DebugPlugin.getDefault().getBreakpointManager().getBreakpoint(marker);
	}

	public void computeDetail(IValue value, IValueDetailListener listener) {
	}

	public void setAttribute(String attribute, Object value) {
		if (attribute.equals(IDebugModelPresentation.DISPLAY_VARIABLE_TYPE_NAMES)) {
			this.isShowTypes = (Boolean) value ;	
		}
	}

	public String getEditorId(IEditorInput input, Object element) {
		System.out.println("getEditorId.");
		return "X";
	}

	public IEditorInput getEditorInput(Object element) {
		return null;
	}
	
	private Image getImage(ImageDescriptor imageDescriptor) {
		Image image = (Image) imageCache.get(imageDescriptor);
		if (image == null) {
			image = imageDescriptor.createImage();
			imageCache.put(imageDescriptor, image);
		}
	    return image ;
	}

	public Image getImage(Object item) {
		ImageDescriptor descriptor;
		if (item instanceof IMarker || item instanceof RubyLineBreakpoint) {
			descriptor = DebugUITools.getImageDescriptor(IDebugUIConstants.IMG_OBJS_BREAKPOINT);
		} else if (item instanceof RubyThread) {
			RubyThread thread = (RubyThread) item;
			if (thread.isSuspended()) {
				descriptor = DebugUITools.getImageDescriptor(IDebugUIConstants.IMG_OBJS_THREAD_SUSPENDED);
			} else if (thread.isTerminated()) {
				descriptor = DebugUITools.getImageDescriptor(IDebugUIConstants.IMG_OBJS_THREAD_TERMINATED);
			} else {
				descriptor = DebugUITools.getImageDescriptor(IDebugUIConstants.IMG_OBJS_THREAD_RUNNING);
			}
		} else {
			descriptor = DebugUITools.getDefaultImageDescriptor(item);
		}
		return getImage(descriptor) ;
	}

}
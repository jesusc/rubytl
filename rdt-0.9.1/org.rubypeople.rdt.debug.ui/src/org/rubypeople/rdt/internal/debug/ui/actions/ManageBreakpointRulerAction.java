/*
 *(c) Copyright QNX Software Systems Ltd. 2002.
 * All Rights Reserved.
 * 
 */
package org.rubypeople.rdt.internal.debug.ui.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IUpdate;
import org.rubypeople.rdt.debug.core.RubyLineBreakpoint;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.ExternalFileRubyAnnotationModel;
import org.rubypeople.rdt.internal.ui.rubyeditor.IRubyScriptEditorInput;

/**
 * 
 * Enter type comment.
 * 
 * @since Aug 23, 2002
 */
public class ManageBreakpointRulerAction extends Action implements IUpdate {

	private IVerticalRulerInfo fRuler;
	private ITextEditor fTextEditor;
	private List fMarkers;

	private String fAddLabel;
	private String fRemoveLabel;

	/**
	 * Constructor for ManageBreakpointRulerAction.
	 * 
	 * @param ruler
	 * @param editor
	 */
	public ManageBreakpointRulerAction(IVerticalRulerInfo ruler, ITextEditor editor) {
		fRuler = ruler;
		fTextEditor = editor;
		fAddLabel = "Add Breakpoint";
		fRemoveLabel = "Remove Breakpoint";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.IUpdate#update()
	 */
	public void update() {
		fMarkers = getBreakpoints();
		setText(fMarkers.isEmpty() ? fAddLabel : fRemoveLabel);
	}

	/**
	 * @see Action#run()
	 */
	public void run() {
		if (fMarkers.isEmpty()) {
			addMarker();
		} else {
			removeMarkers(fMarkers);
		}
	}

	protected List getBreakpoints() {

		List breakpoints = new ArrayList();

		AbstractMarkerAnnotationModel model = getAnnotationModel();
		if (model == null) { return breakpoints; }
		try {
			IResource resource = getResource();
			if (resource == null) resource = ResourcesPlugin.getWorkspace().getRoot();			
			if (resource == null) return breakpoints;
			IMarker[] markers = resource.findMarkers(IBreakpoint.BREAKPOINT_MARKER,
					true, IResource.DEPTH_INFINITE);
			if (markers.length < 1) return breakpoints;

			IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
			IDocument document = getDocument();
			for (int i = 0; i < markers.length; i++) {
				IBreakpoint breakpoint = breakpointManager.getBreakpoint(markers[i]);
				if (breakpoint != null && breakpointManager.isRegistered(breakpoint) && includesRulerLine(model, markers[i], document)) {
					breakpoints.add(breakpoint);
				}
			}

		} catch (CoreException x) {
			RdtDebugUiPlugin.log(x.getStatus());
		}
		return breakpoints;
	}

	/**
	 * Returns the resource for which to create the marker, or <code>null</code>
	 * if there is no applicable resource.
	 * 
	 * @return the resource for which to create the marker or <code>null</code>
	 */
	protected IResource getResource() {
		IEditorInput input = fTextEditor.getEditorInput();
		IResource resource = (IResource) input.getAdapter(IFile.class);
		if (resource == null) resource = (IResource) input.getAdapter(IResource.class);
		return resource;
	}

	/**
	 * Checks whether a position includes the ruler's line of activity.
	 * 
	 * @param position
	 *            the position to be checked
	 * @param document
	 *            the document the position refers to
	 * @return <code>true</code> if the line is included by the given position
	 */
	protected boolean includesRulerLine(AbstractMarkerAnnotationModel model, IMarker marker, IDocument document) {
		if (model instanceof ExternalFileRubyAnnotationModel) {
			try {
				Integer lineNum = (Integer) marker.getAttribute(IMarker.LINE_NUMBER);
				int line = fRuler.getLineOfLastMouseButtonActivity();
				if (line == (lineNum - 1)) { return true; }
			} catch (CoreException e) {
				RdtDebugUiPlugin.log(e);
			}
		} else {
			Position position = model.getMarkerPosition(marker);
			if (position != null) {
				try {
					int markerLine = document.getLineOfOffset(position.getOffset());
					int line = fRuler.getLineOfLastMouseButtonActivity();
					if (line == markerLine) { return true; }
				} catch (BadLocationException x) {}
			}
		}
		return false;
	}

	/**
	 * Returns this action's vertical ruler info.
	 * 
	 * @return this action's vertical ruler
	 */
	protected IVerticalRulerInfo getVerticalRulerInfo() {
		return fRuler;
	}

	/**
	 * Returns this action's editor.
	 * 
	 * @return this action's editor
	 */
	protected ITextEditor getTextEditor() {
		return fTextEditor;
	}

	/**
	 * Returns the <code>AbstractMarkerAnnotationModel</code> of the editor's
	 * input.
	 * 
	 * @return the marker annotation model
	 */
	protected AbstractMarkerAnnotationModel getAnnotationModel() {
		IDocumentProvider provider = fTextEditor.getDocumentProvider();
		IAnnotationModel model = provider.getAnnotationModel(fTextEditor.getEditorInput());
		if (model instanceof AbstractMarkerAnnotationModel) { return (AbstractMarkerAnnotationModel) model; }
		return null;
	}

	/**
	 * Returns the <code>IDocument</code> of the editor's input.
	 * 
	 * @return the document of the editor's input
	 */
	protected IDocument getDocument() {
		IDocumentProvider provider = fTextEditor.getDocumentProvider();
		return provider.getDocument(fTextEditor.getEditorInput());
	}

	protected void addMarker() {
		IEditorInput editorInput = getTextEditor().getEditorInput();
		getDocument();
		int rulerLine = getVerticalRulerInfo().getLineOfLastMouseButtonActivity();
		try {
			IResource resource = getResource(editorInput);
			if (resource.equals(ResourcesPlugin.getWorkspace().getRoot())) {
				RubyLineBreakpoint bp = new RubyLineBreakpoint(resource, rulerLine, getFileName(editorInput));
			} else {
				RubyLineBreakpoint bp = new RubyLineBreakpoint(resource, rulerLine);
			}
		} catch (CoreException x) {
			System.out.println(x.getMessage());
		}
	}

	private String getFileName(IEditorInput editorInput) {
		if (editorInput instanceof IRubyScriptEditorInput)
			return ((IRubyScriptEditorInput) editorInput).getRubyScript().getPath().makeAbsolute().toOSString();
		return null;
	}

	private IResource getResource(IEditorInput editorInput) {
		if (editorInput instanceof IFileEditorInput)
			return ((IFileEditorInput) editorInput).getFile();
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	protected void removeMarkers(List markers) {

		Iterator breakpointIt = fMarkers.iterator();
		while (breakpointIt.hasNext()) {
			IBreakpoint breakpoint = (IBreakpoint) breakpointIt.next();

			try {
				DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint(breakpoint, true);
				return;
			} catch (CoreException e) {}

		}
	}
}

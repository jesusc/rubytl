/*******************************************************************************
 * Copyright (c) 2006 Institute for Software, HSR Hochschule fr Technik  
 * Rapperswil, University of applied sciences
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 * 
 * Contributors: 
 * Emanuel Graf & Leo Buettiker - initial API and implementation
 * Mirko Stocker, Adapted to RDT
 ******************************************************************************/

package org.rubypeople.rdt.refactoring.preview;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareViewerPane;
import org.eclipse.compare.IEncodedStreamContentAccessor;
import org.eclipse.compare.IResourceProvider;
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.contentmergeviewer.IMergeViewerContentProvider;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.ui.refactoring.ChangePreviewViewerInput;
import org.eclipse.ltk.ui.refactoring.IChangePreviewViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.rubypeople.rdt.internal.ui.compare.RubyMergeViewer;
import org.rubypeople.rdt.ui.RubyElementLabelProvider;


public class RubyTextEditChangePreviewViewer implements IChangePreviewViewer {
	
	private RubyMergeViewer viewer;
	private CompareViewerPane viewerPane;
	private RubyTextEditChangePreviewViewerContentProvider textEditChangeContentProvider;

	private static class RubyTextEditChangePreviewViewerContentProvider implements IMergeViewerContentProvider {

		public Object getAncestorContent(Object input) {
			if (input instanceof ICompareInput)
				return ((ICompareInput) input).getAncestor();
			return null;
		}

		public Image getAncestorImage(Object input) {
			if (input instanceof ICompareInput) {
				ITypedElement ancestor = ((ICompareInput) input).getAncestor();
				if(ancestor != null) {
					return ancestor.getImage();
				}
			}
			return null;
		}

		public String getAncestorLabel(Object input) {
			if (input instanceof ICompareInput) {
				ITypedElement ancestor = ((ICompareInput) input).getAncestor();
				if(ancestor != null) {
					return ancestor.getName();
				}
			}
			return null;
		}

		public Object getLeftContent(Object input) {
			if (input instanceof ICompareInput)
				return ((ICompareInput) input).getLeft();
			return null;
		}

		public Image getLeftImage(Object input) {
			if (input instanceof ICompareInput)
				return ((ICompareInput) input).getLeft().getImage();
			return null;
		}

		public String getLeftLabel(Object input) {
			return Messages.RubyTextEditChangePreviewViewer_OriginalSource;
		}

		public Object getRightContent(Object input) {
			if (input instanceof ICompareInput)
				return ((ICompareInput) input).getRight();
			return null;
		}

		public Image getRightImage(Object input) {
			if (input instanceof ICompareInput)
				return ((ICompareInput) input).getRight().getImage();
			return null;
		}

		public String getRightLabel(Object input) {
			return Messages.RubyTextEditChangePreviewViewer_RefactoredSource;
		}

		public boolean isLeftEditable(Object input) {
			return false;
		}

		public boolean isRightEditable(Object input) {
			return false;
		}

		public void saveLeftContent(Object input, byte[] bytes) {
			//No Edits
		}

		public void saveRightContent(Object input, byte[] bytes) {
			//No Edits			
		}

		public boolean showAncestor(Object input) {
			//no Ancestor
			return false;
		}

		public void dispose() {
			//
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			//Nothing to do
		}
		
	}
	
	private static class CompareElement implements ITypedElement, IEncodedStreamContentAccessor, IResourceProvider {
		private static final String ENCODING= "UTF-8"; //$NON-NLS-1$
		private String fContent;
		private String fType;
		private IResource fResource;
		public CompareElement(String content, String type, IResource resource) {
			fContent= content;
			fType= type;
			fResource= resource;
		}
		public String getName() {
			return ""; //$NON-NLS-1$
		}
		public Image getImage() {
			return null;
		}
		public String getType() {
			return fType;
		}
		public InputStream getContents() throws CoreException {
			try {
				return new ByteArrayInputStream(fContent.getBytes(ENCODING));
			} catch (UnsupportedEncodingException e) {
				return new ByteArrayInputStream(fContent.getBytes());
			}
		}
		public String getCharset() {
			return ENCODING;
		}
		public IResource getResource() {
			return fResource;
		}
	}

	public void createControl(Composite parent) {
		CompareConfiguration compConfig = new CompareConfiguration();
		compConfig.setLeftEditable(false);
		compConfig.setRightEditable(false);
		viewerPane = new CompareViewerPane(parent, SWT.BORDER | SWT.FLAT);
		viewer =  new RubyMergeViewer(viewerPane,SWT.MULTI | SWT.FULL_SELECTION, compConfig);
		textEditChangeContentProvider = new RubyTextEditChangePreviewViewerContentProvider();
		viewer.setContentProvider(textEditChangeContentProvider);
		viewerPane.setContent(viewer.getControl());
	}

	public Control getControl() {
		return viewerPane;
	}

	public void setInput(ChangePreviewViewerInput input) {
		try {
			Change change= input.getChange();
			if (change instanceof RubyTextFileChange) {
				RubyTextFileChange editChange = (RubyTextFileChange) change;
				setInput(editChange, editChange.getCurrentContent(new NullProgressMonitor()), editChange.getPreviewContent(new NullProgressMonitor()), editChange.getTextType());
				return;
			} 
			viewer.setInput(null);
		} catch (CoreException e) {
			viewer.setInput(null);
		}
	}

	private void setInput(RubyTextFileChange change, String left, String right, String type) {
		IFile resource = change.getFile();
		viewerPane.setText(resource.getName());
		viewerPane.setImage(new RubyElementLabelProvider().getImage(resource));
		viewer.setInput(new DiffNode( 
			new CompareElement(left, type, resource), 
			new CompareElement(right, type, resource)));
	}

}

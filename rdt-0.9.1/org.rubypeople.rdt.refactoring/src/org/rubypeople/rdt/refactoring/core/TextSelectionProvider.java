/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2006 Lukas Felber <lfelber@hsr.ch>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/

package org.rubypeople.rdt.refactoring.core;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.Member;
import org.rubypeople.rdt.internal.core.MemberElementInfo;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;

public class TextSelectionProvider {

	private String activeDocument;
	private int start;
	private int end;
	private int caret;
	

	@SuppressWarnings("restriction") //$NON-NLS-1$
	public TextSelectionProvider(IAction action) {
		
		if (action == null || action instanceof org.eclipse.ui.internal.EditorPluginAction) {
			initEditor();
		} else {
			initOutline(action);
		}
	}
	
	private void initEditor() {
		RubyEditor editor = (RubyEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		ITextSelection selection = (ITextSelection) editor.getSelectionProvider().getSelection();
		start = selection.getOffset();
		end = start + selection.getLength();
		if (end > start) {
			end--;
		}
		caret = editor.getCaretPosition().getOffset();
	}

	@SuppressWarnings("restriction") //$NON-NLS-1$
	private void initOutline(IAction action) {
		TreeSelection selection = (TreeSelection) ((org.eclipse.ui.internal.PluginAction)action).getSelection();
		Member member = (Member) selection.toArray()[0];
		try {
			MemberElementInfo info = (MemberElementInfo) member.getElementInfo();
			start = info.getNameSourceStart();
			end = info.getNameSourceEnd()+1;
			caret = start;
		} catch (RubyModelException e) {
			e.printStackTrace();
		}
	}

	public SelectionInformation getSelectionInformation() {
		if(activeDocument == null) {
			RubyEditor editor = (RubyEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			activeDocument = editor.getViewer().getDocument().get();
		}
		return new SelectionInformation(getStartOffset(), getEndOffset(), activeDocument);
	}
	
	public int getCarretPosition() {
		return caret;
	}
	
	public int getStartOffset() {
		return start;
	}
	
	public int getEndOffset() {
		return end;
	}
}

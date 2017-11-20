/*******************************************************************************
 * Copyright (c) 2005 RadRails.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package com.aptana.rdt.internal.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

import com.aptana.rdt.AptanaRDTPlugin;
import com.aptana.rdt.core.gems.Gem;
import com.aptana.rdt.core.gems.GemListener;
import com.aptana.rdt.ui.gems.InstallGemDialog;

/**
 * Install a gem
 * 
 * @author cwilliams
 */
public class InstallGemActionDelegate implements IObjectActionDelegate, IViewActionDelegate, GemListener {
	
	private IAction action;

	public InstallGemActionDelegate() {
		AptanaRDTPlugin.getDefault().getGemManager().addGemListener(this);
	}
	
	/**
	 * @see org.eclipse.ui.IObjectActionDelegate#setActivePart(org.eclipse.jface.action.IAction,
	 *      org.eclipse.ui.IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.action = action;
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		InstallGemDialog dialog = new InstallGemDialog(Display.getCurrent().getActiveShell());
		if (dialog.open() == Dialog.OK) {
			final Gem gem = dialog.getGem();
			Display.getDefault().asyncExec(new Runnable() {			
				public void run() {
					AptanaRDTPlugin.getDefault().getGemManager().installGem(gem);			
				}			
			});			
		}		
	}

	/**
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction,
	 *      org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.action = action;
		action.setEnabled(isEnabled());
	}

	private boolean isEnabled() {
		if (!AptanaRDTPlugin.getDefault().getGemManager().isInitialized()) return false;
		return !AptanaRDTPlugin.getDefault().getGemManager().getRemoteGems().isEmpty();
	}

	/**
	 * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
	 */
	public void init(IViewPart view) {
	}

	public void gemAdded(Gem gem) {		
	}

	public void gemRemoved(Gem gem) {		
	}

	public void gemsRefreshed() {			
	}

	public void managerInitialized() {
		if (action == null) return;
		action.setEnabled(isEnabled());			
	}
	
	

}

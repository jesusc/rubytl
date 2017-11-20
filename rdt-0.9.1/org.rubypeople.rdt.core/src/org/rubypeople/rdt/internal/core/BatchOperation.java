/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.core;

import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.IRubyModelStatus;
import org.rubypeople.rdt.core.RubyModelException;

/**
 * An operation created as a result of a call to RubyCore.run(IWorkspaceRunnable, IProgressMonitor)
 * that encapsulates a user defined IWorkspaceRunnable.
 */
public class BatchOperation implements IWorkspaceRunnable {
	// FIXME Extend from RubyModelOperation!
	protected IWorkspaceRunnable runnable;
	private IProgressMonitor progressMonitor;
	public BatchOperation(IWorkspaceRunnable runnable) {
		this.runnable = runnable;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.core.RubyModelOperation#executeOperation()
	 */
	protected void executeOperation() throws RubyModelException {
		try {
			this.runnable.run(this.progressMonitor);
		} catch (CoreException ce) {
			if (ce instanceof RubyModelException) {
				throw (RubyModelException)ce;
			}
			if (ce.getStatus().getCode() == IResourceStatus.OPERATION_FAILED) {
				Throwable e= ce.getStatus().getException();
				if (e instanceof RubyModelException) {
					throw (RubyModelException) e;
				}
			}
			throw new RubyModelException(ce);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.core.RubyModelOperation#verify()
	 */
	protected IRubyModelStatus verify() {
		// cannot verify user defined operation
		return RubyModelStatus.VERIFIED_OK;
	}

	public void run(IProgressMonitor monitor) throws RubyModelException {
		this.progressMonitor = monitor;		
		executeOperation();
	}

	
}

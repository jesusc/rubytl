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
package org.rubypeople.rdt.internal.ui.infoviews;

import org.eclipse.osgi.util.NLS;

/**
 * 
 */
class InfoViewMessages extends NLS {

	private static final String BUNDLE_NAME= InfoViewMessages.class.getName();
	
	public static String RubyInformation_ri_not_found;
	public static String RubyInformation_please_wait;
	public static String RubyInformation_refresh;
	public static String RubyInformation_refresh_tooltip;
	public static String RubyInformation_update_job_title;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, InfoViewMessages.class);
	}
}

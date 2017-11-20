/**
 * Copyright (c) 2007 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl -v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.rubypeople.rdt.refactoring.core.extractconstant;

import java.util.ArrayList;
import java.util.Collection;

import org.rubypeople.rdt.refactoring.editprovider.EditProvider;
import org.rubypeople.rdt.refactoring.editprovider.MultiEditProvider;

public class ConstantExtractor extends MultiEditProvider {

	private ExtractConstantConfig config;	

	public ConstantExtractor(ExtractConstantConfig config) {
		this.config = config;
	}

	protected Collection<EditProvider> getEditProviders() {
		Collection<EditProvider> providers = new ArrayList<EditProvider>();

		providers.add(new ExtractedConstantCall(config));
		providers.add(new ExtractedConstantDef(config));

		return providers;
	}

	public EditProvider getDefEdit() {
		return new ExtractedConstantDef(config);
	}

	public void setConstantName(String name) {
		config.setConstantName(name);
	}

	public String getConstantName() {
		return config.getConstantName();
	}
}

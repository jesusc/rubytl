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

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.rubypeople.rdt.internal.refactoring.RefactoringMessages;
import org.rubypeople.rdt.refactoring.core.RubyRefactoring;
import org.rubypeople.rdt.refactoring.core.TextSelectionProvider;
import org.rubypeople.rdt.refactoring.ui.pages.ExtractConstantPage;

public class ExtractConstantRefactoring extends RubyRefactoring {

	public static final String NAME = RefactoringMessages.ExtractConstantAction_extract_constant;
		
	public ExtractConstantRefactoring(TextSelectionProvider selectionProvider) {
		super(NAME);
		ExtractConstantConfig config = new ExtractConstantConfig(getDocumentProvider(), selectionProvider.getSelectionInformation());
		ExtractConstantConditionChecker checker = new ExtractConstantConditionChecker(config);
		
		setRefactoringConditionChecker(checker);
		if(checker.shouldPerform()) {
			ConstantExtractor methodExtractor = new ConstantExtractor(config);
			setEditProvider(methodExtractor);
			UserInputWizardPage page = new ExtractConstantPage(methodExtractor);
			pages.add(page);
		}
	}
}

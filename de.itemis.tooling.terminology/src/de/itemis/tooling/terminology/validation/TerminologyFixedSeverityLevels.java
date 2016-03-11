/*******************************************************************************
 * Copyright (c) 2013 itemis AG (http://www.itemis.eu).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package de.itemis.tooling.terminology.validation;

import org.eclipse.xtext.diagnostics.Severity;

public class TerminologyFixedSeverityLevels implements
		TerminologyValidationSeverityLevels {

	private static boolean checkMissingPreferredTermForLanguageWithoutTerms=true;

	public Severity getOnePreferredTermPerLanguageLevel() {
		return Severity.WARNING;
	}
	public Severity getUniqueEntryIdLevel() {
		return Severity.ERROR;
	}
	public Severity getUniqueTermLevel() {
		return Severity.WARNING;
	}
	public Severity getMissingPreferredTermLevel() {
		return Severity.WARNING;
	}
	public Severity getMissingDefinitionLevel() {
		return null;
	}
	public Severity getEntryRefSymmetricLevel() {
		return null;
	}
	public boolean checkMissingPreferredTermForLanguageWithoutTerms() {
		return checkMissingPreferredTermForLanguageWithoutTerms;
	}
}

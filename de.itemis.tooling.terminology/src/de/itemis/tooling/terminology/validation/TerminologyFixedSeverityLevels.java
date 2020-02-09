/*******************************************************************************
 * Copyright (C) 2013-2020 itemis AG (http://www.itemis.eu).
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Alexander Nittka (alex@nittka.de) - initial implementation
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

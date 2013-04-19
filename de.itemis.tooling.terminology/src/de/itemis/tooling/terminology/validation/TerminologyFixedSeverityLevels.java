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

	public Severity getOnePreferredTermPerLanguageLevel() {
		return Severity.WARNING;
	}
	public Severity getUniqueEntryIdLevel() {
		return Severity.ERROR;
	}
	public Severity getUniqueTermLevel() {
		return Severity.WARNING;
	}
	public Severity getMissingPreferredTerm() {
		return Severity.WARNING;
	}
	public Severity getMissingDefinition() {
		return null;
	}
	public Severity getEntryRefSymmetric() {
		return null;
	}
}

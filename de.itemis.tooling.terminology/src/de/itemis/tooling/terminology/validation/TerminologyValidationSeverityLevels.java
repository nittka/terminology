/*******************************************************************************
 * Copyright (c) 2013 itemis AG (http://www.itemis.eu).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package de.itemis.tooling.terminology.validation;

import org.eclipse.xtext.diagnostics.Severity;


public interface TerminologyValidationSeverityLevels {

	/**
	 * at most one preferred Term per language
	 * */
	Severity getOnePreferredTermPerLanguageLevel();
	/**
	 * at least one preferred Term per definied language*/
	Severity getMissingPreferredTerm();
	/**
	 * entry ID unique within terminology
	 * */
	Severity getUniqueEntryIdLevel();
	/**
	 * term unique within terminology
	 * */
	Severity getUniqueTermLevel();
	/**
	 * entry definition is empty
	 * */
	Severity getMissingDefinition();
	/**
	 * entry references must be symmetric
	 * */
	Severity getEntryRefSymmetric();
}

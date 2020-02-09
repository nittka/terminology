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
package de.itemis.tooling.terminology.ui.preferences;

/**
 * Constant definitions for plug-in preferences
 */
public class TerminologyPreferenceConstants {

	private static final String LANGUAGE_PREFIX="de.itemis.tooling.terminology.Terminology";

	//folding preferences
	public static final String FOLD_TERM_KEY = LANGUAGE_PREFIX+".folding.term";
	public static final String FOLD_METADATA_KEY = LANGUAGE_PREFIX+".folding.metadata";
	public static final String FOLD_FEEDBACK_KEY = LANGUAGE_PREFIX+".folding.feedback";

	//validation
	public static final String VALIDATION_UNIQUE_ENTRY_KEY = LANGUAGE_PREFIX+".validation.uniqueentry";
	public static final String VALIDATION_UNIQUE_TERM_KEY = LANGUAGE_PREFIX+".validation.uniqueterm";
	public static final String VALIDATION_PREFTERM_PERLANGUAGE_KEY = LANGUAGE_PREFIX+".validation.preferredterm";
	public static final String VALIDATION_PREFTERM_NOLANGUAGE_KEY = LANGUAGE_PREFIX+".validation.preferredtermnolang";
	public static final String VALIDATION_MISSING_PREFTERM_KEY = LANGUAGE_PREFIX+".validation.missingpreferredterm";
	public static final String VALIDATION_MISSING_DEFINITION_KEY = LANGUAGE_PREFIX+".validation.missingdefinition";
	public static final String VALIDATION_ENTRY_REF_SYMMETRIC_KEY = LANGUAGE_PREFIX+".validation.entryRefSym";
}

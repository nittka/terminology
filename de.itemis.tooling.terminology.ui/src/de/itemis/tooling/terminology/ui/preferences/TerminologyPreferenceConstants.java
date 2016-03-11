/*******************************************************************************
 * Copyright (c) 2013 itemis AG (http://www.itemis.eu).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

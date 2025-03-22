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

import com.google.inject.Inject;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import de.itemis.tooling.terminology.ui.generator.TerminologyGenerators;

/**
 * Class used to initialize default preference values.
 */
public class TerminologyPreferenceInitializer extends AbstractPreferenceInitializer {

	@Inject
	IPreferenceStore store;
	@Inject
	TerminologyGenerators generators;

	public void initializeDefaultPreferences() {
		//folding preferences
		store.setDefault(TerminologyPreferenceConstants.FOLD_METADATA_KEY, true);
		store.setDefault(TerminologyPreferenceConstants.FOLD_TERM_KEY, true);
		store.setDefault(TerminologyPreferenceConstants.FOLD_TERM_KEY, false);

		//validation
		store.setDefault(TerminologyPreferenceConstants.VALIDATION_PREFTERM_PERLANGUAGE_KEY, "null");
		store.setDefault(TerminologyPreferenceConstants.VALIDATION_UNIQUE_ENTRY_KEY, "null");
		store.setDefault(TerminologyPreferenceConstants.VALIDATION_UNIQUE_TERM_KEY, "null");
		store.setDefault(TerminologyPreferenceConstants.VALIDATION_MISSING_DEFINITION_KEY, "null");
		store.setDefault(TerminologyPreferenceConstants.VALIDATION_MISSING_PREFTERM_KEY, "null");
		store.setDefault(TerminologyPreferenceConstants.VALIDATION_ENTRY_REF_SYMMETRIC_KEY, "null");
		store.setDefault(TerminologyPreferenceConstants.VALIDATION_PREFTERM_NOLANGUAGE_KEY, true);

		//generators
//		generators.applyDefaults(store);
	}
}

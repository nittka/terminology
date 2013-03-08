package de.itemis.tooling.terminology.ui.preferences;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.xtext.builder.preferences.BuilderPreferenceAccess;

/**
 * Class used to initialize default preference values.
 */
public class TerminologyPreferenceInitializer extends AbstractPreferenceInitializer {

	@Inject
	IPreferenceStore store;

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

		store.setDefault(BuilderPreferenceAccess.PREF_AUTO_BUILDING,false);
	}
}

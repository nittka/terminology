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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.xtext.diagnostics.Severity;

import de.itemis.tooling.terminology.validation.TerminologyValidationSeverityLevels;

public class TerminologyPreferenceBasedValidationSeverityLevels implements
		TerminologyValidationSeverityLevels, IPropertyChangeListener {

	private IPreferenceStore prefernces;
	private Severity onePreferredPerLanguage;
	private Severity missingPreferredForLanguage;
	private Severity globalUniquEntryId;
	private Severity uniqueTerm;
	private Severity missingDefinition;
	private Severity entryRefSymmetric;
	private boolean checkMissingPreferredTermForLanguageWithoutTerms;

	@Inject
	public TerminologyPreferenceBasedValidationSeverityLevels(IPreferenceStore store) {
		prefernces=store;
		prefernces.addPropertyChangeListener(this);
		initValues();
	}

	public Severity getOnePreferredTermPerLanguageLevel() {
		return onePreferredPerLanguage;
	}

	public Severity getUniqueEntryIdLevel() {
		return globalUniquEntryId;
	}

	public Severity getUniqueTermLevel() {
		return uniqueTerm;
	}
	public Severity getMissingDefinitionLevel() {
		return missingDefinition;
	}

	public Severity getMissingPreferredTermLevel() {
		return missingPreferredForLanguage;
	}

	public Severity getEntryRefSymmetricLevel() {
		return entryRefSymmetric;
	}

	public boolean checkMissingPreferredTermForLanguageWithoutTerms() {
		return checkMissingPreferredTermForLanguageWithoutTerms;
	}

	public void propertyChange(PropertyChangeEvent event) {
		if(event.getProperty().contains(".validation.")){
			initValues();
		}
	}

	private void initValues() {
		globalUniquEntryId=getSeverity(TerminologyPreferenceConstants.VALIDATION_UNIQUE_ENTRY_KEY);
		onePreferredPerLanguage=getSeverity(TerminologyPreferenceConstants.VALIDATION_PREFTERM_PERLANGUAGE_KEY);
		missingPreferredForLanguage=getSeverity(TerminologyPreferenceConstants.VALIDATION_MISSING_PREFTERM_KEY);
		uniqueTerm=getSeverity(TerminologyPreferenceConstants.VALIDATION_UNIQUE_TERM_KEY);
		missingDefinition=getSeverity(TerminologyPreferenceConstants.VALIDATION_MISSING_DEFINITION_KEY);
		entryRefSymmetric=getSeverity(TerminologyPreferenceConstants.VALIDATION_ENTRY_REF_SYMMETRIC_KEY);
		checkMissingPreferredTermForLanguageWithoutTerms=prefernces.getBoolean(TerminologyPreferenceConstants.VALIDATION_PREFTERM_NOLANGUAGE_KEY);
	}

	Severity getSeverity(String key){
		Severity result=null;
		String s=prefernces.getString(key);
		if("info".equals(s)){
			result=Severity.INFO;
		}else if("warn".equals(s)){
			result=Severity.WARNING;
		}else if("error".equals(s)){
			result=Severity.ERROR;
		}
		return result;
	}
}

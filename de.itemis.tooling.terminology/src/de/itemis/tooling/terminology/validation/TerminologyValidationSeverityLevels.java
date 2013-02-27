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
}

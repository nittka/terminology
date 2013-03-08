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
}

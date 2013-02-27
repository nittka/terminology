/*
 * generated by Xtext
 */
package de.itemis.tooling.terminology;

import org.eclipse.xtext.formatting.IIndentationInformation;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.resource.IDefaultResourceDescriptionStrategy;

import de.itemis.tooling.terminology.formatting.TerminologyIndentation;
import de.itemis.tooling.terminology.naming.TerminologyNameProvider;
import de.itemis.tooling.terminology.resource.TerminologyResourceDescriptionStrategy;
import de.itemis.tooling.terminology.validation.TerminologyFixedSeverityLevels;
import de.itemis.tooling.terminology.validation.TerminologyValidationSeverityLevels;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class TerminologyRuntimeModule extends de.itemis.tooling.terminology.AbstractTerminologyRuntimeModule {

	@Override
	public Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider() {
		return TerminologyNameProvider.class;
	}

	public Class<? extends TerminologyValidationSeverityLevels> bindSeverityLevels() {
		return TerminologyFixedSeverityLevels.class;
	}

	public Class<? extends IDefaultResourceDescriptionStrategy> bindIndexingStrategy() {
		return TerminologyResourceDescriptionStrategy.class;
	}

	public Class<? extends IIndentationInformation> bindIndentation() {
		return TerminologyIndentation.class;
	}
}

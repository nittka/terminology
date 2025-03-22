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
package de.itemis.tooling.terminology.ui;

import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.formatting.IIndentationInformation;
import org.eclipse.xtext.generator.IGenerator2;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.actions.IActionContributor;
import org.eclipse.xtext.ui.editor.folding.IFoldingRegionProvider;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.outline.impl.OutlineFilterAndSorter.IComparator;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer;
import org.eclipse.xtext.ui.editor.syntaxcoloring.HighlightingReconciler;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.ui.editor.templates.XtextTemplateContextType;

import com.google.inject.Binder;
import com.google.inject.name.Names;

import de.itemis.tooling.terminology.formatting.TerminologyIndentation;
import de.itemis.tooling.terminology.ui.folding.TerminologyFoldingActionContributor;
import de.itemis.tooling.terminology.ui.folding.TerminologyFoldingRegionProvider;
import de.itemis.tooling.terminology.ui.generator.TerminologyUIGenerator;
import de.itemis.tooling.terminology.ui.hover.TerminologyEObjectHoverProvider;
import de.itemis.tooling.terminology.ui.outline.TerminologyOutlineSorter;
import de.itemis.tooling.terminology.ui.preferences.TerminologyBuilderPreferenceInitializer;
import de.itemis.tooling.terminology.ui.preferences.TerminologyPreferenceBasedValidationSeverityLevels;
import de.itemis.tooling.terminology.ui.syntaxcoloring.TerminologyHighlightingConfig;
import de.itemis.tooling.terminology.ui.syntaxcoloring.TerminologyHighlightingReconciler;
import de.itemis.tooling.terminology.ui.syntaxcoloring.TerminologySemanticHighlighter;
import de.itemis.tooling.terminology.ui.templates.TerminologyTemplateContextType;
import de.itemis.tooling.terminology.ui.templates.TerminologyTemplateContextTypeRegistry;
import de.itemis.tooling.terminology.validation.TerminologyValidationSeverityLevels;

/**
 * Use this class to register components to be used within the IDE.
 */
public class TerminologyUiModule extends de.itemis.tooling.terminology.ui.AbstractTerminologyUiModule {
	public TerminologyUiModule(AbstractUIPlugin plugin) {
		super(plugin);
	}

	public Class<? extends IHighlightingConfiguration> bindHighlightingConfig(){
		return TerminologyHighlightingConfig.class;
	}

	public Class<? extends ISemanticHighlightingCalculator> bindSemanticHighlighter(){
		return TerminologySemanticHighlighter.class;
	}

	public Class<? extends IEObjectHoverProvider> bindHoverProvider(){
		return TerminologyEObjectHoverProvider.class;
	}

	//Folding Start
	@Override
	@SuppressWarnings("restriction")
	public void configureBracketMatchingAction(Binder binder) {
		//actually we want to override the first binding only...
		binder.bind(IActionContributor.class).annotatedWith(Names.named("foldingActionGroup")).to( //$NON-NLS-1$
				TerminologyFoldingActionContributor.class);
		binder.bind(IActionContributor.class).annotatedWith(Names.named("bracketMatcherAction")).to( //$NON-NLS-1$
				org.eclipse.xtext.ui.editor.bracketmatching.GoToMatchingBracketAction.class);
		binder.bind(IPreferenceStoreInitializer.class).annotatedWith(Names.named("bracketMatcherPrefernceInitializer")) //$NON-NLS-1$
				.to(org.eclipse.xtext.ui.editor.bracketmatching.BracketMatchingPreferencesInitializer.class);
		binder.bind(IActionContributor.class).annotatedWith(Names.named("selectionActionGroup")).to( //$NON-NLS-1$
				org.eclipse.xtext.ui.editor.selection.AstSelectionActionContributor.class);
	}

	public Class<? extends XtextEditor> bindEditor() {
		return TerminologyXtextEditor.class;
	}

	public Class<? extends IFoldingRegionProvider> bindFoldingRegionProvider() {
		return TerminologyFoldingRegionProvider.class;
	}
	//Folding End

	public Class<? extends TerminologyValidationSeverityLevels> bindSeverityLevels() {
		return TerminologyPreferenceBasedValidationSeverityLevels.class;
	}

	@Override
	public Class<? extends IIndentationInformation> bindIIndentationInformation() {
		return TerminologyIndentation.class;
	}

	@Override
	public Class<? extends ContextTypeRegistry> bindContextTypeRegistry() {
		return TerminologyTemplateContextTypeRegistry.class;
	}

	public Class<? extends XtextTemplateContextType> bindTemplateContextType() {
		return TerminologyTemplateContextType.class;
	}

	@Override
	public Class<? extends IComparator> bindOutlineFilterAndSorter$IComparator() {
		return TerminologyOutlineSorter.class;
	}

	@Override
	public void configureBuilderPreferenceStoreInitializer(com.google.inject.Binder binder) {
		binder.bind(org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer.class).annotatedWith(com.google.inject.name.Names.named("builderPreferenceInitializer")).to(TerminologyBuilderPreferenceInitializer.class);
	}

	public Class<? extends IGenerator2> bindGenerator() {
		return TerminologyUIGenerator.class;
	}

	public Class<? extends HighlightingReconciler> bindHighlightingReconciler() {
		return TerminologyHighlightingReconciler.class;
	}
}

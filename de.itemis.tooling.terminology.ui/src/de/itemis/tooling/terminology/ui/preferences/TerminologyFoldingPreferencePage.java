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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.google.inject.Inject;

public class TerminologyFoldingPreferencePage extends FieldEditorPreferencePage  implements IWorkbenchPreferencePage{

	@Inject
	public TerminologyFoldingPreferencePage(IPreferenceStore preferenceStore) {
		super();
		setPreferenceStore(preferenceStore);
		setDescription("Choose the regions to be folded on editor startup.");
	}

	@Override
	protected void createFieldEditors() {
		addField(new BooleanFieldEditor(TerminologyPreferenceConstants.FOLD_METADATA_KEY, "Metadata", getFieldEditorParent()));
		addField(new BooleanFieldEditor(TerminologyPreferenceConstants.FOLD_TERM_KEY, "Terms", getFieldEditorParent()));
		addField(new BooleanFieldEditor(TerminologyPreferenceConstants.FOLD_FEEDBACK_KEY, "Feedback", getFieldEditorParent()));
	}
	public void init(IWorkbench workbench) {}
}

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

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.xtext.builder.preferences.BuilderPreferencePage;
import org.eclipse.xtext.ui.editor.preferences.PreferenceStoreAccessImpl;
import org.eclipse.xtext.xbase.lib.Pair;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

import de.itemis.tooling.terminology.generator.TerminologyGeneratorParticipant;
import de.itemis.tooling.terminology.ui.generator.TerminologyGenerators;

@SuppressWarnings("restriction")
public class TerminologyBuilderPreferencePage extends BuilderPreferencePage{

	private PreferenceStoreAccessImpl preferenceStoreAccess;
	private TerminologyGenerators generators;
	private Map<TerminologyGeneratorParticipant,Pair<Button,Text>> controls=Maps.newHashMap();

	@Inject
	public TerminologyBuilderPreferencePage(PreferenceStoreAccessImpl prefStoreAccess, TerminologyGenerators generators) {
		preferenceStoreAccess=prefStoreAccess;
		this.generators=generators;
	}

	@Override
	protected Control createPreferenceContent(Composite parent,
			IPreferencePageContainer preferencePageContainer) {
		if(generators.getGenerators(getProject()).size()>0){
		
			ExpandableComposite exComposite = createStyleSection(parent, "Terminology generators", 3);
			Composite composite=new Composite(exComposite, SWT.NONE);
			exComposite.setClient(composite);
			composite.setLayout(new GridLayout(3, false));
		
			new Label(composite, SWT.NONE).setText("Generator");
			new Label(composite, SWT.NONE).setText("active");
			new Label(composite, SWT.NONE).setText("subfolder");
			
			for (final TerminologyGeneratorParticipant generator : generators.getGenerators(getProject())) {
					new Label(composite, SWT.NONE).setText(generator.getDisplayName());
					final Button active= new Button(composite, SWT.CHECK);
					active.setData(generator);
					active.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							generator.setActive(active.getSelection());
						}
					});
					final Text directory= new Text(composite, SWT.BORDER | SWT.SINGLE);
					GridData textData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
					textData.widthHint = 150;
					directory.setLayoutData(textData);
					directory.setData(generator);
					directory.addModifyListener(new ModifyListener() {
						public void modifyText(ModifyEvent e) {
							generator.setFolder(directory.getText());
						}
					});
					controls.put(generator, Pair.of(active,directory));
			}
			updateControls(useProjectSettings());
		}
		Control result = super.createPreferenceContent(parent, preferencePageContainer);
		return result;
	}

	protected ExpandableComposite createStyleSection(Composite parent, String label, int nColumns) {
		ExpandableComposite excomposite = new ExpandableComposite(parent,SWT.NONE, ExpandableComposite.TWISTIE
				| ExpandableComposite.CLIENT_INDENT);
		excomposite.setText(label);
		excomposite.setExpanded(false);
		excomposite.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		excomposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, nColumns, 1));
		excomposite.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				getShell().layout(true, true);
			}
		});
		return excomposite;
	}

	

	@Override
	public void performApply() {
		super.performApply();
		generators.apply(getPreferenceStore());

	}

	@Override
	public boolean performOk() {
		boolean result= super.performOk();
		generators.apply(getPreferenceStore());
		return result;
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		generators.applyDefaults(getPreferenceStore());
		updateControls(useProjectSettings());
	}

	@Override
	protected void enableProjectSpecificSettings(
			boolean useProjectSpecificSettings) {
		super.enableProjectSpecificSettings(useProjectSpecificSettings);
		updateControls(useProjectSpecificSettings);
	}

	private void updateControls(boolean useProjectSpecificSettings) {
		boolean activated=!isProjectPreferencePage() || useProjectSpecificSettings;
		for (TerminologyGeneratorParticipant generator : generators.getGenerators(getProject())) {
			Pair<Button, Text> pair = controls.get(generator);
			if(pair!=null){
				pair.getKey().setSelection(generator.isActive());
				pair.getValue().setText(generator.getFolder());
				pair.getKey().setEnabled(activated);
				pair.getValue().setEnabled(activated);
			}
		}
	}

	@Override
	protected boolean hasProjectSpecificOptions(IProject project) {
		return super.hasProjectSpecificOptions(project)||preferenceStoreAccess.getContextPreferenceStore(project).getBoolean("is_project_specific");
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return preferenceStoreAccess.getWritablePreferenceStore(getProject());
	}
}

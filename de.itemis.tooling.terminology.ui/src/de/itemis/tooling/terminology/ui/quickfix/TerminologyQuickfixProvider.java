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
package de.itemis.tooling.terminology.ui.quickfix;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.model.edit.IModificationContext;
import org.eclipse.xtext.ui.editor.model.edit.ISemanticModification;
import org.eclipse.xtext.ui.editor.quickfix.DefaultQuickfixProvider;
import org.eclipse.xtext.ui.editor.quickfix.Fix;
import org.eclipse.xtext.ui.editor.quickfix.IssueResolutionAcceptor;
import org.eclipse.xtext.validation.Issue;

import de.itemis.tooling.terminology.terminology.Entry;
import de.itemis.tooling.terminology.validation.TerminologyJavaValidator;

public class TerminologyQuickfixProvider extends DefaultQuickfixProvider {

	@Fix(TerminologyJavaValidator.RELATED_ENTRY_SYMMETRIC)
	public void addInverseReference(final Issue issue, IssueResolutionAcceptor acceptor) {
		if(issue.getData()==null ||issue.getData().length<2){
			return;
		}
		acceptor.accept(issue, "add inverse reference", "adds this entry to the referenced entries of "+issue.getData()[1], null, new ISemanticModification() {
			public void apply(EObject element, IModificationContext context)
					throws Exception {
				String searchFor=issue.getData()[1];
				String referenceTo=issue.getData()[0];
				if(element instanceof Entry) {
					Entry entry = (Entry)element ;
					if(entry.getName().equals(referenceTo)){
						for (Entry target : entry.getRelatedEntries()) {
							if(target.getName().equals(searchFor)){
								target.getRelatedEntries().add(entry);
								break;
							}
						}
					}
				}
			}
		});
	}
}

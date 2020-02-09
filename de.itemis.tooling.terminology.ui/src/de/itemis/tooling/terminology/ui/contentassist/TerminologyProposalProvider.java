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
package de.itemis.tooling.terminology.ui.contentassist;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;
import org.eclipse.xtext.ui.editor.contentassist.PrefixMatcher;

import de.itemis.tooling.terminology.terminology.Entry;
import de.itemis.tooling.terminology.terminology.SubjectEntries;
import de.itemis.tooling.terminology.terminology.Term;
/**
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#contentAssist on how to customize content assistant
 */
public class TerminologyProposalProvider extends AbstractTerminologyProposalProvider {

	//propose related entries by allowing typing a term name but inserting the entry id
	@Override
	public void completeEntry_RelatedEntries(EObject model,
			Assignment assignment, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		SubjectEntries entries=(SubjectEntries) EcoreUtil2.getRootContainer(model);
		for (Entry entry : entries.getEntries()) {
			for (Term term : entry.getTerms()) {
				final String termNameLower =term.getName().toLowerCase();
				PrefixMatcher matcher = new PrefixMatcher() {
					@Override
					public boolean isCandidateMatchingPrefix(String name, String prefix) {
						return termNameLower.startsWith(prefix.toLowerCase())||name.startsWith(prefix);
					}
				};
				ContentAssistContext newContext = context.copy().setMatcher(matcher).toContext();

				ICompletionProposal result = null;
				StyledString displayString = getStyledDisplayString(term,term.getName(),term.getName());
				Image image = getImage(term);
				result = createCompletionProposal(entry.getName(), displayString, image, newContext);
				if (result instanceof ConfigurableCompletionProposal) {
					ConfigurableCompletionProposal typed = (ConfigurableCompletionProposal) result;
					typed.setAdditionalProposalInfo(entry);
					typed.setHover(getHover());
					typed.setMatcher(matcher);
				}
				getPriorityHelper().adjustCrossReferencePriority(result, context.getPrefix());
				acceptor.accept(result);
			}
		}
	}
}

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
package de.itemis.tooling.terminology.formatting;

import org.eclipse.xtext.formatting.impl.AbstractDeclarativeFormatter;
import org.eclipse.xtext.formatting.impl.FormattingConfig;

import com.google.inject.Inject;

import de.itemis.tooling.terminology.services.TerminologyGrammarAccess;

/**
 * This class contains custom formatting description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#formatting
 * on how and when to use it 
 * 
 * Also see {@link org.eclipse.xtext.xtext.XtextFormattingTokenSerializer} as an example
 */
public class TerminologyFormatter extends AbstractDeclarativeFormatter {

	@Inject
	TerminologyGrammarAccess ga;
	@Override
	protected void configureFormatting(FormattingConfig c) {
		c.setAutoLinewrap(120);
// It's usually a good idea to activate the following three statements.
// They will add and preserve newlines around comments
		c.setLinewrap(0, 1, 2).before(ga.getSL_COMMENTRule());
		c.setLinewrap(0, 1, 2).before(ga.getML_COMMENTRule());
		c.setLinewrap(0, 1, 1).after(ga.getML_COMMENTRule());

		c.setNoSpace().before(ga.getSubjectEntriesRule());
		//Entry
		c.setLinewrap(2).before(ga.getEntryRule());
		c.setNoSpace().around(ga.getEntryAccess().getLeftParenthesisKeyword_0_1());
		c.setNoSpace().before(ga.getEntryAccess().getRightParenthesisKeyword_0_4());
		c.setNoSpace().before(ga.getEntryAccess().getCommaKeyword_0_3_0());
		c.setLinewrap(1).after(ga.getEntryAccess().getRightParenthesisKeyword_0_4());
		c.setLinewrap(1).before(ga.getFeedbackAccess().getFeedbackKeyword_0());

		//Metadata
		c.setLinewrap(1).before(ga.getMetaDataRule());
		c.setIndentationIncrement().after(ga.getEntryAccess().getLeftCurlyBracketKeyword_3());
		c.setIndentationDecrement().before(ga.getEntryAccess().getRightCurlyBracketKeyword_11());
		c.setIndentationIncrement().before(ga.getMetaDataAccess().getStatusKeyword_2());
		c.setLinewrap(1).before(ga.getMetaDataAccess().getStatusKeyword_2());
		c.setLinewrap(1).before(ga.getMetaDataAccess().getCreatedKeyword_4());
		c.setLinewrap(1).before(ga.getMetaDataAccess().getModifiedKeyword_7_0());
		c.setLinewrap(1).before(ga.getMetaDataAccess().getRightCurlyBracketKeyword_8());
		c.setIndentationDecrement().before(ga.getMetaDataAccess().getRightCurlyBracketKeyword_8());

		//Entry continued Definition
		c.setLinewrap(1).before(ga.getEntryAccess().getDefinitionKeyword_6());
		c.setLinewrap(1).before(ga.getEntryAccess().getSourceKeyword_8_0());
		c.setLinewrap(1).before(ga.getEntryAccess().getRelatedEntriesKeyword_9_0());
		c.setLinewrap(1).before(ga.getEntryAccess().getRelatedEntriesKeyword_9_0());
		c.setNoSpace().before(ga.getEntryAccess().getCommaKeyword_9_2_0());

		//Term
		c.setLinewrap(1).before(ga.getTermAccess().getAllowDuplicateDuplicateOKKeyword_0_0());
		c.setLinewrap(1).before(ga.getTermAccess().getTermKeyword_1());
		c.setLinewrap(1).after(ga.getTermAccess().getNameAssignment_2());
		c.setIndentationIncrement().after(ga.getTermAccess().getNameAssignment_2());
		//TODO this seems to cause problems wrt. the unordered group
//		c.setIndentationDecrement().after(ga.getTermAccess().getUnorderedGroup_9());
		c.setIndentationDecrement().after(ga.getTermRule());

		c.setLinewrap(1).after(ga.getTermRule());
		c.setLinewrap(1).before(ga.getTermAccess().getStatusKeyword_3());
		c.setLinewrap(1).before(ga.getTermAccess().getLanguageKeyword_5());
		c.setLinewrap(1).before(ga.getTermAccess().getGrKeyword_7());
		c.setLinewrap(1).before(ga.getTermAccess().getUsageKeyword_9_0());
		c.setLinewrap(1).before(ga.getTermAccess().getCustomersKeyword_11_0());
		c.setLinewrap(1).before(ga.getTermAccess().getProductsKeyword_10_0());
		c.setNoSpace().before(ga.getTermAccess().getCommaKeyword_10_2_0());
		c.setNoSpace().before(ga.getTermAccess().getCommaKeyword_11_2_0());
		
		
		
		//Terminology
		c.setNoSpace().before(ga.getTerminologyRule());
		c.setLinewrap(1).before(ga.getTerminologyAccess().getDefinitionsKeyword_3());
		c.setIndentationIncrement().after(ga.getTerminologyAccess().getDefinitionsKeyword_3());
		c.setNoSpace().before(ga.getTerminologyAccess().getCommaKeyword_4_2_0());
		c.setNoSpace().before(ga.getTerminologyAccess().getCommaKeyword_5_2_0());
		c.setNoSpace().before(ga.getTerminologyAccess().getCommaKeyword_6_2_0());
		c.setNoSpace().before(ga.getTerminologyAccess().getCommaKeyword_7_2_0());
		c.setNoSpace().before(ga.getTerminologyAccess().getCommaKeyword_8_2_0());
		c.setNoSpace().before(ga.getTerminologyAccess().getCommaKeyword_9_2_0());
		c.setNoSpace().before(ga.getTerminologyAccess().getCommaKeyword_10_2_0());

		c.setLinewrap(1).before(ga.getTerminologyAccess().getSubjectsKeyword_4_0());
		c.setLinewrap(1).before(ga.getSubjectRule());
		c.setIndentationIncrement().before(ga.getTerminologyAccess().getSubjectsAssignment_4_1());
		c.setIndentationDecrement().before(ga.getTerminologyAccess().getStatusKeyword_5_0());

		c.setLinewrap(1).before(ga.getTerminologyAccess().getStatusKeyword_5_0());
		c.setLinewrap(1).before(ga.getStatusRule());
		c.setIndentationIncrement().before(ga.getTerminologyAccess().getStatusAssignment_5_1());
		c.setIndentationDecrement().before(ga.getTerminologyAccess().getLanguagesKeyword_6_0());

		c.setLinewrap(1).before(ga.getTerminologyAccess().getLanguagesKeyword_6_0());

		c.setLinewrap(1).before(ga.getTerminologyAccess().getGrKeyword_7_0());
		c.setIndentationIncrement().before(ga.getTerminologyAccess().getGrsAssignment_7_1());
		c.setLinewrap(1).before(ga.getGrRule());
		c.setIndentationDecrement().before(ga.getTerminologyAccess().getAuthorsKeyword_8_0());

		c.setLinewrap(1).before(ga.getTerminologyAccess().getAuthorsKeyword_8_0());
		c.setIndentationIncrement().before(ga.getTerminologyAccess().getAuthorsAssignment_8_1());
		c.setLinewrap(1).before(ga.getAuthorRule());
		c.setIndentationDecrement().before(ga.getTerminologyAccess().getProductsKeyword_9_0());

		c.setLinewrap(1).before(ga.getTerminologyAccess().getProductsKeyword_9_0());
		c.setIndentationIncrement().before(ga.getTerminologyAccess().getProductsAssignment_9_1());
		c.setLinewrap(1).before(ga.getProductRule());
		c.setIndentationDecrement().before(ga.getTerminologyAccess().getCustomersKeyword_10_0());

		c.setLinewrap(1).before(ga.getTerminologyAccess().getCustomersKeyword_10_0());
		c.setIndentationIncrement().before(ga.getTerminologyAccess().getCustomersAssignment_10_1());
		c.setLinewrap(1).before(ga.getCustomerRule());
//		c.setIndentationDecrement().before(ga.getTerminologyAccess().getCustomersKeyword_9_0());
	}
}

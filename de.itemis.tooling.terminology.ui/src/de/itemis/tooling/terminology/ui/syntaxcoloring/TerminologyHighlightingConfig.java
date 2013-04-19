/*******************************************************************************
 * Copyright (c) 2013 itemis AG (http://www.itemis.eu).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package de.itemis.tooling.terminology.ui.syntaxcoloring;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

public class TerminologyHighlightingConfig extends DefaultHighlightingConfiguration {

	public static final String TERM = "term";
	public static final String PREFERRED_TERM = "preferredTerm";
	public static final String DEFINITION = "definition";

	@Override
	public void configure(IHighlightingConfigurationAcceptor acceptor) {
		super.configure(acceptor);
		acceptor.acceptDefaultHighlighting(TERM, "Term", getTermStyle());
		acceptor.acceptDefaultHighlighting(PREFERRED_TERM, "preferred Term", getPreferredTermStyle());
		acceptor.acceptDefaultHighlighting(DEFINITION, "Definition", getDefinitionStyle());
	}

	public TextStyle getTermStyle(){
		TextStyle textStyle = defaultTextStyle().copy();
		textStyle.setColor(new RGB(64, 64, 0));
		return textStyle;
	}
	public TextStyle getPreferredTermStyle(){
		TextStyle style= getTermStyle().copy();
		style.setStyle(SWT.BOLD);
		return style;
	}
	public TextStyle getDefinitionStyle(){
		TextStyle style= stringTextStyle().copy();
		return style;
	}

	@Override
	public TextStyle keywordTextStyle() {
		TextStyle style = super.keywordTextStyle();
		style.setStyle(SWT.NONE);
		return style;
	}
}

/*******************************************************************************
 * Copyright (c) 2013 itemis AG (http://www.itemis.eu).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package de.itemis.tooling.terminology.ui.outline;

import java.text.Collator;
import java.util.Locale;

import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.actions.SortOutlineContribution.DefaultComparator;

public class TerminologyOutlineSorter extends DefaultComparator {
	private Collator sorter=Collator.getInstance(Locale.GERMANY);
	@Override
	public int compare(IOutlineNode o1, IOutlineNode o2) {
		return sorter.compare(o1.getText().toString(),o2.getText().toString());
	}
}

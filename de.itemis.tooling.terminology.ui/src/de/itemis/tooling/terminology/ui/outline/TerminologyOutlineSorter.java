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

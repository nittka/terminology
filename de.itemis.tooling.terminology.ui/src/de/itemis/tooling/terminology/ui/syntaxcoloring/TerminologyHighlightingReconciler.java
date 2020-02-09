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
package de.itemis.tooling.terminology.ui.syntaxcoloring;

import org.eclipse.xtext.ui.editor.syntaxcoloring.HighlightingReconciler;

public class TerminologyHighlightingReconciler extends HighlightingReconciler {

	@Override
	public void refresh() {
		new Thread(){
			@Override
			public void run() {
				internalRefresh();
			}
		}.run();
	}
	private void internalRefresh(){
		super.refresh();
	}
}

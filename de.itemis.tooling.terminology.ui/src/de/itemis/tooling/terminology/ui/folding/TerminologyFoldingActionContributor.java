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
package de.itemis.tooling.terminology.ui.folding;

import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.actions.IActionContributor;

//hook for using our own action group, where we want to have a string collapse action
/**
 * adapted from {@link org.eclipse.xtext.ui.editor.folding.FoldingActionContributor}
 * */
public class TerminologyFoldingActionContributor implements IActionContributor {

	private TerminologyFoldingActionGroup foldingActionGroup;

	public void contributeActions(XtextEditor editor) {
		foldingActionGroup = new TerminologyFoldingActionGroup(editor, editor.getInternalSourceViewer());
	}

	public void editorDisposed(XtextEditor editor) {
		if(foldingActionGroup != null)
			foldingActionGroup.dispose();
	}
}

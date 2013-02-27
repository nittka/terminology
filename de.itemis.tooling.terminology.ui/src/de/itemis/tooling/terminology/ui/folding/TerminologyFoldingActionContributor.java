package de.itemis.tooling.terminology.ui.folding;

import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.folding.FoldingActionContributor;

//hook for using our own action group, where we want to have a string collapse action
public class TerminologyFoldingActionContributor extends FoldingActionContributor {

	private TerminologyFoldingActionGroup foldingActionGroup;

	public void contributeActions(XtextEditor editor) {
		foldingActionGroup = new TerminologyFoldingActionGroup(editor, editor.getInternalSourceViewer());
	}

	public void editorDisposed(XtextEditor editor) {
		if(foldingActionGroup != null)
			foldingActionGroup.dispose();
	}
}

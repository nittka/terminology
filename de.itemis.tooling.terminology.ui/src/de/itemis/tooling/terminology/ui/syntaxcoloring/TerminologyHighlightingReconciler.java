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

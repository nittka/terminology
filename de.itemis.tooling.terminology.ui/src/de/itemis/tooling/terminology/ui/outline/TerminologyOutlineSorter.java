package de.itemis.tooling.terminology.ui.outline;

import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.actions.SortOutlineContribution.DefaultComparator;

public class TerminologyOutlineSorter extends DefaultComparator {

	@Override
	public int compare(IOutlineNode o1, IOutlineNode o2) {
		return o1.getText().toString().compareToIgnoreCase(o2.getText().toString());
	}
}

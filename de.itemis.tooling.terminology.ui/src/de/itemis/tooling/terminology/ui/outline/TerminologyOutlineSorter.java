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

package de.itemis.tooling.terminology.formatting;

import org.eclipse.xtext.formatting.IIndentationInformation;

public class TerminologyIndentation implements IIndentationInformation {

	public String getIndentString() {
		return "  ";
	}
}

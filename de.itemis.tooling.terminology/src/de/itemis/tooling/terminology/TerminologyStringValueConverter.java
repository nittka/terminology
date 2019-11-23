package de.itemis.tooling.terminology;

import org.eclipse.xtext.conversion.impl.STRINGValueConverter;

public class TerminologyStringValueConverter extends STRINGValueConverter {

	@Override
	protected String toEscapedString(String value) {
		String temp = super.toEscapedString(value);
		if(temp.contains("\\'")) {
			temp=temp.replaceAll("\\\\'", "'");
		}
		return temp;
	}
}

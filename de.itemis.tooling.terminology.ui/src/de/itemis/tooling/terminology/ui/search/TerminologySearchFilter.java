package de.itemis.tooling.terminology.ui.search;

import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.ui.search.IXtextSearchFilter;

import de.itemis.tooling.terminology.terminology.TerminologyPackage;

public class TerminologySearchFilter implements IXtextSearchFilter {

	public boolean reject(IEObjectDescription element) {
		return element.getEClass()==TerminologyPackage.Literals.ENTRY;
	}
}

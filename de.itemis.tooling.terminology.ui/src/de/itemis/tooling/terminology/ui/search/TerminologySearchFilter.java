package de.itemis.tooling.terminology.ui.search;

import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.ui.search.IXtextSearchFilter;

import de.itemis.tooling.terminology.terminology.TerminologyPackage;

public class TerminologySearchFilter implements IXtextSearchFilter {

	public boolean reject(IEObjectDescription element) {
		return element.getEClass()==TerminologyPackage.Literals.ENTRY
				||element.getEClass()==TerminologyPackage.Literals.SUBJECT
				||element.getEClass()==TerminologyPackage.Literals.TERMINOLOGY
				||element.getEClass()==TerminologyPackage.Literals.PRODUCT
				||element.getEClass()==TerminologyPackage.Literals.GR
				||element.getEClass()==TerminologyPackage.Literals.LANGUAGE
				||element.getEClass()==TerminologyPackage.Literals.CUSTOMER;
	}
}

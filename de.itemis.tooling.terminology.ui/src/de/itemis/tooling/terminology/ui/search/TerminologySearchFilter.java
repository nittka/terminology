/*******************************************************************************
 * Copyright (c) 2013 itemis AG (http://www.itemis.eu).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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

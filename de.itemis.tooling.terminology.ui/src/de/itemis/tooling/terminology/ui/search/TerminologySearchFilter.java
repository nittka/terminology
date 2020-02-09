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

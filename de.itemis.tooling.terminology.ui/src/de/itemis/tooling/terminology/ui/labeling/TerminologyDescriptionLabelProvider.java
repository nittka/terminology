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
package de.itemis.tooling.terminology.ui.labeling;

import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.ui.label.DefaultDescriptionLabelProvider;

import de.itemis.tooling.terminology.terminology.TermStatus;
import de.itemis.tooling.terminology.terminology.TerminologyPackage;

/**
 * Provides labels for a IEObjectDescriptions and IResourceDescriptions.
 * 
 * see http://www.eclipse.org/Xtext/documentation/latest/xtext.html#labelProvider
 */
public class TerminologyDescriptionLabelProvider extends DefaultDescriptionLabelProvider {

/*
	//Labels and icons can be computed like this:
	
	String text(IEObjectDescription ele) {
	  return "my "+ele.getName();
	}
	 
    String image(IEObjectDescription ele) {
      return ele.getEClass().getName() + ".gif";
    }	 
*/
	@Override
	public String text(IEObjectDescription ele) {
		if(ele.getEClass()==TerminologyPackage.Literals.TERM){
			return ele.getName().getLastSegment()+ " - Term";
		}
		return (String)super.text(ele);
	}

	public String image(IEObjectDescription ele) {
		if(ele.getEClass()==TerminologyPackage.Literals.TERM){
			String result=null;
			try {
				TermStatus status = TermStatus.getByName(ele.getUserData("status"));
				switch(status){
					case PREFERRED:result="public_co.gif";break;
					case PERMITTED:result="protected_co.gif";break;
					case REJECTED:result="private_co.gif";break;
					case SUPERSEDED:result="compare_method.gif";break;
				}
				return result;
			} catch (Exception e) {
				return null;
			}
		}
		return (String)super.image(ele);
	}
}

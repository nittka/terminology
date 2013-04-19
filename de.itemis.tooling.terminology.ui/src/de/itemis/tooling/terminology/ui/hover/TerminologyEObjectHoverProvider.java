/*******************************************************************************
 * Copyright (c) 2013 itemis AG (http://www.itemis.eu).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package de.itemis.tooling.terminology.ui.hover;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.ui.editor.hover.html.DefaultEObjectHoverProvider;

import com.google.common.collect.ImmutableList;

import de.itemis.tooling.terminology.terminology.Author;
import de.itemis.tooling.terminology.terminology.Customer;
import de.itemis.tooling.terminology.terminology.Entry;
import de.itemis.tooling.terminology.terminology.Gr;
import de.itemis.tooling.terminology.terminology.Product;
import de.itemis.tooling.terminology.terminology.Status;
import de.itemis.tooling.terminology.terminology.Subject;
import de.itemis.tooling.terminology.terminology.Term;
import de.itemis.tooling.terminology.terminology.TerminologyPackage;

public class TerminologyEObjectHoverProvider extends DefaultEObjectHoverProvider {

//	@Override
//	protected String getFirstLine(EObject o) {
//		return "";
//	}
	@Override
	protected String getDocumentation(EObject o) {
		if(o instanceof Term){
			ICompositeNode node = NodeModelUtils.findActualNodeFor(o);
			StringBuilder b=new StringBuilder();
			String definition=((Entry)o.eContainer()).getDefinition();
			if(definition!=null && definition.length()>0){
				b.append("Definition: ");
				b.append(definition);
				b.append("\n");
			}
			String nodeText = node.getText().trim();
			int StatusIndex =nodeText.indexOf("Status");
			b.append(nodeText.substring(StatusIndex));
			//make hover window bigger
			b.append("<dl></dl><dl></dl><dl></dl><dl></dl><dl></dl>");
			return b.toString().replaceAll("\n", "</br>");
		} else if(o instanceof Entry){
			Entry e=(Entry)o;
			StringBuilder b=new StringBuilder();
			if(e.getDefinition()!=null &&e.getDefinition().length()>0){
				b.append("Definition: ");
				b.append(e.getDefinition());
				b.append("\n");
			}
			for (Term t : e.getTerms()) {
				b.append("Term ");
				b.append(t.getName());
				b.append("\n");
			}
			b.append("<dl></dl><dl></dl><dl></dl><dl></dl><dl></dl>");
			return b.toString().replaceAll("\n", "</br>");
		} else if (o instanceof Subject){
			return ((Subject) o).getDisplayName();
		} else if(o instanceof Gr){
			return ((Gr) o).getDisplayName();
		} else if(o instanceof Status){
			return ((Status) o).getDisplayName();
		} else if(o instanceof Author){
			return ((Author) o).getDisplayName();
		} else if(o instanceof Customer){
			return ((Customer) o).getDisplayName();
		} else if(o instanceof Product){
			return ((Product) o).getDisplayName();
		}
		return null;
	}

//	//remove surrounding quotation marks and escaped quotes
//	private String simplify(String value) {
//		String resultString="";
//		if(value!=null){
//			if(value.startsWith("\"\"\"")){
//				resultString=value.substring(3, value.length()-3);
//			}else{
//				resultString=value.substring(1, value.length()-1);
//			}
//		}
//		return resultString.replaceAll("\\\\\"", "\"").replaceAll("\n", "</br>");
//	}

	private static final List<EClass> hoverClasses=ImmutableList.of(
			TerminologyPackage.Literals.TERM,
			TerminologyPackage.Literals.AUTHOR,
			TerminologyPackage.Literals.SUBJECT,
			TerminologyPackage.Literals.STATUS,
			TerminologyPackage.Literals.GR,
			TerminologyPackage.Literals.ENTRY,
			TerminologyPackage.Literals.PRODUCT,
			TerminologyPackage.Literals.CUSTOMER
		);
	@Override
	protected boolean hasHover(EObject o) {
		EClass eClass = o.eClass();
		return hoverClasses.contains(eClass);
	}
}

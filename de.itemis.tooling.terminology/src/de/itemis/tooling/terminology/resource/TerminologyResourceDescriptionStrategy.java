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
package de.itemis.tooling.terminology.resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.impl.LeafNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionStrategy;
import org.eclipse.xtext.util.IAcceptor;
import org.eclipse.xtext.util.IResourceScopeCache;

import de.itemis.tooling.terminology.terminology.Entry;
import de.itemis.tooling.terminology.terminology.Term;
import de.itemis.tooling.terminology.terminology.TermStatus;
import de.itemis.tooling.terminology.terminology.TerminologyPackage;

public class TerminologyResourceDescriptionStrategy extends
		DefaultResourceDescriptionStrategy {

	@Inject
	IResourceScopeCache cache;

	@Override
	public boolean createEObjectDescriptions(EObject eObject,
			IAcceptor<IEObjectDescription> acceptor) {
		if (eObject instanceof Entry) {
			acceptor.accept(EObjectDescription.create(getQualifiedNameProvider().getFullyQualifiedName(eObject), eObject));
			Term defTerm=getTermForDefinition((Entry) eObject);
			for (Term term : ((Entry) eObject).getTerms()) {
				acceptor.accept(EObjectDescription.create(
						getQualifiedNameProvider().getFullyQualifiedName(term),
						term, getUserData((Term) term, defTerm)));
			}
			return false;
		}
		return super.createEObjectDescriptions(eObject, acceptor);
	}

	private Term getTermForDefinition(Entry entry) {
		Term fallBack=null;
		Term firstPreferred=null;
		if(entry.getTerms().size()>0){
			fallBack=entry.getTerms().get(0);
		}
		for (Term term : entry.getTerms()) {
			if(term.getStatus()==TermStatus.PREFERRED){
				if(firstPreferred==null){
					firstPreferred=term;
					break;
				}
			}
		}
		return firstPreferred!=null?firstPreferred:fallBack;
	}

	private Map<String, String> getUserData(Term term, Term defTerm) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("status", term.getStatus().toString());
		String def = ((Entry) term.eContainer()).getDefinition();
		if (term==defTerm && def != null && def.length() > 0 ) {
			result.put("def", def);
		}
		if(term.getUsage()!=null &&term.getUsage().length()>0){
			result.put("sem", term.getUsage());
		}
		List<INode> languageNodes = NodeModelUtils.findNodesForFeature(term, TerminologyPackage.Literals.TERM__LANGUAGE);
		for (INode iNode : languageNodes) {
			if(iNode instanceof LeafNode){
				if(!((LeafNode) iNode).isHidden()){
					result.put("lang", iNode.getText());
					break;
				}
			}
		}
		return result;
	}
}

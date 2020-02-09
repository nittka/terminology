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
package de.itemis.tooling.terminology.ui.syntaxcoloring;

import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;

import de.itemis.tooling.terminology.terminology.Entry;
import de.itemis.tooling.terminology.terminology.Term;
import de.itemis.tooling.terminology.terminology.TermStatus;
import de.itemis.tooling.terminology.terminology.TerminologyPackage;

public class TerminologySemanticHighlighter implements
		ISemanticHighlightingCalculator {

	public void provideHighlightingFor(XtextResource resource,
			IHighlightedPositionAcceptor acceptor) {
		if(resource!=null){
			TreeIterator<EObject> iterator = resource.getAllContents();
			while(iterator.hasNext()){
				highlight(iterator.next(), acceptor);
			}
		}
	}

	private void highlight(EObject obj, IHighlightedPositionAcceptor acceptor) {
		if(obj instanceof Term){
			highlightTerm((Term)obj,acceptor);
		} else if(obj instanceof Entry){
			highlightDefinition((Entry)obj, acceptor);
		}
	}

	private void highlightTerm(Term term,
			IHighlightedPositionAcceptor acceptor) {
		String id=TerminologyHighlightingConfig.TERM;
		if(term.getStatus()==TermStatus.PREFERRED){
			id=TerminologyHighlightingConfig.PREFERRED_TERM;
		}
		INode node = getNode(term, TerminologyPackage.Literals.TERM__NAME);
		if(node!=null){
			acceptor.addPosition(node.getOffset(), node.getLength(), id);
		}
	}

	private void highlightDefinition(Entry entry,
			IHighlightedPositionAcceptor acceptor) {
		INode node = getNode(entry, TerminologyPackage.Literals.ENTRY__DEFINITION);
		if(node!=null){
			acceptor.addPosition(node.getOffset(), node.getLength(), TerminologyHighlightingConfig.DEFINITION);
		}
	}

	private INode getNode(EObject o, EStructuralFeature f){
		List<INode> candidates = NodeModelUtils.findNodesForFeature(o, f);
		if(candidates.size()>0){
			return candidates.get(0);
		}else{
			return null;
		}
	}
}

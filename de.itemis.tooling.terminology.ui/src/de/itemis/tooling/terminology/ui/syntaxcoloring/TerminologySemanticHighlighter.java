package de.itemis.tooling.terminology.ui.syntaxcoloring;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
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
		INode node = NodeModelUtils.findNodesForFeature(term, TerminologyPackage.Literals.TERM__NAME).get(0);
		acceptor.addPosition(node.getOffset(), node.getLength(), id);
	}

	private void highlightDefinition(Entry entry,
			IHighlightedPositionAcceptor acceptor) {
		INode node = NodeModelUtils.findNodesForFeature(entry, TerminologyPackage.Literals.ENTRY__DEFINITION).get(0);
		acceptor.addPosition(node.getOffset(), node.getLength(), TerminologyHighlightingConfig.DEFINITION);
	}
}

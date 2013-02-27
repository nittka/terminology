package de.itemis.tooling.terminology.resource;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionStrategy;
import org.eclipse.xtext.util.IAcceptor;

import de.itemis.tooling.terminology.terminology.Entry;
import de.itemis.tooling.terminology.terminology.Term;
import de.itemis.tooling.terminology.terminology.TermStatus;
import de.itemis.tooling.terminology.terminology.TerminologyPackage;

public class TerminologyResourceDescriptionStrategy extends
		DefaultResourceDescriptionStrategy {

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

	//TODO use primary language (first in list of languages) instead of hard coded de
	private Term getTermForDefinition(Entry entry) {
		Term deTerm=null;
		Term firstPreferred=null;
		for (Term term : entry.getTerms()) {
			if(term.getStatus()==TermStatus.PREFERRED){
				if(firstPreferred==null){
					firstPreferred=term;
				}
				try {
					//TODO ensure comments do not break this functionality
					String language=NodeModelUtils.findNodesForFeature(term, TerminologyPackage.Literals.TERM__LANGUAGE).get(0).getText();
					if("de".equals(language) && deTerm==null){
						deTerm=term;
					}
				} catch (Exception e) {
					//that's OK
				}
				if(deTerm!=null && firstPreferred!=null){
					break;
				}
			}
		}
		return deTerm!=null?deTerm:firstPreferred;
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
		//TODO customer/products
		return result;
	}
}

package de.itemis.tooling.terminology.resource;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionStrategy;
import org.eclipse.xtext.util.IAcceptor;
import org.eclipse.xtext.util.IResourceScopeCache;

import com.google.inject.Provider;

import de.itemis.tooling.terminology.terminology.Entry;
import de.itemis.tooling.terminology.terminology.Language;
import de.itemis.tooling.terminology.terminology.Term;
import de.itemis.tooling.terminology.terminology.TermStatus;
import de.itemis.tooling.terminology.terminology.Terminology;

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
		Term primaryLanguagePreferredTerm=null;
		Term firstPreferred=null;
		for (Term term : entry.getTerms()) {
			if(term.getStatus()==TermStatus.PREFERRED){
				if(firstPreferred==null){
					firstPreferred=term;
				}
				try {
					if(primaryLanguagePreferredTerm==null && isPrimaryLanguageTerm(term)){
						primaryLanguagePreferredTerm=term;
					}
				} catch (Exception e) {
					//that's OK
				}
				if(primaryLanguagePreferredTerm!=null && firstPreferred!=null){
					break;
				}
			}
		}
		return primaryLanguagePreferredTerm!=null?primaryLanguagePreferredTerm:firstPreferred;
	}

	private boolean isPrimaryLanguageTerm(Term term) {
		//linking during creation of ResourceDescriptions is not really good style
		final Language l=term.getLanguage();
		Language primaryLanguage=cache.get("primaryLanguage", term.eResource(), new Provider<Language>() {
			public Language get() {
				return ((Terminology)l.eContainer()).getLanguages().get(0);
			}
		});
		return l==primaryLanguage;
//		String language=NodeModelUtils.findNodesForFeature(term, TerminologyPackage.Literals.TERM__LANGUAGE).get(0).getText();
//		return "de".equals(language);
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

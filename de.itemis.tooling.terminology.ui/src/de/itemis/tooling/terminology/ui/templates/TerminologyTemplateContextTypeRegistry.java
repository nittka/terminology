package de.itemis.tooling.terminology.ui.templates;

import java.util.List;

import org.eclipse.xtext.IGrammarAccess;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.ui.editor.templates.ContextTypeIdHelper;
import org.eclipse.xtext.ui.editor.templates.XtextTemplateContextType;
import org.eclipse.xtext.ui.editor.templates.XtextTemplateContextTypeRegistry;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.itemis.tooling.terminology.services.TerminologyGrammarAccess;

@Singleton
public class TerminologyTemplateContextTypeRegistry extends
		XtextTemplateContextTypeRegistry {

	@Inject
	public TerminologyTemplateContextTypeRegistry(IGrammarAccess grammarAccess,
			Provider<XtextTemplateContextType> ctxTypeProvider,
			ContextTypeIdHelper helper) {
		super(grammarAccess, ctxTypeProvider, helper);
	}
	
	@Override
	protected void registerContextTypes(IGrammarAccess grammarAccess,
			Provider<XtextTemplateContextType> ctxTypeProvider) {
		TerminologyGrammarAccess ga = (TerminologyGrammarAccess)grammarAccess;
		List<XtextTemplateContextType> allContextTypes = Lists.newArrayList();

		//entry
		allContextTypes.add(getType(ga.getEntryRule(), ctxTypeProvider));
		allContextTypes.add(getType(ga.getFeedbackAccess().getFeedbackKeyword_0(), ctxTypeProvider));
		allContextTypes.add(getType(ga.getEntryAccess().getSourceKeyword_8_0(), ctxTypeProvider));

		//term
		allContextTypes.add(getType(ga.getTermRule(), ctxTypeProvider));
		allContextTypes.add(getType(ga.getMetaDataAccess().getModifiedKeyword_7_0(), ctxTypeProvider));
		allContextTypes.add(getType(ga.getTermAccess().getUsageKeyword_9_0(), ctxTypeProvider));
		allContextTypes.add(getType(ga.getTermAccess().getProductsKeyword_10_0(), ctxTypeProvider));
		allContextTypes.add(getType(ga.getTermAccess().getCustomersKeyword_11_0(), ctxTypeProvider));
		
		for (XtextTemplateContextType templateContextType: allContextTypes) {
			addContextType(templateContextType);
		}
	}

	private XtextTemplateContextType getType(ParserRule rule, Provider<XtextTemplateContextType> ctxTypeProvider){
		XtextTemplateContextType type = ctxTypeProvider.get();
		type.setName(rule.getName());
		type.setId(getId(rule));
		return type;
	}

	private XtextTemplateContextType getType(Keyword keyword, Provider<XtextTemplateContextType> ctxTypeProvider){
		XtextTemplateContextType type = ctxTypeProvider.get();
		type.setName("Keyword '"+keyword.getValue()+"'");
		type.setId(getId(keyword));
		return type;
	}
}

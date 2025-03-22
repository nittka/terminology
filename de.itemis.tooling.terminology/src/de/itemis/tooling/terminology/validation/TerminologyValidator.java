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
package de.itemis.tooling.terminology.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IReferenceDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider;
import org.eclipse.xtext.util.IResourceScopeCache;
import org.eclipse.xtext.validation.Check;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;

import de.itemis.tooling.terminology.terminology.Entry;
import de.itemis.tooling.terminology.terminology.Language;
import de.itemis.tooling.terminology.terminology.Subject;
import de.itemis.tooling.terminology.terminology.SubjectEntries;
import de.itemis.tooling.terminology.terminology.Term;
import de.itemis.tooling.terminology.terminology.TermStatus;
import de.itemis.tooling.terminology.terminology.Terminology;
import de.itemis.tooling.terminology.terminology.TerminologyPackage;
 

public class TerminologyValidator extends AbstractTerminologyValidator {

	public static final String RELATED_ENTRY_SYMMETRIC="relatedEntrySymmetric";
	@Inject
	TerminologyValidationSeverityLevels levels;

	@Inject
	private ResourceDescriptionsProvider resourceDescriptionsProvider;

	@Inject
	private IResourceScopeCache cache;

	@Check
	public void oneMissingDefinition(Entry entry) {
		Severity level=levels.getMissingDefinitionLevel();
		if(level!=null){
			String definition=entry.getDefinition();
			if(definition==null ||definition.trim().length()==0){
				createError(level, "missing definition", TerminologyPackage.Literals.ENTRY__NAME);
			}
		}
	}

	@Check
	public void onePreferredTerm(Entry entry) {
		Severity levelToMany=levels.getOnePreferredTermPerLanguageLevel();
		Severity levelMissing=levels.getMissingPreferredTermLevel();
		boolean includeMissingForUnusedLanguage=levels.checkMissingPreferredTermForLanguageWithoutTerms();
		if(levelToMany==null && levelMissing==null){
			return;
		}
		List<Language> languages = Lists.newArrayList(getTerminology(entry).getLanguages());
		Set<Language> duplicates=new HashSet<Language>();
		Set<Language> unUsedLanguages=new HashSet<Language>(languages);
		Map<Language, Term> defined=new HashMap<Language, Term>();

		for (Term term :entry.getTerms()) {
			Language language = term.getLanguage();
			unUsedLanguages.remove(term.getLanguage());
			if(term.getStatus()==TermStatus.PREFERRED){
				if(defined.containsKey(language)){
					duplicates.add(language);
					createError(levelToMany, term, "only one preferred term per language allowed", TerminologyPackage.Literals.TERM__LANGUAGE);
				} else{
					defined.put(language, term);
				}
			}
		}

		for (Language language : duplicates) {
			createError(levelToMany, defined.get(language), "only one preferred term per language allowed", TerminologyPackage.Literals.TERM__LANGUAGE);
		}

		languages.removeAll(defined.keySet());
		languages.removeAll(entry.getMissingPreferredTermLangage());
		if(!includeMissingForUnusedLanguage){
			languages.removeAll(unUsedLanguages);
		}
		if(!languages.isEmpty() && levelMissing!=null){
			String list=Joiner.on(", ").join(Collections2.transform(languages, new Function<Language, String>() {
				public String apply(Language s){
					return s.getName();
				}
			}));
			createError(levelMissing, "missing preferred term for the following languages: "+list, TerminologyPackage.Literals.ENTRY__NAME);
		}
	}

//	@Check(CheckType.EXPENSIVE)
	public void oneSubjectFile(Terminology terminology) {
		Map<Subject, List<URI>> subjectFileLinks=getSubjectLinks(terminology);
		for (Subject def : terminology.getSubjects()) {
			List<URI> list = subjectFileLinks.get(def);
			if(list.isEmpty()){
				error("no file for this subject",def,TerminologyPackage.Literals.WITH_NAME_AND_DISPLAY_NAME__NAME,-1);
			} else if(list.size()>1){
				error("more than one file for this subject",def,TerminologyPackage.Literals.WITH_NAME_AND_DISPLAY_NAME__NAME,-1);
			}
		}
	}

	@Check
	public void uniqueEntryId(Entry entry) {
		Severity level=levels.getUniqueEntryIdLevel();
		if(level!=null){
			SubjectEntries entries=getEntries(entry);
			if(getDuplicate(entries, TerminologyPackage.Literals.ENTRY).keySet().contains(entry.getName())){
				createError(level, "entry id not unique: "+entry.getName(), TerminologyPackage.Literals.ENTRY__NAME);
			}
		}
	}

	@Check
	public void inverseEntryReference(Entry entry) {
		Severity level=levels.getEntryRefSymmetricLevel();
		if(level!=null){
			for (Entry referenced : entry.getRelatedEntries()) {
				if(!referenced.getRelatedEntries().contains(entry)){
					createError(level, entry, "related entry "+referenced.getName() +" does refence this one", TerminologyPackage.Literals.ENTRY__RELATED_ENTRIES, entry.getRelatedEntries().indexOf(referenced), RELATED_ENTRY_SYMMETRIC,entry.getName(),referenced.getName());
				}
			}
		}
	}


	@Check
	public void uniqueTerm(Term term) {
		Severity level=levels.getUniqueTermLevel();
		if(level!=null){
			SubjectEntries entries=getEntries(term);
			if(!term.isAllowDuplicate()){
				List<IEObjectDescription> duplicates = getDuplicate(entries, TerminologyPackage.Literals.TERM).get(term.getName());
				if(duplicates!=null){
					String termLanguage = term.getLanguage().getName();
					List<IEObjectDescription> languageDuplicates=new ArrayList<IEObjectDescription>();
					for (IEObjectDescription desc : duplicates) {
						if(termLanguage.equals(desc.getUserData("lang"))){
							languageDuplicates.add(desc);
						}
					}
					//term itself will be one element of the list
					if(languageDuplicates.size()>1){
						createError(level, "multiple definitions for the same language for term "+term.getName(), TerminologyPackage.Literals.TERM__NAME);
					}
				}
			}
		}
	}


	private Map<String, List<IEObjectDescription>> getDuplicate(final SubjectEntries entries, final EClass clazz){
		Map<String, List<IEObjectDescription>>result= cache.get(clazz, entries.eResource(), new Provider<Map<String, List<IEObjectDescription>>>() {
			public Map<String, List<IEObjectDescription>> get(){
				String terminologyName=getTerminology(entries).getName();
				Map<String, List<IEObjectDescription>> unfiltered=new HashMap<String, List<IEObjectDescription>>();
				IResourceDescriptions index = resourceDescriptionsProvider.getResourceDescriptions(entries.eResource());
				Iterable<IEObjectDescription> indexedEntries = index.getExportedObjectsByType(clazz);
				for (IEObjectDescription entry : indexedEntries) {
					if(terminologyName.equals(entry.getQualifiedName().getFirstSegment())){
						String name = entry.getQualifiedName().getLastSegment();
						List<IEObjectDescription> list = unfiltered.get(name);
						if(list==null){
							list=new ArrayList<IEObjectDescription>();
							unfiltered.put(name, list);
						}
						list.add(entry);
					}
				}
				Map<String, List<IEObjectDescription>> duplicates=new HashMap<String, List<IEObjectDescription>>();
				for (Map.Entry<String, List<IEObjectDescription>> mapEntry : unfiltered.entrySet()) {
					if(mapEntry.getValue().size()>1){
						duplicates.put(mapEntry.getKey(), mapEntry.getValue());
					}
				}
				return duplicates;
			}
		});
		return result;

	}

	private Map<Subject, List<URI>> getSubjectLinks(final Terminology t){
		Map<Subject, List<URI>>result= cache.get("subjectLinks", t.eResource(), new Provider<Map<Subject, List<URI>>>() {
			public Map<Subject, List<URI>> get(){
				Map<URI, Subject> defUri=new HashMap<URI, Subject>();
				Map<Subject, List<URI>> result=new HashMap<Subject, List<URI>>();
				EList<Subject> defs = t.getSubjects();
				for (Subject subject : defs) {
					defUri.put(EcoreUtil.getURI(subject), subject);
					result.put(subject, new ArrayList<URI>());
				}
				IResourceDescriptions index = resourceDescriptionsProvider.getResourceDescriptions(t.eResource());
				Iterable<IResourceDescription> descs = index.getAllResourceDescriptions();
				for (IResourceDescription desc : descs) {
					if(desc.getClass().getSimpleName().charAt(0)!='C'){
						//prevent error log for CopiedResourceDescription
						Iterable<IReferenceDescription> refs = desc.getReferenceDescriptions();
						for (IReferenceDescription ref : refs) {
							if(ref.getEReference()==TerminologyPackage.Literals.SUBJECT_ENTRIES__SUBJECT){
								Subject def = defUri.get(ref.getTargetEObjectUri());
								if(def!=null){
									result.get(def).add(ref.getSourceEObjectUri());
								}
							}
						}
					}
				}
				return result;
			}
		});
		return result;
	}

	private Terminology getTerminology(EObject object){
		EObject root = EcoreUtil2.getRootContainer(object);
		if(root instanceof Terminology){
			return (Terminology)root;
		}else if(root instanceof SubjectEntries){
			return getTerminology(((SubjectEntries)root).getSubject());
		}
		return null;
	}

	private SubjectEntries getEntries(EObject o){
		EObject root = EcoreUtil2.getRootContainer(o);
		if(root instanceof SubjectEntries){
			return (SubjectEntries)root;
		}
		return null;
	}

	private void createError(Severity s, String errorMEssage, EStructuralFeature feature){
		createError(s, getCurrentObject(), errorMEssage, feature,-1,(String)null);
	}
	private void createError(Severity s, EObject source, String errorMEssage, EStructuralFeature feature){
		createError(s, source, errorMEssage, feature,-1,(String)null);
	}
	private void createError(Severity s, EObject o, String errorMEssage, EStructuralFeature feature, int index, String ...issueData){
		if(s==null){
			return;
		}
		String code=null;
		String[]data={};
		//init code and data
		if(issueData!=null&&issueData.length>0){
			code=issueData[0];
			if(issueData.length>1){
				data=new String[issueData.length-1];
				//TODO there is definetly some library for that
				for(int i=0;i<data.length;i++){
					data[i]=issueData[i+1];
				}
			}
		}
		switch (s) {
		case ERROR:
			error(errorMEssage, o, feature,index, code, data);
			break;
		case WARNING:
			warning(errorMEssage, o, feature,index, code, data);
			break;
		case INFO:
			info(errorMEssage, o, feature,index, code, data);
			break;
		default:
			break;
		}
	}
}

/*******************************************************************************
 * Copyright (c) 2013 itemis AG (http://www.itemis.eu).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package de.itemis.tooling.terminology.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

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
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.inject.Provider;

import de.itemis.tooling.terminology.terminology.Entry;
import de.itemis.tooling.terminology.terminology.Language;
import de.itemis.tooling.terminology.terminology.Subject;
import de.itemis.tooling.terminology.terminology.SubjectEntries;
import de.itemis.tooling.terminology.terminology.Term;
import de.itemis.tooling.terminology.terminology.TermStatus;
import de.itemis.tooling.terminology.terminology.Terminology;
import de.itemis.tooling.terminology.terminology.TerminologyPackage;
 

public class TerminologyJavaValidator extends AbstractTerminologyJavaValidator {

	public static final String RELATED_ENTRY_SYMMETRIC="relatedEntrySymmetric";
	@Inject
	TerminologyValidationSeverityLevels levels;

	@Inject
	private ResourceDescriptionsProvider resourceDescriptionsProvider;

	@Inject
	private IResourceScopeCache cache;

	@Check
	public void oneMissingDefinition(Entry entry) {
		Severity level=levels.getMissingDefinition();
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
		Severity levelMissing=levels.getMissingPreferredTerm();
		if(levelToMany==null && levelMissing==null){
			return;
		}
		List<Language> languages = Lists.newArrayList(getTerminology(entry).getLanguages());
		Set<Language> duplicates=new HashSet<Language>();
		Map<Language, Term> defined=new HashMap<Language, Term>();
		
		Collection<Term> preferred = Collections2.filter(entry.getTerms(),new Predicate<Term>() {
			public boolean apply(Term term){
				return term.getStatus()==TermStatus.PREFERRED;
			}
		});
		for (Term term : preferred) {
			Language language = term.getLanguage();
			if(defined.containsKey(language)){
				duplicates.add(language);
				createError(levelToMany, term, "only one preferred term per language allowed", TerminologyPackage.Literals.TERM__LANGUAGE);
			} else{
				defined.put(language, term);
			}
		}
		for (Language language : duplicates) {
			createError(levelToMany, defined.get(language), "only one preferred term per language allowed", TerminologyPackage.Literals.TERM__LANGUAGE);
		}

		languages.removeAll(defined.keySet());
		languages.removeAll(entry.getMissingPreferredTermLangage());
		if(!languages.isEmpty() &&levelMissing!=null){
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
				error("no file for this subject",def,TerminologyPackage.Literals.SUBJECT__NAME,-1);
			} else if(list.size()>1){
				error("more than one file for this subject",def,TerminologyPackage.Literals.SUBJECT__NAME,-1);
			}
		}
	}

	@Check
	public void uniqueEntryId(Entry entry) {
		Severity level=levels.getUniqueEntryIdLevel();
		if(level!=null){
			SubjectEntries entries=getEntries(entry);
			if(getDuplicate(entries, TerminologyPackage.Literals.ENTRY).contains(entry.getName())){
				createError(level, "entry id not unique: "+entry.getName(), TerminologyPackage.Literals.ENTRY__NAME);
			}
		}
	}

	@Check
	public void inverseEntryReference(Entry entry) {
		Severity level=levels.getEntryRefSymmetric();
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
			if(!term.isAllowDuplicate() && getDuplicate(entries, TerminologyPackage.Literals.TERM).contains(term.getName())){
				createError(level, "multiple definitions for term "+term.getName(), TerminologyPackage.Literals.TERM__NAME);
			}
		}
	}


	private Set<String> getDuplicate(final SubjectEntries entries, final EClass clazz){
		Set<String>result= cache.get(clazz, entries.eResource(), new Provider<Set<String>>() {
			public Set<String> get(){
				String terminologyName=getTerminology(entries).getName();
				Set<String> defined=new HashSet<String>();
				Set<String> duplicates=new HashSet<String>();
				IResourceDescriptions index = resourceDescriptionsProvider.getResourceDescriptions(entries.eResource());
				Iterable<IEObjectDescription> indexedEntries = index.getExportedObjectsByType(clazz);
				for (IEObjectDescription entry : indexedEntries) {
					if(terminologyName.equals(entry.getQualifiedName().getFirstSegment())){
						String entryId = entry.getQualifiedName().getLastSegment();
						if(defined.contains(entryId)){
							duplicates.add(entryId);
						}else{
							defined.add(entryId);
						}
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

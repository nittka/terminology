/*******************************************************************************
 * Copyright (c) 2013 itemis AG (http://www.itemis.eu).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package de.itemis.tooling.terminology.wikiexport

import de.itemis.tooling.terminology.generator.TerminologyGeneratorParticipant
import de.itemis.tooling.terminology.terminology.Entry
import de.itemis.tooling.terminology.terminology.EntryStatus
import de.itemis.tooling.terminology.terminology.Language
import de.itemis.tooling.terminology.terminology.SubjectEntries
import de.itemis.tooling.terminology.terminology.Term
import de.itemis.tooling.terminology.terminology.TermStatus
import de.itemis.tooling.terminology.terminology.Terminology
import java.text.Collator
import java.util.List
import java.util.Locale
import java.util.Map
import com.google.common.base.Optional

//TODO escaping
class WikiExporter extends TerminologyGeneratorParticipant {

	List<Language> languages

	public new() {
		super("wiki_export", "Wiki Exporter", "wiki")
	}
	
	override getFileContents(SubjectEntries entries) {
		languages=(entries.subject.eContainer as Terminology).languages
		languages.map[entries.subject.name+"_"+name+".wiki"->content(entries)]
	}

	def CharSequence content(Language l, SubjectEntries entries)'''
		||Benennung||weitere Benennungen «Optional.fromNullable(l.displayName).or(l.name)»||«languages.filter[it!=l].map["Benennungen "+Optional.fromNullable(it.displayName).or(it.name)].join("||")»||
		«FOR Entry e:sortedEntries(l,entries)»
			«e.getLine(l)»
		«ENDFOR»
	'''

	//TODO use primary language if it can be turned into a locale?
	val sorter=Collator::getInstance(Locale::GERMANY)
	def List<Entry> sortedEntries(Language l, SubjectEntries entries){
		val filtered=entries.entries.filter[it.showEntry(l)]
		//TODO in case of performance issues optimise here
		filtered.sort[Entry e1, Entry e2| 
			sorter.compare(
				e1.terms.findFirst[preferredForLanguage(l)].name, 
				e2.terms.findFirst[preferredForLanguage(l)].name
			)]
	}

	def boolean showEntry(Entry e,Language l){
		val status=e.meta.status.status
		status!=EntryStatus::ARCHIVE &&
		status!=EntryStatus::UNFINISHED &&
		e.terms.exists[preferredForLanguage(l)]
	}

	def boolean preferredForLanguage(Term t, Language l){
		t.status==TermStatus::PREFERRED && t.language==l
	}

	def getLine(Entry e,Language l){
		val Map<Language,List<Term>> termMap=newLinkedHashMap()
		languages.forEach[termMap.put(it,newArrayList())]
		e.terms.sortBy[status.value].forEach[termMap.get(language).add(it)]
		var result=new StringBuilder("|")
		//guaranteed to exist as we make sure l has a preferred term
		val pref=termMap.get(l).head
		result.append(e.defImage)
		result.append(pref.markUp)
		result.append("|")
		result.append(termMap.get(l).tail.map[markUp].join(", ").nonEmpty)
		for(Language l2:languages.filter[it!=l]){
			result.append("|")
			result.append(termMap.get(l2).map[markUp].join(", ").nonEmpty)
		}
		result.append("|")
		result.toString
	}

	def private String defImage(Entry e){
		if (e.definition.nullOrEmpty){
			return "!no_def.png! "
		}else{
			val escaped=e.definition.replaceAll("\n"," ").replaceAll(",","&#44;")
			return '''!def.png|title=«escaped»! '''
		}
	}

	def nonEmpty(String s){
		if(s.length==0) " "else s
	}

	def markUp(Term t){
		val string=t.name.trim
		switch t.status{
			case TermStatus::PREFERRED: '''*«string»*'''
			case TermStatus::PERMITTED: string
			case TermStatus::SUPERSEDED: '''{color:grey}«string»{color}'''
			case TermStatus::REJECTED: '''-«string»-'''
		}
	}
}
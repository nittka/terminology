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
package de.itemis.tooling.terminology.csvexport

import de.itemis.tooling.terminology.generator.TerminologyGeneratorParticipant
import de.itemis.tooling.terminology.terminology.Author
import de.itemis.tooling.terminology.terminology.Customer
import de.itemis.tooling.terminology.terminology.Entry
import de.itemis.tooling.terminology.terminology.Language
import de.itemis.tooling.terminology.terminology.Product
import de.itemis.tooling.terminology.terminology.Status
import de.itemis.tooling.terminology.terminology.SubjectEntries
import de.itemis.tooling.terminology.terminology.Term
import de.itemis.tooling.terminology.terminology.Terminology
import java.util.Map

class CsvExporter extends TerminologyGeneratorParticipant {

	//number of columns depends on the number of terms per language, will be stored here
	Map<Language,Integer> languageCount

	public new() {
		super("csv_export", "CSV Exporter", "csv")
	}
	
	override getFileContents(SubjectEntries entries) {
		newArrayList(entries.subject.name+".csv"->entries.content)
	}

	//header line, entry lines
	def CharSequence content(SubjectEntries entries)'''
		«entries.header»
		«FOR entry:entries.entries»
			«entry.content»
		«ENDFOR»
	'''

	//count max number of terms per language
	//then append the corresponding number of term headers
	def header(SubjectEntries entries){
	languageCount=entries.countLanguages
	'''
		id;status;created;createdBy;modified;modifiedBy;definition;source;relatedEntries;«FOR 
			language:languageCount.keySet SEPARATOR ';'»«FOR 
			count:1..languageCount.get(language) SEPARATOR ';'
				»«language.termHeader»«ENDFOR»«ENDFOR»
	'''
	}

	def termHeader(Language l)'''term_«l.name»;status;grammar;usage;products;customers'''

	def content(Entry entry)'''«new EntryContent(entry,languageCount)»'''

	def countLanguages(SubjectEntries entries){
		val Map<Language,Integer> termCount=newLinkedHashMap()
		(entries.subject.eContainer as Terminology).languages.forEach[termCount.put(it,countLanguages(entries,it))]
		termCount
	}
	def countLanguages(SubjectEntries entries, Language l){
		entries.entries.fold(0,[a,b| 
			var Integer temp= b.terms.filter[language==l].size
			if(a>temp) a else temp
		])
	}
}

//class wrapping a csv entry line
class EntryContent{
	val Entry entry;
	val Map<Language, Integer> count;
	new(Entry entry, Map<Language, Integer> languageCount){
		this.entry=entry
		count=languageCount
	}

	override toString(){
		newArrayList(
			entry.name,
			fromNullable(entry.meta.status),
			fromNullable(entry.meta.created),
			fromNullable(entry.meta.createdAuthor),
			fromNullable(entry.meta.modified),
			fromNullable(entry.meta.modifiedAuthor),
			fromNullable(entry.definition),
			fromNullable(entry.definitionSource),
			fromNullable(entry.relatedEntries),
			termEntries
		).join(';')
	}

	def private termEntries()'''«FOR language:count.keySet SEPARATOR ';'»«language.content»«ENDFOR»'''

	def private String content(Language l){
		val result=newArrayList()
		entry.terms.filter[language==l].forEach[result.add(it.content)]
		//append empty entries until max size is reached
		while(result.size<count.get(l)){
			result.add(";;;;;")
		}
		result.join(';')
	}

	def private content(Term t){
		newArrayList(
			t.name,
			t.status.getName,
			t.gr.name,
			t.usage.fromNullable,
			t.products.fromNullable,
			t.customers.fromNullable
		).join(';')
	}

	def String fromNullable(Object o){
		switch o {
			case o==null:""
			Iterable<?> : o.map[Object it|fromNullable].join(', ')
			Author : o.name
			Product : o.name
			Customer :o.name
			Entry :o.name
			Status: o.name
			default: {
				val string=o.toString
				//TODO potential for optimization
				//will fail if iterable contains elements with characters to be escaped
				//which is currently not the case
				if(string.indexOf(';')>=0 || string.indexOf('\n')>=0){
					'''"«string»"'''
				}else{
					string
				}
			}
		}
	}
}
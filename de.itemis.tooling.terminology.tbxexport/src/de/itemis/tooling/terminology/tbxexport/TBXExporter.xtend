/*******************************************************************************
 * Copyright (c) 2013 itemis AG (http://www.itemis.eu).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package de.itemis.tooling.terminology.tbxexport

import de.itemis.tooling.terminology.generator.TerminologyGeneratorParticipant
import de.itemis.tooling.terminology.terminology.Author
import de.itemis.tooling.terminology.terminology.Entry
import de.itemis.tooling.terminology.terminology.EntryStatus
import de.itemis.tooling.terminology.terminology.Gender
import de.itemis.tooling.terminology.terminology.GrNumber
import de.itemis.tooling.terminology.terminology.Pos
import de.itemis.tooling.terminology.terminology.Status
import de.itemis.tooling.terminology.terminology.SubjectEntries
import de.itemis.tooling.terminology.terminology.Term
import de.itemis.tooling.terminology.terminology.TermStatus
import de.itemis.tooling.terminology.terminology.Terminology
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.LinkedHashMap
import java.util.List
import java.util.Map

class TBXExporter extends TerminologyGeneratorParticipant {

	public new() {
		super("default_tbx_generator", "TBX Generator", "tbx")
	}

	override getFileContents(SubjectEntries entries) {
		newArrayList(entries.subject.name+".tbx"->entries.content)
	}

	//TODO Header anpassen
	def dispatch CharSequence getContent(SubjectEntries entries)'''
		<?xml version='1.0'?>
		<!DOCTYPE martif SYSTEM "TBXBasiccoreStructV02.dtd">
		
		<martif type="TBX-Basic" xml:lang="en-US">
			<martifHeader>
				<fileDesc>
					<titleStmt>
						<title>«(entries.subject.eContainer as Terminology).titleInfo»</title>
					</titleStmt>
					<sourceDesc>
						<p>This file was created («new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date)») using the eclipse terminology plugin (see https://github.com/nittka/terminology).</p>
						<p>«entries.subject.displayName»</p>
					</sourceDesc>
				</fileDesc>
				<encodingDesc>
					<p type="XCSURI">http://www.lisa.org/fileadmin/standards/tbx_basic/TBXBasicXCSV02.xcs
					</p>
				</encodingDesc>
			</martifHeader>

			<text>
				<body>
					«entries.entries.map[content].join("\n")»
				</body>
				«entries.backMatter»
			</text>
		</martif>
	'''

	def CharSequence getBackMatter(SubjectEntries entries)'''
		<back>
			<refObjectList type="respPerson">
				«(entries.subject.eContainer as Terminology).authors.map[authorContent].join("\n")»
			</refObjectList>
		</back>
	'''

	//TODO email?
	//<item type="email">ich@mail.de</item>
	def CharSequence getAuthorContent(Author k)'''
		<refObject id="«k.backMatter.toId»">
			<item type="fn">«k.backMatter.displayString»</item>
		</refObject>
	'''

	def firstTermString(Entry e){
		val sortedTerms=e.terms.sortBy[status.value]
		if (sortedTerms.size>0) sortedTerms.head.name else ""
	}

	def dispatch CharSequence getContent(Entry e)'''
		<termEntry id="«e.name»">
			«e.definitionContent»
			<admin type="elementWorkingStatus">«e.meta.status.localize»</admin>
			<descrip type="subjectField">«(e.eContainer as SubjectEntries).subject.name»</descrip>
			«FOR related:e.relatedEntries»
				<descrip type"relatedConcept" target="«related.name»">«related.firstTermString»</descrip> 
			«ENDFOR»
			<transacGrp>
				<transac type="transactionType">origination</transac>
				<transacNote type="responsibility" target="«e.meta.createdAuthor.backMatter.toId»">«e.meta.createdAuthor.backMatter.toSimpleId»</transacNote>
				<date>«e.meta.created.formatDate»</date>
			</transacGrp>
			«IF e.meta.modified.hasContent»
				<transacGrp>
					<transac type="transactionType">modification</transac>
					<transacNote type="responsibility" target="«e.meta.modifiedAuthor.backMatter.toId»">«e.meta.modifiedAuthor.backMatter.toSimpleId»</transacNote>
					<date>«e.meta.modified.formatDate»</date>
				</transacGrp>
			«ENDIF»
			«e.languageSets»
		</termEntry>
	'''

	def getDefinitionContent(Entry e)'''
		«IF e.definition.hasContent»
		<descripGrp>
			<descrip type="definition">«e.definition»</descrip>
		«IF e.definitionSource.hasContent»
			<admin type="source">«e.definitionSource»</admin>
		«ENDIF»
		</descripGrp>
	«ENDIF»
	'''

	def CharSequence getLanguageSets(Entry e){
		var Map<String, List<CharSequence>> setMap=new LinkedHashMap<String, List<CharSequence>>()
		for(Term b:e.terms){
			val language=b.language.name
			if(!setMap.containsKey(language)){
				setMap.put(language,new ArrayList)
			}
			setMap.get(language).add(b.termContent)
		}
		val List<CharSequence>resultList=new ArrayList
		for(String l:setMap.keySet){
			if(!setMap.get(l).empty){
				resultList.add('''
					<langSet xml:lang="«l»">
						«setMap.get(l).join("\n")»
					</langSet>
				''')
			}
		}
		resultList.join("\n")
	}
	//TODO customer, products
	def termContent(Term b){
		val boolean grPresent=b.gr.pos!==Pos._NA
	'''
		<tig>
			<term>«b.name»</term>
			«IF grPresent && b.gr.pos!=null»
				<termNote type="partOfSpeech">«b.gr.pos.localize»</termNote>
			«ENDIF»
			«IF grPresent && b.gr.gender!=null»
				<termNote type="grammaticalGender">«b.gr.gender.localize»</termNote>
			«ENDIF»
			«IF grPresent && b.gr.number!=null»
				<termNote type="grammaticalNumber">«b.gr.number.localize»</termNote>
			«ENDIF»
			<termNote type="normativeAuthorization">«b.status.localize»</termNote>
			«IF b.usage.hasContent»
				<termNote type="usageNote">«b.usage»</termNote>
			«ENDIF»
			«FOR product:b.products»
				<admin type="productSubset">«product.name»</admin>
			«ENDFOR»
			«FOR customer:b.customers»
				<admin type="customerSubset">«customer.name»</admin>
			«ENDFOR»
		</tig>	'''
	}

	def localize(Pos w){
		switch (w){
			case Pos::_NA: throw new IllegalStateException
			case Pos::ADJECTIVE:"adjective"
			case Pos::ADVERB:"adverb"
			case Pos::_NOUN:"noun"
			case Pos::INTRANSITIVE_VERB:"intransitive_verb"
			case Pos::TRANSITIVE_VERB:"transitive_verb"
		}
	}
	def localize(Gender g){
		switch (g){
			case Gender::MASCULINE:"masculine"
			case Gender::FEMININE:"feminine"
			case Gender::NEUTER:"neuter"
			case Gender::OTHER:"otherGender"
		}
	}
	def localize(GrNumber z){
		switch (z){
			case GrNumber::SINGULAR:"singular"
			case GrNumber::PLURAL:"plural"
		}
	}
	def localize(TermStatus s){
		switch (s){
			//TODO superseded
			case TermStatus::REJECTED:"deprecatedTerm"
			case TermStatus::PREFERRED:"preferredTerm"
			case TermStatus::PERMITTED:"admittedTerm"
			case TermStatus::SUPERSEDED:"supersededTerm"
		}
	}
  
 
	def localize(Status s){
		switch (s.status){
			case EntryStatus::ARCHIVE:"archiveElement"
			case EntryStatus::EXPORTED:"exportedElement"
			case EntryStatus::CONSOLIDATED:"consolidatedElement"
			case EntryStatus::IMPORTED:"importedElement"
			case EntryStatus::_OK:"workingElement"
			case EntryStatus::UNFINISHED:"starterElement"
		}
	}
	def boolean hasContent(String s){
		s!=null && s.trim.length>0
	}

//	val sourceDateFormat=new SimpleDateFormat("dd.MM.yy")
//	val targetDateFormat=new SimpleDateFormat("yyyy-MM-dd")
	def formatDate(String string) {
//		val date=sourceDateFormat.parse(string)
//		targetDateFormat.format(date)
		string
	}

	def create _this:new BackMatter() getBackMatter(Author author){
		_this.setAuthor(author)
	}
}

class BackMatter{
	var Author author
	var String backMatterId 
	def void setAuthor(Author a){
		author=a
		val term=a.eContainer as Terminology
		backMatterId=term.name+"_"+(term.authors.indexOf(a)+1)
	}

	def String toId(){
		backMatterId
	}

	def String toSimpleId(){
		author.name
	}

	def String displayString(){
		author.displayName
	}
}
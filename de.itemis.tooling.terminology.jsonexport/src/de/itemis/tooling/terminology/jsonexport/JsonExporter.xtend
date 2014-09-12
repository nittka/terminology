/*******************************************************************************
 * Copyright (c) 2013 itemis AG (http://www.itemis.eu).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package de.itemis.tooling.terminology.jsonexport

import de.itemis.tooling.terminology.generator.SubjectEntriesSiblings
import de.itemis.tooling.terminology.generator.TerminologyGeneratorParticipant
import de.itemis.tooling.terminology.terminology.SubjectEntries
import de.itemis.tooling.terminology.terminology.Terminology
import java.text.Collator
import java.util.ArrayList
import java.util.Locale

class JsonExporter extends TerminologyGeneratorParticipant {

	val sorter=Collator::getInstance(Locale::GERMANY)
	public new() {
		super("json_export", "Json Exporter", "json")
	}

	override getFileContents(SubjectEntries entries) {
		val terminology=entries.subject.eContainer as Terminology
		val siblings=entries.eAdapters.findFirst[it instanceof SubjectEntriesSiblings]
		if(siblings!==null){
			val allSubjects=(siblings as SubjectEntriesSiblings).allEntries
			val allTerms=new ArrayList(
				allSubjects.map[getEntries]
				.flatten
				.map[terms].flatten
				.sort[t1,t2|sorter.compare(t1.name, t2.name)].toList)
			return #[
				"terms.json"->new JsonFileContent().content(terminology, allTerms),
				"terms.html"->new HtmlFileContent().html(terminology,allSubjects)
				]
		}else{
			return #[]
		}
	}
}
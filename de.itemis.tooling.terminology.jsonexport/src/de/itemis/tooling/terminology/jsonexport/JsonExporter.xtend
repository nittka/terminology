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
	new() {
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
				.sortWith[t1,t2|sorter.compare(t1.name, t2.name)].toList)
			return #[
				"terms.json"->new JsonFileContent().content(terminology, allTerms),
				"terms.html"->new HtmlFileContent().html(terminology,allSubjects)
				]
		}else{
			return #[]
		}
	}
}
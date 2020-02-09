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

import de.itemis.tooling.terminology.terminology.Entry
import de.itemis.tooling.terminology.terminology.Term
import de.itemis.tooling.terminology.terminology.Terminology
import de.itemis.tooling.terminology.terminology.WithNameAndDisplayName
import java.util.List
import java.util.concurrent.atomic.AtomicInteger

import static extension de.itemis.tooling.terminology.jsonexport.JsonStringHelper.*
import de.itemis.tooling.terminology.terminology.SubjectEntries

package class JsonFileContent {
	
	def CharSequence content(Terminology terminology, List<Term> terms){
		'''
			«terminologyContent(terminology)»
			«dataContent(terms, new AtomicInteger(0))»
		'''
	}

	def private CharSequence terminologyContent(Terminology terminology)'''
		terminology={
		  "name": "«terminology.titleInfo»",
		  «nameDisplayNameList("entry_status", terminology.status)»,
		  «nameDisplayNameList("languages", terminology.languages)»,
		  «nameDisplayNameList("subjects", terminology.subjects)»,
		  «nameDisplayNameList("authors", terminology.authors)»,
		  «nameDisplayNameList("customers", terminology.customers)»,
		  «nameDisplayNameList("products", terminology.products)»
		};
	'''

	def private CharSequence nameDisplayNameList(String id, List<? extends WithNameAndDisplayName> elements)'''
		"«id»":[
		  «elements.join(",\n",[nameDisplayNameEntry])»
		]
	'''

	def private CharSequence nameDisplayNameEntry(WithNameAndDisplayName element)
	'''{"name": "«element.name»", "display": "«element.displayName.or(element.name)»"}'''

	def private CharSequence dataContent(List<Term> terms, AtomicInteger counter)'''
		data=[
			«FOR term:terms SEPARATOR ","»
		  	«content(term, '''«counter.incrementAndGet»''')»
			«ENDFOR»
		];
	'''
	def private CharSequence content(Term t, String id)'''
		{
		  "id": "«id»",
		  "term": "«t.name.toJsonString»",
		  "term_status": "«t.status.getName»",
		  "usage": "«(t.usage?:"").toJsonString»",
		  "customers": [«t.customerList.join(",")»],
		  "products": [«t.productList.join(",")»],
		  "language":"«t.language.name»",
		  «entryContent(t.eContainer as Entry)»
		}
	'''
	
	def private List<String> getCustomerList(Term term){
		val List<String> singleCustomers=newArrayList()
		if(term.customers===null || term.customers.empty){
			singleCustomers.add('{"id":"none"}')
		}else{
			term.customers.forEach[
				singleCustomers.add('''{"id":"«name»"}''')
			]
		}
		return singleCustomers
	}

	def private List<String> getProductList(Term term){
		val List<String> singleProducts=newArrayList()
		if(term.products===null || term.products.empty){
			singleProducts.add('{"id":"none"}')
		}else{
			term.products.forEach[
				singleProducts.add('''{"id":"«name»"}''')
			]
		}
		return singleProducts
	}

	def private CharSequence entryContent(Entry e)'''
		  "entry_id": "«e.name»",
		  "create_author": "«e.meta.createdAuthor.name»",
		  "modify_author": "«e.meta.modifiedAuthor?.name»",
		  "subject": "«(e.eContainer as SubjectEntries).subject.name»",
		  "entry_status":"«e.meta.status.name»",
		  "entry_definition": "«(e.definition?:"").toJsonString»"
	'''

	def private String toJsonString(String s){
		nlEspcape(s," ")
	}
}
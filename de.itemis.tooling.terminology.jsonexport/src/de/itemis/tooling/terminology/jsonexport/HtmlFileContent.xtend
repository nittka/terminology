package de.itemis.tooling.terminology.jsonexport

import static extension de.itemis.tooling.terminology.jsonexport.JsonStringHelper.*

import de.itemis.tooling.terminology.terminology.Entry
import de.itemis.tooling.terminology.terminology.SubjectEntries
import de.itemis.tooling.terminology.terminology.Term
import de.itemis.tooling.terminology.terminology.Terminology
import java.util.List
import de.itemis.tooling.terminology.terminology.TermStatus

class HtmlFileContent {
	
		def CharSequence html(Terminology terminology, List<SubjectEntries> subjects)'''
	<!DOCTYPE HTML>
	<html xmlns="http://www.w3.org/1999/xhtml">
	  <head>
	    <meta content="text/html; charset=UTF-8" http-equiv="Content-Type"/>
	    <link href="../style.css" media="screen" rel="stylesheet" type="text/css"/>
	    <script src="../jquery/jquery.min.js" type="text/javascript"></script>
	    <script src="../jquery/jquery-ui-1.10.2.custom.min.js" type="text/javascript"></script>
	    <script src="../localize.js" type="text/javascript"></script>
	    <style type="text/css">
	    <!--
	      .subject{display:none;}
	    //-->
	    </style>
	  </head>
	  <body>
	    <div id="languageToggle"></div>
	    <h1 class="terminology">«terminology.titleInfo»</h1>
	    «FOR subject:subjects»
	      «html(subject)»
	    «ENDFOR»
	  </body>
	</html>
	'''

	def private CharSequence html(SubjectEntries subject){
		val subjectName=subject.subject.displayName
		'''
			<h2 class="subject_heading">«subjectName»</h2>
			«FOR entry:subject.entries»
			  «html(entry,subjectName)»
			«ENDFOR»
		'''
	}
	def private CharSequence html(Entry entry, String subject)'''
	<div class="entry_container">
	  <div class="entry" id='«entry.name»'>
	    <div class="subject"><span class="subject_label"></span>«subject»</div>
	    <div class="entry_status"><span class="entry_status_label"></span>«entry.meta.status.displayName.or(entry.meta.status.name)»</div>
	    «entry.htmlDefinition»
	    «FOR term:entry.terms»
	      «html(term)»
	    «ENDFOR»
	  </div>«IF(!entry.relatedEntries.nullOrEmpty)»<span class="see_also_label"></span><span class="see_also">«entry.relatedEntries.map[link].join(", ")»</span>«ENDIF»
	</div>
	'''

	def private CharSequence link(Entry entry)'''
	<a href="#«entry.name»">«entry.name»</a> («entry.terms.filter[status==TermStatus.PREFERRED].map[name].join(", ")»)
	'''

	def private CharSequence html(Term term)'''
	<div class="term"><span class="term_label"></span><span class="«term.status.getName»">«term.name.toJsonString»</span> <span class="term_status">«term.status.getName»</span>
	  <div class="language"><span class="language_label"></span>«term.language.name»</div>
	  <div class="grammar"><span class="grammar_label"></span>«term.gr.displayName»</div>
	  «IF !(term.usage.nullOrEmpty)»<div class="usage"><span class="usage_label"></span>«term.usage.toJsonString»</div>«ENDIF»
	  «IF !(term.products.nullOrEmpty)»<div class="products"><span class="products_label"></span>«term.products.map[it.displayName].join(", ")»</div>«ENDIF»
	  «IF !(term.customers.nullOrEmpty)»<div class="customers"><span class="customers_label"></span>«term.customers.map[it.displayName].join(", ")»</div>«ENDIF»
	</div>
	'''

	def private CharSequence htmlDefinition(Entry it){
		if(definition.nullOrEmpty){
			""
		}else{
			val source=if(definitionSource.nullOrEmpty)"" else''' («definitionSource»)'''.toString.toJsonString
			'''<div class="definition"><span class="definition_label"></span>«definition.toJsonString»«source»</div>'''
		}
	}

	def private String toJsonString(String s){
		nlEspcape(s,"<p>")
	}
}
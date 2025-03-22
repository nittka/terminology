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
package de.itemis.tooling.terminology.formatting

import de.itemis.tooling.terminology.terminology.SubjectEntries
import de.itemis.tooling.terminology.tests.TerminologyInjectorProvider
import com.google.inject.Inject
import org.eclipse.xtext.formatting.INodeModelFormatter
import org.eclipse.xtext.resource.SaveOptions
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.util.ParseHelper
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(XtextRunner)
@InjectWith(TerminologyInjectorProvider)
class IllustrateInvokationTest {

	@Inject extension ParseHelper<SubjectEntries>
	//	@Inject extension ValidationTestHelper
	@Inject INodeModelFormatter formatter

	@Test
	def justToIllustrate() {
		'''     
		
		
			Subject tada.tidum
	 Entry allg0001 {
				@Feedback    "dkghdfgldg ldlkg 
				dg dfg d
				g dsf gdfg"
  Metadata{
    Status Ok
    created mbr 2012-11-26
  }
  Definition ""Term
  "Administrations-Handbuch" Status preferred
    Language de
    Gr n Usage 
    
    "sflsjfsljf
    
agdgd
 gds
  gf
   d
    g
     ds
      gsd
    dg" Products
    p1
    , 
    p2 Customers
    
    
    c1
    
    ,
    
    c2 Term
  "Administrations-Handbuch" Status preferred
    Language de
    Gr n Usage ""
Products p1 
Customers c1
Term
  "Administrations-Handbuch" Status preferred
    Language de
    Gr n Usage ""
Products p1
Customers c1,c2
}


Entry allg0002 {


				@Feedback    "dkghdfgldg ldlkg 
				dg dfg d
				g dsf gdfg"


  Metadata{
    Status O
    created 
    	mbr 2012-11-26
  }
  
  Definition ""
Term
  "GIS"
  Status preferred Language de Gr n
    Usage 
    
    
    "Adler"
  Term
  "Geografisches Informationssystem"
  
  Status permitted
    Language de
    
    
    Gr n Customers c1, c2, c3 Term"GIS"Status permitted
    Language en Gr noun
    
    
    @DuplicateOK
    
    
    
  Term "Geographic Information System"
Status permitted Language en
  Gr noun Products p1,
  
  p2
}@MissingPreferredTermOK(de , en,

nl) Entry allg0003 {


				@Feedback    "dkghdfgldg ldlkg 
				dg dfg d
				g dsf gdfg"


  Metadata{
    Status Ok
    created mbr 2012-11-26
  }  Definition "Adddada"	Source 
  
  "tada" RelatedEntries 
  
  
  m1, m2,m3
	@DuplicateOK
Term
  "maintenance" Status preferred
    Language en
    Gr noun
	@DuplicateOK
Term
  "Instandhaltung" Status preferred
    Language de
    Gr f

}
		'''.format2
	}

	def void format2(CharSequence model) {
		val root = (model.parse.eResource as XtextResource).parseResult.rootNode
		println(formatter.format(root, 0, Integer.MAX_VALUE).formattedText)
	}

	def void format(CharSequence model) {
		val parsed=model.parse
		val saveOptions = SaveOptions::newBuilder.format.options.toOptionsMap
		parsed.eResource.save(System::out, saveOptions)
	}
}
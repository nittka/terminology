package de.itemis.tooling.terminology.validation

import de.itemis.tooling.terminology.TerminologyInjectorProvider
import de.itemis.tooling.terminology.terminology.SubjectEntries
import javax.inject.Inject
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith
import de.itemis.tooling.terminology.terminology.Terminology
import de.itemis.tooling.terminology.terminology.TerminologyPackage
import java.util.ArrayList

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(TerminologyInjectorProvider))
class ValidateDuplicateTermsTests {

	@Inject ParseHelper<Terminology> terminologyParser
	@Inject extension ParseHelper<SubjectEntries> 
	@Inject extension ValidationTestHelper

	val termClass=TerminologyPackage::Literals::TERM

	def private defaultMetaData()'''
		Metadata {
			Status OK
			created me 2016-03-03
		}
	'''

	def private String prefTerm(String term, String language)'''
		Term "«term»"
			Status preferred
			Language «language»
			Gr n
	'''

	def private String permTerm(String term, String language)'''
		Term "«term»"
			Status permitted
			Language «language»
			Gr n
	'''

	

	def private termDefinition(String terminologyName)'''
		Terminology «terminologyName» "«terminologyName»"
		Definitions
		
		Subjects
		  subject1 "",
		  subject2 ""
		Status OK "OK" _OK
		Languages de "German", en "English"
		Gr n "neuter noun" _noun neuter singular
		Authors me "Me"
	'''

	def getParsed(CharSequence... models){
		val rs=terminologyParser.parse("Example".termDefinition).eResource.resourceSet
		//new ArrayList ensures that the models are actually parsed
		new ArrayList(models.map[
			it.parse(rs)
		])
	}

	def private void assertNoIssues(CharSequence... models){
		models.parsed.forEach[
			it.assertNoIssues
		]
	}

	@Test
	def singleTerms() {
		assertNoIssues('''
			Subject Example.subject1
				Entry e1{
					«defaultMetaData»
					Definition "def1"
					«prefTerm("noDuplicate","de")»
					«prefTerm("noDuplicate2","en")»
				}
		''')
	}

	@Test
	def sameTermsDiffLanguageSameEntry() {
		assertNoIssues('''
			Subject Example.subject1
				Entry e1{
					«defaultMetaData»
					Definition "def1"
					«prefTerm("duplicate","de")»
					«prefTerm("duplicate","en")»
				}
		''')
	}

	@Test
	def sameTermsDiffLanguageDifferentEntries() {
		assertNoIssues('''
			Subject Example.subject1
				Entry e1{
					«defaultMetaData»
					Definition "def1"
					«prefTerm("duplicate","de")»
					«prefTerm("noDuplicate","en")»
				}
				Entry e2{
					«defaultMetaData»
					Definition "def1"
					«prefTerm("noDuplicate","de")»
					«prefTerm("duplicate","en")»
				}
		''')
	}

	@Test
	def sameTermsDiffLanguageDifferentModels() {
		assertNoIssues('''
			Subject Example.subject1
				Entry e1{
					«defaultMetaData»
					Definition "def1"
					«prefTerm("duplicate","de")»
					«prefTerm("noDuplicate","en")»
				}
		''',
		'''
			Subject Example.subject2
				Entry e2{
					«defaultMetaData»
					Definition "def1"
					«prefTerm("noDuplicate","de")»
					«prefTerm("duplicate","en")»
				}
		''')
	}

	@Test
	def sameTermSameEntry() {
		getParsed('''
			Subject Example.subject1
				Entry e1{
					«defaultMetaData»
					Definition "def1"
					«prefTerm("duplicate","de")»
					«permTerm("duplicate","de")»
					«prefTerm("noDuplicate","en")»
				}
		''').get(0).assertWarning(termClass,null)
	}

	@Test
	def sameTermDifferentEntry() {
		getParsed('''
			Subject Example.subject1
				Entry e1{
					«defaultMetaData»
					Definition "def1"
					«prefTerm("duplicate","de")»
					«prefTerm("noDuplicate","en")»
				}
				Entry e2{
					«defaultMetaData»
					Definition "def1"
					«prefTerm("duplicate","de")»
					«prefTerm("noDuplicat2","en")»
				}
		''').forEach[assertWarning(termClass,null)]
	}

	@Test
	def sameTermDifferentModels() {
		getParsed('''
			Subject Example.subject1
				Entry e1{
					«defaultMetaData»
					Definition "def1"
					«prefTerm("duplicate","de")»
					«prefTerm("noDuplicate","en")»
				}
			''',
			'''
			Subject Example.subject2
				Entry e2{
					«defaultMetaData»
					Definition "def1"
					«prefTerm("duplicate","de")»
					«prefTerm("noDuplicat2","en")»
				}
		''').forEach[assertWarning(termClass,null)]
	}

	@Test
	def sameTermDifferentModelsSuppressWarning() {
		val parsedModels=getParsed('''
			Subject Example.subject1
				Entry e1{
					«defaultMetaData»
					Definition "def1"
					@DuplicateOK
					«prefTerm("duplicate","de")»
					«prefTerm("noDuplicate","en")»
				}
			''',
			'''
			Subject Example.subject2
				Entry e2{
					«defaultMetaData»
					Definition "def1"
					«prefTerm("duplicate","de")»
					«prefTerm("noDuplicat2","en")»
				}
		''')
		parsedModels.get(0).assertNoIssues
		parsedModels.get(1).assertWarning(termClass,null)
	}

	@Test
	def sameTermDifferentTerminologies() {
		val rs=terminologyParser.parse("E1".termDefinition).eResource.resourceSet
		terminologyParser.parse("E2".termDefinition, rs)
		
		val model1='''
			Subject E1.subject1
				Entry e1{
					«defaultMetaData»
					Definition "def1"
					«prefTerm("duplicate","de")»
					«prefTerm("noDuplicate","en")»
				}
			'''.parse(rs)
		val model2='''
			Subject E2.subject1
				Entry e1{
					«defaultMetaData»
					Definition "def1"
					«prefTerm("duplicate","de")»
					«prefTerm("noDuplicat","en")»
				}
		'''.parse(rs)
		model1.assertNoIssues
		model2.assertNoIssues
	}
}
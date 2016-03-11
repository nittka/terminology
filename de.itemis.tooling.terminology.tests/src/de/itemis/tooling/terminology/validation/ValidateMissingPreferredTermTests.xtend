package de.itemis.tooling.terminology.validation

import de.itemis.tooling.terminology.TerminologyInjectorProvider
import de.itemis.tooling.terminology.terminology.SubjectEntries
import javax.inject.Inject
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import de.itemis.tooling.terminology.terminology.Terminology
import de.itemis.tooling.terminology.terminology.TerminologyPackage
import java.util.ArrayList
import org.junit.Before

@RunWith(typeof(XtextRunner))
@InjectWith(typeof(TerminologyInjectorProvider))
class ValidateMissingPreferredTermsTests {

	@Inject ParseHelper<Terminology> terminologyParser
	@Inject extension ParseHelper<SubjectEntries> 
	@Inject extension ValidationTestHelper

	val entryClass=TerminologyPackage::Literals::ENTRY

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
		Languages de "German", en "English", it "Italian"
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

	def private void setCheckIncludeUnusedLanguages(boolean value){
		val field=TerminologyFixedSeverityLevels.getDeclaredField("checkMissingPreferredTermForLanguageWithoutTerms")
		field.setAccessible(true)
		field.setBoolean(TerminologyFixedSeverityLevels,value)
	}

	@Before
	def restoreDefault() {
		setCheckIncludeUnusedLanguages(true)
	}

	@Test
	def allPreferredPresent() {
		assertNoIssues('''
			Subject Example.subject1
				Entry e1{
					«defaultMetaData»
					Definition "def1"
					«prefTerm("bli","de")»
					«prefTerm("bla","en")»
					«prefTerm("blubbs","it")»
				}
		''')
	}

	@Test
	def suppressLanguagesAnnotation() {
		assertNoIssues('''
			Subject Example.subject1
				@MissingPreferredTermOK(en, it)
				Entry e1{
					«defaultMetaData»
					Definition "def1"
					«prefTerm("bli","de")»
					«permTerm("blubbs","it")»
				}
		''')
	}

	@Test
	def preferredTermMissing() {
		val model=getParsed('''
			Subject Example.subject1
				Entry e1{
					«defaultMetaData»
					Definition "def1"
					«prefTerm("bli","de")»
					«prefTerm("bla","en")»
					«permTerm("blubbs","it")»
				}
		''').get(0)
		model.assertWarning(entryClass, null, "it")
		Assert.assertEquals(1, model.validate.size)
	}

	@Test
	def suppressLanguageAnnotation() {
		val model=getParsed('''
			Subject Example.subject1
				@MissingPreferredTermOK(en)
				Entry e1{
					«defaultMetaData»
					Definition "def1"
					«prefTerm("bli","de")»
					«permTerm("blubbs","it")»
				}
		''').get(0)
		model.assertWarning(entryClass, null, "it")
		Assert.assertEquals(1, model.validate.size)
	}

	@Test
	def unusedLanguageEn() {
		setCheckIncludeUnusedLanguages(false)
		assertNoIssues('''
			Subject Example.subject1
				@MissingPreferredTermOK(it)
				Entry e1{
					«defaultMetaData»
					Definition "def1"
					«prefTerm("bli","de")»
					«permTerm("blubbs","it")»
				}
		''')
	}

	@Test
	def unusedEnUsedIt() {
		setCheckIncludeUnusedLanguages(false)
		val model=getParsed('''
			Subject Example.subject1
				Entry e1{
					«defaultMetaData»
					Definition "def1"
					«prefTerm("bli","de")»
					«permTerm("blubbs","it")»
				}
		''').get(0)
		model.assertWarning(entryClass, null, "it")
		Assert.assertEquals(1, model.validate.size)
	}

}
/*******************************************************************************
 * Copyright (c) 2013 itemis AG (http://www.itemis.eu).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package de.itemis.tooling.terminology.ui.generator

import de.itemis.tooling.terminology.generator.SubjectEntriesSiblings
import de.itemis.tooling.terminology.generator.TerminologyGeneratorParticipant
import de.itemis.tooling.terminology.terminology.SubjectEntries
import de.itemis.tooling.terminology.terminology.Terminology
import java.util.List
import javax.inject.Inject
import javax.inject.Provider
import org.eclipse.core.resources.IProject
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.IFileSystemAccess
import org.eclipse.xtext.generator.IGenerator
import org.eclipse.xtext.resource.XtextResourceSet
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider

class TerminologyUIGenerator implements IGenerator {

	@Inject
	private Provider<TerminologyGenerators> generators;
	@Inject
	ResourceDescriptionsProvider index
	@Inject
	Provider<XtextResourceSet> sets

	override void doGenerate(Resource resource, IFileSystemAccess fsa) {
		val root=resource.contents.get(0)
		if(root instanceof SubjectEntries){
			addSiblingAdapter(root as SubjectEntries)
		}
		root.createFiles(fsa)
	}

	def void addSiblingAdapter(SubjectEntries root){
		val terminology= root.subject.eContainer as Terminology
		val termURI=terminology.eResource.URI
		val descs=index.getResourceDescriptions(root.eResource)
		val List<URI> siblings=newArrayList
		descs.allResourceDescriptions.filter[referenceDescriptions.exists[targetEObjectUri.trimFragment.equals(termURI)]].forEach[
			siblings.add(URI)
		]
		siblings.add(termURI)
		val adapter=new SubjectEntriesSiblings(sets.get(), siblings)
		root.eAdapters.add(adapter)
	}

	def dispatch void createFiles(EObject o, IFileSystemAccess fsa){}

	def dispatch createFiles(SubjectEntries c, IFileSystemAccess fsa){
		var IProject p=null;
		if(c.eResource.URI.platformResource){
			var projectName=c.eResource.URI.segment(1)
			p=ResourcesPlugin::workspace.root.getProject(projectName)
			if(!p.exists){
				p=null
			}
		}
		val instances=generators.get.getGenerators(p).filter[active]
		if(instances.size>0){
			val terminologyName=(c.subject.eContainer as Terminology).name
			instances.forEach[it.processGenerator(c,terminologyName,fsa)]
		}
		return
	}

	def processGenerator(TerminologyGeneratorParticipant generator, SubjectEntries entries, String terminologyName, IFileSystemAccess fsa){
		val folderName=folderName(terminologyName,generator)
		generator.getFileContents(entries)?.forEach[fsa.generateFile(folderName+it.key,it.value)]
	}

	def folderName(String terminologyName, TerminologyGeneratorParticipant generator){
		var folderName=terminologyName+"/"
		if(generator.folder!=null && generator.folder.length>0){
			folderName=folderName+generator.folder
		}
		if(!folderName.endsWith("/")){
			folderName=folderName+"/"
		}
	}
}

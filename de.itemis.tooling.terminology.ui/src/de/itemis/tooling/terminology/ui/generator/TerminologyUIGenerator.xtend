/*******************************************************************************
 * Copyright (c) 2013 itemis AG (http://www.itemis.eu).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package de.itemis.tooling.terminology.ui.generator

import de.itemis.tooling.terminology.generator.TerminologyGeneratorParticipant
import de.itemis.tooling.terminology.terminology.SubjectEntries
import de.itemis.tooling.terminology.terminology.Terminology
import javax.inject.Inject
import javax.inject.Provider
import org.eclipse.core.resources.IProject
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.IFileSystemAccess
import org.eclipse.xtext.generator.IGenerator

class TerminologyUIGenerator implements IGenerator {

	@Inject
	private Provider<TerminologyGenerators> generators;

	override void doGenerate(Resource resource, IFileSystemAccess fsa) {
		resource.contents.get(0).createFiles(fsa)
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

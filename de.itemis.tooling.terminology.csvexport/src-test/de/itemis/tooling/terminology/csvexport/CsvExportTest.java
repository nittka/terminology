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
package de.itemis.tooling.terminology.csvexport;

import com.google.inject.Inject;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.XtextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.itemis.tooling.terminology.terminology.SubjectEntries;
import de.itemis.tooling.terminology.tests.TerminologyInjectorProvider;

@RunWith(XtextRunner.class)
@InjectWith(TerminologyInjectorProvider.class)
@SuppressWarnings("all")
public class CsvExportTest {
	@Inject
	private IResourceServiceProvider services;

	@Test
	public void justToIllustrate() {
		final XtextResourceSet rs = this.services
				.<XtextResourceSet> get(XtextResourceSet.class);
		Resource file1 = rs.getResource(
				URI.createFileURI("testfiles/Example.terms"), true);
		Resource file2 = rs.getResource(
				URI.createFileURI("testfiles/Example_app.terms"), true);
		Resource file3 = rs.getResource(
				URI.createFileURI("testfiles/Example_ctrl.terms"), true);

		SubjectEntries subject = (SubjectEntries)file2.getContents().get(0);
		CsvExporter exporter=new CsvExporter();
		System.out.println(exporter.getFileContents(subject));
	}
}

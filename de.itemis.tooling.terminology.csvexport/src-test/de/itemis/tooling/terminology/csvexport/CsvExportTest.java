package de.itemis.tooling.terminology.csvexport;

import de.itemis.tooling.terminology.TerminologyInjectorProvider;
import de.itemis.tooling.terminology.csvexport.CsvExporter;
import de.itemis.tooling.terminology.terminology.SubjectEntries;

import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;

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

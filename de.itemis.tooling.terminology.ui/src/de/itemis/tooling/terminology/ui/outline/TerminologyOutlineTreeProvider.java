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
package de.itemis.tooling.terminology.ui.outline;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.AbstractOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider;
import org.eclipse.xtext.ui.editor.outline.impl.DocumentRootNode;
import org.eclipse.xtext.ui.editor.outline.impl.EObjectNode;
import org.eclipse.xtext.util.TextRegion;

import com.google.common.base.Optional;

import de.itemis.tooling.terminology.terminology.Entry;
import de.itemis.tooling.terminology.terminology.Language;
import de.itemis.tooling.terminology.terminology.SubjectEntries;
import de.itemis.tooling.terminology.terminology.Term;
import de.itemis.tooling.terminology.terminology.Terminology;

/**
 * customization of the default outline structure
 * 
 */
public class TerminologyOutlineTreeProvider extends DefaultOutlineTreeProvider {

	protected boolean _isLeaf(Term modelElement) {
		return true;
	}

	@Override
	protected void _createChildren(final DocumentRootNode parentNode,
			EObject modelElement) {
		if(modelElement instanceof SubjectEntries){
			final SubjectEntries entries = (SubjectEntries)modelElement;
			final EList<Language> languages;
			try {
				languages = ((Terminology) entries.getSubject().eContainer()).getLanguages();
			} catch (NullPointerException e) {
				new AbstractOutlineNode(parentNode,(Image)null,"No outline available. Make sure,",true) {};
				new AbstractOutlineNode(parentNode,(Image)null,"the project has the Xtext nature and",true) {};
				new AbstractOutlineNode(parentNode,(Image)null,"start a clean build.",true) {};
				return;
			}
			createLanguageNodes(parentNode, entries, languages);
		}
	}

	void _createChildren(final IOutlineNode parentNode, SubjectEntries modelElement){}

	void createLanguageNodes(IOutlineNode parent, final SubjectEntries entries, final EList<Language> languages){
		for (final Language language : languages) {
			EObjectNode ln = new EObjectNode(entries, parent,(Image)null,Optional.fromNullable(language.getDisplayName()).or(language.getName()),false) {
				boolean created=false;
				@Override
				public List<IOutlineNode> getChildren() {
					if(!created){
							createLanguageChildren(this, language, entries);
						}
						created=true;
					return super.getChildren();
				}
			};
			ICompositeNode node = NodeModelUtils.getNode(entries);
			ln.setTextRegion(new TextRegion(node.getOffset(), node.getLength()));
		}
	}

	private void createLanguageChildren(IOutlineNode parentNode, Language language,
			SubjectEntries entries) {
		for (Entry entry: entries.getEntries()) {
			for (Term term: entry.getTerms()) {
				if(term.getLanguage()==language){
					super.createEObjectNode(parentNode, term);
				}
			}
		}
	}
}

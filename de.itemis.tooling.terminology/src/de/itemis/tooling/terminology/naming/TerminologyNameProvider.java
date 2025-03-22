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
package de.itemis.tooling.terminology.naming;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.DefaultDeclarativeQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.eclipse.xtext.util.IResourceScopeCache;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;

import de.itemis.tooling.terminology.terminology.Entry;
import de.itemis.tooling.terminology.terminology.SubjectEntries;
import de.itemis.tooling.terminology.terminology.Term;
import de.itemis.tooling.terminology.terminology.TerminologyPackage;

public class TerminologyNameProvider extends
		DefaultDeclarativeQualifiedNameProvider {

	@Inject
	IResourceScopeCache cache;

	private QualifiedName getCategoryName(EObject o) {
		final SubjectEntries cat = (SubjectEntries) EcoreUtil2
				.getRootContainer(o);
		QualifiedName terminologyName = cache.get(cat, o.eResource(),
				new Provider<QualifiedName>() {
					public QualifiedName get() {
						List<INode> refNodes = NodeModelUtils
								.findNodesForFeature(
										cat,
										TerminologyPackage.Literals.SUBJECT_ENTRIES__SUBJECT);
						INode node = refNodes.get(0);
						String withoutHidden = node.getText().substring(
								node.getOffset() - node.getTotalOffset());
						return QualifiedName.create(withoutHidden.split("\\."));
					}
				});
		return terminologyName;
	}

	QualifiedName qualifiedName(final Term term) {
		return getCategoryName(term).append(Optional.fromNullable(term.getName()).or(""));
	}

	QualifiedName qualifiedName(final Entry entry) {
		return getCategoryName(entry).append(Optional.fromNullable(entry.getName()).or(""));
	}
}

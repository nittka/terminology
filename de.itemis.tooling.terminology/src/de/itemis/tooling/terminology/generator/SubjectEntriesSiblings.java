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
package de.itemis.tooling.terminology.generator;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import de.itemis.tooling.terminology.terminology.SubjectEntries;

public class SubjectEntriesSiblings implements Adapter {

	private ResourceSet rs;
	private List<URI> uris;

	public void notifyChanged(Notification notification) {
	}

	public Notifier getTarget() {
		return null;
	}

	public void setTarget(Notifier newTarget) {
	}

	public boolean isAdapterForType(Object type) {
		return false;
	}

	public SubjectEntriesSiblings(ResourceSet rs, List<URI> allURIs) {
		this.rs=rs;
		this.uris=allURIs;
	}

	public List<SubjectEntries> getAllEntries(){
		List<SubjectEntries> result=new ArrayList<SubjectEntries>();
		for (URI uri : uris) {
			rs.getResource(uri, true);
		}
		for (Resource r : rs.getResources()) {
			if(r.getContents().get(0) instanceof SubjectEntries){
				result.add((SubjectEntries)r.getContents().get(0));
			}
		}
		return result;
	}

}

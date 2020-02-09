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
package de.itemis.tooling.terminology.ui.search;

import java.text.Collator;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.xtext.resource.IEObjectDescription;

import com.google.common.collect.Lists;

/**
 * copied and adapted from {@Link org.eclipse.xtext.ui.search.IteratorJob}
 * */
public class TerminologyIteratorJob extends Job {

	private static final int TIME_THRESHOLD = 250;

	private Iterator<IEObjectDescription> iterator;

	private List<IEObjectDescription> matches;

	private final TerminologyEObjectSearchDialog dialog;

	private Collator sorter=Collator.getInstance(Locale.GERMANY);

	public TerminologyIteratorJob(TerminologyEObjectSearchDialog dialog) {
		super("Counting");
		this.dialog = dialog;
		setSystem(true);
	}

	public void init(Iterable<IEObjectDescription> matchResult) {
		this.iterator = matchResult.iterator();
		matches = Lists.newArrayList();
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		long startTime = System.currentTimeMillis();
		while (iterator.hasNext()) {
			IEObjectDescription next = iterator.next();
			if (next.getQualifiedName() != null && next.getEObjectURI() != null && next.getEClass() != null) {
				matches.add(next);
				long endTime = System.currentTimeMillis();
				if (matches.size() == dialog.getHeightInChars() || endTime - startTime > TIME_THRESHOLD) {
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}
					dialog.updateMatches(sortedCopy(matches), false);
					startTime = endTime;
				}
			}
		}
		dialog.updateMatches(sortedCopy(matches), true);
		return Status.OK_STATUS;
	}

	private Collection<IEObjectDescription> sortedCopy(Iterable<IEObjectDescription> list) {
		List<IEObjectDescription> result = Lists.newArrayList(matches);
		Collections.sort(result, new Comparator<IEObjectDescription>() {
			public int compare(IEObjectDescription o1, IEObjectDescription o2) {
				return sorter.compare(o1.getQualifiedName().getLastSegment(), o2.getQualifiedName().getLastSegment());
			}
		});
		return result;
	}
}

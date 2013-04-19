/*******************************************************************************
 * Copyright (c) 2013 itemis AG (http://www.itemis.eu).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package de.itemis.tooling.terminology.ui.search;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;
import org.eclipse.xtext.ui.label.GlobalDescriptionLabelProvider;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * adapted from {@Link org.eclipse.xtext.ui.search.OpenXtextElementHandler}
 */
public class TerminologySearchHandler extends AbstractHandler {

	@Inject
	private IURIEditorOpener uriEditorOpener;
	
	@Inject
	private TerminologyEObjectSearch searchEngine;
	
	@Inject
	private GlobalDescriptionLabelProvider globalDescriptionLabelProvider;

	@Inject
	private IResourceDescriptions index;
	
	@Inject(optional=true)
	@Named("xtext.enable.styledLables")
	private boolean enableStyledLabels = true;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell activeShell = HandlerUtil.getActiveShell(event);
		ListDialog searchDialog = createSearchDialog(event, activeShell, searchEngine, uriEditorOpener);
		searchDialog.open();
		return null;
	}

	protected ListDialog createSearchDialog(ExecutionEvent event, Shell activeShell, TerminologyEObjectSearch searchEngine, IURIEditorOpener uriOpener) {
		return new TerminologyEObjectSearchDialog(activeShell, searchEngine, globalDescriptionLabelProvider, isEnableStyledLabels(), uriOpener, index);
	}

	public void setEnableStyledLabels(boolean enableStyledLabels) {
		this.enableStyledLabels = enableStyledLabels;
	}

	public boolean isEnableStyledLabels() {
		return enableStyledLabels;
	}
}

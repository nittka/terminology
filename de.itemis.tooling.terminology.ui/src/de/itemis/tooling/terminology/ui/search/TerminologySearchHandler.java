package de.itemis.tooling.terminology.ui.search;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;
import org.eclipse.xtext.ui.label.GlobalDescriptionLabelProvider;

import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * adapted from OpenXtextElementHandler
 */
public class TerminologySearchHandler extends AbstractHandler {

	@Inject
	private IURIEditorOpener uriEditorOpener;
	
	@Inject
	private TerminologyEObjectSearch searchEngine;
	
	@Inject
	private GlobalDescriptionLabelProvider globalDescriptionLabelProvider;
	
	@Inject(optional=true)
	@Named("xtext.enable.styledLables")
	private boolean enableStyledLabels = true;
	
	private static final Logger LOG = Logger.getLogger(TerminologySearchHandler.class);

	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell activeShell = HandlerUtil.getActiveShell(event);
		ListDialog searchDialog = createSearchDialog(event, activeShell, searchEngine, uriEditorOpener);
		int result = searchDialog.open();
		if (result == Window.OK) {
			try {
				Object[] selections = searchDialog.getResult();
				if (selections != null && selections.length > 0) {
					Object selection = selections[0];
					if (selection instanceof IEObjectDescription) {
						IEObjectDescription selectedObjectDescription = (IEObjectDescription) selection;
						uriEditorOpener.open(selectedObjectDescription.getEObjectURI(), true);
					}
				}
			} catch (Exception e) {
				LOG.error("Error opening editor", e);
				throw new ExecutionException("Error opening editor", e);
			}
		}
		return null;
	}

	protected ListDialog createSearchDialog(ExecutionEvent event, Shell activeShell, TerminologyEObjectSearch searchEngine, IURIEditorOpener uriOpener) {
		return new TerminologyEObjectSearchDialog(activeShell, searchEngine, globalDescriptionLabelProvider, isEnableStyledLabels(), uriOpener);
	}

	public void setEnableStyledLabels(boolean enableStyledLabels) {
		this.enableStyledLabels = enableStyledLabels;
	}

	public boolean isEnableStyledLabels() {
		return enableStyledLabels;
	}
}

package de.itemis.tooling.terminology.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.xtext.ui.XtextUIMessages;
import org.eclipse.xtext.ui.editor.XtextEditor;

import com.google.common.collect.ObjectArrays;

import de.itemis.tooling.terminology.terminology.TerminologyPackage;
import de.itemis.tooling.terminology.ui.folding.TerminologyFoldingRegionProvider.TypedFoldedRegion;
import de.itemis.tooling.terminology.ui.preferences.TerminologyPreferenceConstants;

public class TerminologyXtextEditor extends XtextEditor{

	@Inject
	private IPreferenceStore preferencStore; 

	//need to override as the FoldingActionGroup is not used for populating the menu
	//(although it has a fillMenu-method)
	@Override
	protected void rulerContextMenuAboutToShow(IMenuManager menu) {
		super.rulerContextMenuAboutToShow(menu);
		//remove the projection menu introduced by super call
		//unfortunately we cannot call super.super.rulerC...
		menu.remove("projection");

		IMenuManager foldingMenu = new MenuManager(XtextUIMessages.Editor_FoldingMenu_name, "projection"); //$NON-NLS-1$
		menu.appendToGroup(ITextEditorActionConstants.GROUP_RULERS, foldingMenu);
		IAction action = getAction("FoldingToggle"); //$NON-NLS-1$
		foldingMenu.add(action);
		action = getAction("FoldingExpandAll"); //$NON-NLS-1$
		foldingMenu.add(action);
		action = getAction("FoldingCollapseAll"); //$NON-NLS-1$
		foldingMenu.add(action);
		action = getAction("FoldingCollapseMetadata"); //$NON-NLS-1$
		foldingMenu.add(action);
		action = getAction("FoldingCollapseTerm"); //$NON-NLS-1$
		foldingMenu.add(action);
		action = getAction("FoldingRestore"); //$NON-NLS-1$
		foldingMenu.add(action);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		ProjectionAnnotationModel model = ((ProjectionViewer) getSourceViewer()).getProjectionAnnotationModel();
		foldRegionsOnStartup(model);
	}

	private void foldRegionsOnStartup(ProjectionAnnotationModel model){
		//TODO retrieve set of types to fold from helper, as other types might be added
		Set<EClass> typesToFold=new HashSet<EClass>();
		if(preferencStore.getBoolean(TerminologyPreferenceConstants.FOLD_METADATA_KEY)){
			typesToFold.add(TerminologyPackage.Literals.META_DATA);
		}
		if(preferencStore.getBoolean(TerminologyPreferenceConstants.FOLD_TERM_KEY)){
			typesToFold.add(TerminologyPackage.Literals.TERM);
		}
		if(preferencStore.getBoolean(TerminologyPreferenceConstants.FOLD_FEEDBACK_KEY)){
			typesToFold.add(TerminologyPackage.Literals.FEEDBACK);
		}
		if(!typesToFold.isEmpty()){
			Iterator<?> iterator = model.getAnnotationIterator();
			while (iterator.hasNext()){
				Object next = iterator.next();
				if(next instanceof ProjectionAnnotation){
					ProjectionAnnotation pa = (ProjectionAnnotation) next;
					Position position = model.getPosition(pa);
					if(position instanceof TypedFoldedRegion &&typesToFold.contains(((TypedFoldedRegion) position).getType())){
						model.collapse(pa);
					}
				}
			}
		}
	}

	@Override
	protected String[] collectContextMenuPreferencePages() {
		String[] superPages = super.collectContextMenuPreferencePages();
		String[] xturtlePages = new String[] {
				getLanguageName() + ".folding",
				getLanguageName() + ".validation",
				getLanguageName() + ".refactoring"
				};
		return ObjectArrays.concat(superPages, xturtlePages, String.class);
	}
}

package de.itemis.tooling.terminology.ui.folding;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.ResourceAction;
import org.eclipse.xtext.ui.editor.folding.FoldingActionGroup;

import de.itemis.tooling.terminology.terminology.TerminologyPackage;
import de.itemis.tooling.terminology.ui.folding.TerminologyFoldingRegionProvider.TypedFoldedRegion;

class TerminologyFoldingActionGroup extends FoldingActionGroup {

	//inner classes copied from super
	private static abstract class PreferenceAction extends ResourceAction implements IUpdate {
		PreferenceAction(ResourceBundle bundle, String prefix, int style) {
			super(bundle, prefix, style);
		}
	}
	private class FoldingAction extends PreferenceAction {

		FoldingAction(ResourceBundle bundle, String prefix) {
			super(bundle, prefix, IAction.AS_PUSH_BUTTON);
		}
		public void update() {
			setEnabled(TerminologyFoldingActionGroup.this.isEnabled() && viewwer.isProjectionMode());
		}
	}

	private ProjectionViewer viewwer;
	private FoldingAction collapseMetadata;
	private FoldingAction collapseTerm;

	TerminologyFoldingActionGroup(final ITextEditor editor, ITextViewer viewer) {
		super(editor, viewer);
		if(!(viewer instanceof ProjectionViewer)){
			return;
		}
		this.viewwer=(ProjectionViewer) viewer;
		
		collapseMetadata= getFoldingAction("Projection.CollapseMetadata.", TerminologyPackage.Literals.META_DATA);
		collapseMetadata.setActionDefinitionId("de.itemis.tooling.terminology.ui.folding.collapseMetadata");
		editor.setAction("FoldingCollapseMetadata", collapseMetadata); //$NON-NLS-1$

		collapseTerm=getFoldingAction("Projection.CollapseTerm.", TerminologyPackage.Literals.TERM);
		collapseTerm.setActionDefinitionId("de.itemis.tooling.terminology.ui.folding.collapseTerm");
		editor.setAction("FoldingCollapseTerm", collapseTerm); //$NON-NLS-1$
	}

	private FoldingAction getFoldingAction(String bundlePrefix, final EClass type){
		return new FoldingAction(TerminologyFoldingMessages.getResourceBundle(), bundlePrefix) {
			public void run() {
				ProjectionAnnotationModel model = viewwer.getProjectionAnnotationModel();
				Iterator<?> iterator = model.getAnnotationIterator();
				List<Annotation> toCollapse=new ArrayList<Annotation>();
				while (iterator.hasNext()){
					Object next = iterator.next();
					if(next instanceof ProjectionAnnotation){
						ProjectionAnnotation pa = (ProjectionAnnotation) next;
						//foldable regions for strings have been marked by using the TextFoldedRegion class
						//there may indeed be better ways...
						Position position = model.getPosition(pa);
						if(position instanceof TypedFoldedRegion && type==((TypedFoldedRegion) position).getType()){
							if (!pa.isCollapsed()){
								toCollapse.add(pa);
							}
						}
					}
				}
				for (Annotation annotation : toCollapse) {
					model.collapse(annotation);
				}
			}
		};
	}
	@Override
	protected void update() {
		super.update();
		if(isEnabled()){
			collapseMetadata.update();
		}
	}
}

package de.itemis.tooling.terminology.ui.templates;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.xtext.ui.editor.templates.AbstractTemplateVariableResolver;
import org.eclipse.xtext.ui.editor.templates.XtextTemplateContext;
import org.eclipse.xtext.ui.editor.templates.XtextTemplateContextType;

import com.google.common.collect.ImmutableList;

public class TerminologyTemplateContextType extends XtextTemplateContextType {

	private class TerminologyDateVariableResolver extends AbstractTemplateVariableResolver{

		private SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		public TerminologyDateVariableResolver() {
			super("today","inserts today's date in correct format");
		}
		@Override
		public List<String> resolveValues(TemplateVariable variable,
				XtextTemplateContext xtextTemplateContext) {
			return ImmutableList.of(format.format(new Date()));
		}
		@Override
		protected boolean isUnambiguous(TemplateContext context) {
			return true;
		}
	}
	@Override
	protected void addDefaultTemplateVariables() {
		super.addDefaultTemplateVariables();
		addResolver(new TerminologyDateVariableResolver());
	}
}

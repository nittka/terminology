package de.itemis.tooling.terminology.ui.search;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.dialogs.SearchPattern;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;

import de.itemis.tooling.terminology.terminology.TermStatus;
import de.itemis.tooling.terminology.terminology.TerminologyPackage;

/**
 * adapted from IXtextEObjectSearch 
 */
class TerminologyEObjectSearch {
	
	static class TerminologySearchPattern{
		private String textPattern;
		private boolean inDefinition;
		private boolean inUsage;
		private Set<String> status=new HashSet<String>();
		public void setTextPattern(String textPattern) {
			this.textPattern = textPattern;
		}
		public void setInDefinition(boolean inDefinition) {
			this.inDefinition = inDefinition;
		}
		public void setInUsage(boolean inUsage) {
			this.inUsage = inUsage;
		}
		public void setStatus(Map<TermStatus, Button> statusControls){
			status.clear();
			for (TermStatus bstatus : statusControls.keySet()) {
				if(statusControls.get(bstatus).getSelection()){
					status.add(bstatus.toString());
				}
			}
		}
	}

	@Inject
	private IResourceDescriptions resourceDescriptions;
	
	@Inject
	private IQualifiedNameConverter qualifiedNameConverter;

	Iterable<IEObjectDescription> findMatches(TerminologySearchPattern pattern){
		return Iterables.filter(getSearchScope(), getSearchPredicate(pattern));
	}


		protected Predicate<IEObjectDescription> getSearchPredicate(final TerminologySearchPattern pattern) {
			final SearchPattern searchPattern = new SearchPattern();
			searchPattern.setPattern(pattern.textPattern);

			return new Predicate<IEObjectDescription>() {
				public boolean apply(IEObjectDescription input) {
					if(input.getEClass()==TerminologyPackage.Literals.TERM){
						if(!pattern.status.contains(input.getUserData("status"))){
							return false;
						}
						if (isNameMatches(searchPattern, input)) {
							return true;
						}
						if(pattern.inDefinition){
							String def=input.getUserData("def");
							if(def!=null && searchPattern.matches(def)){
								return true;
							}
						}
						if(pattern.inUsage){
							String sem=input.getUserData("sem");
							if(sem!=null && searchPattern.matches(sem)){
								return true;
							}
						}
					}
					return false;
				}
			};
		}

		protected boolean isNameMatches(SearchPattern searchPattern, IEObjectDescription eObjectDescription) {
			String simpleName = eObjectDescription.getQualifiedName().getLastSegment();
			if (simpleName!=null) {
				if(searchPattern.matches(simpleName)) {
					return true;
				}
			}
			return false;
		}
		
		protected Iterable<IEObjectDescription> getSearchScope() {
			return Iterables.concat(Iterables.transform(getResourceDescriptions().getAllResourceDescriptions(),
					new Function<IResourceDescription, Iterable<IEObjectDescription>>() {
						public Iterable<IEObjectDescription> apply(IResourceDescription from) {
							return from.getExportedObjects();
						}
					}));
		}

		public void setResourceDescriptions(IResourceDescriptions resourceDescriptions) {
			this.resourceDescriptions = resourceDescriptions;
		}

		public IResourceDescriptions getResourceDescriptions() {
			return resourceDescriptions;
		}
}
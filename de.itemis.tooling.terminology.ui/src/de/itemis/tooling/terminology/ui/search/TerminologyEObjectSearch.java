/*******************************************************************************
 * Copyright (c) 2013 itemis AG (http://www.itemis.eu).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package de.itemis.tooling.terminology.ui.search;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.swt.widgets.Button;
import org.eclipse.ui.dialogs.SearchPattern;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IReferenceDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import de.itemis.tooling.terminology.terminology.TermStatus;
import de.itemis.tooling.terminology.terminology.TerminologyPackage;

/**
 * adapted from {@Link org.eclipse.xtext.ui.search.IXtextEObjectSearch} 
 */
class TerminologyEObjectSearch {
	
	static class TerminologySearchPattern{
		private String textPattern;
		private boolean inDefinition;
		private boolean inUsage;
		private Set<String> status=new HashSet<String>();
		private Set<URI> products=Sets.newHashSet();
		private Set<URI> customers=Sets.newHashSet();
		private boolean useCustomerProducts;

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
		public void setUseCustomerProducts(boolean useCustomerProducts) {
			this.useCustomerProducts = useCustomerProducts;
		}
		public void setProducts(IEObjectDescription[] checked) {
			initFromArray(products, checked);
		}
		public void setCustomers(IEObjectDescription[] checked){
			initFromArray(customers, checked);
		}

		private void initFromArray(Set<URI> set, IEObjectDescription[] checked){
			set.clear();
			for (IEObjectDescription desc : checked) {
				set.add(desc.getEObjectURI());
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
			final Set<URI> productRefs;
			final Set<URI> customerRefs;
			if(pattern.useCustomerProducts && !pattern.products.isEmpty()){
				productRefs=getReferences(pattern.products, TerminologyPackage.Literals.TERM__PRODUCTS);
			}else{
				productRefs=null;
			}
			if(pattern.useCustomerProducts && !pattern.customers.isEmpty()){
				customerRefs=getReferences(pattern.customers, TerminologyPackage.Literals.TERM__CUSTOMERS);
			}else{
				customerRefs=null;
			}

			return new Predicate<IEObjectDescription>() {
				public boolean apply(IEObjectDescription input) {
					boolean isMatch=false;
					if(input.getEClass()==TerminologyPackage.Literals.TERM){
						if(pattern.status.contains(input.getUserData("status"))){
							isMatch|=isNameMatches(searchPattern, input);
							if(pattern.inDefinition && !isMatch){
								String def=input.getUserData("def");
								isMatch|=(def!=null && searchPattern.matches(def));
							}
							if(pattern.inUsage &&!isMatch){
								String sem=input.getUserData("sem");
								isMatch|=(sem!=null && searchPattern.matches(sem));
							}
							if(isMatch && productRefs!=null){
								isMatch&=productRefs.contains(input.getEObjectURI());
							}
							if(isMatch && customerRefs!=null){
								isMatch&=customerRefs.contains(input.getEObjectURI());
							}
						}
					}
					return isMatch;
				}
			};
		}

		protected Set<URI> getReferences(final Set<URI> selected, final EReference ref){
			Set<URI> result=Sets.newHashSet();
			Iterator<IResourceDescription> it1 = getResourceDescriptions().getAllResourceDescriptions().iterator();
			while(it1.hasNext()){
				IResourceDescription res = it1.next();
				Iterable<IReferenceDescription> descs = Iterables.filter(res.getReferenceDescriptions(), new Predicate<IReferenceDescription>() {
					public boolean apply(IReferenceDescription desc){
						if(desc.getEReference()==ref){
							return selected.contains(desc.getTargetEObjectUri());
						}
						return false;
					}
				});
				for (IReferenceDescription desc : descs) {
					result.add(desc.getSourceEObjectUri());
				}
			}
			return result;
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

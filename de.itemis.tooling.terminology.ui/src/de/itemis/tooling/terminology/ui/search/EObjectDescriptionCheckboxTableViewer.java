package de.itemis.tooling.terminology.ui.search;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.ui.label.AbstractLabelProvider;

public class EObjectDescriptionCheckboxTableViewer extends CheckboxTableViewer {

	public EObjectDescriptionCheckboxTableViewer(Table table) {
		super(table);
		setLabelProvider(new AbstractLabelProvider() {
			@Override
			protected Object doGetText(Object element) {
				if(element instanceof IEObjectDescription){
					return ((IEObjectDescription)element).getQualifiedName().toString();
				}
				return super.doGetText(element);
			}
		});
	}

	@Override
	//make every selection event a check event
	public void handleSelect(SelectionEvent event) {
		if(event.detail!=SWT.CHECK){
			if(event.item instanceof TableItem){
				TableItem ti=(TableItem)event.item;
				ti.setChecked(!ti.getChecked());
				event.detail=SWT.CHECK;
			}
		}
		super.handleSelect(event);
	}

	public static EObjectDescriptionCheckboxTableViewer newCheckList(
			Composite parent, int style) {
		Table table = new Table(parent, SWT.CHECK | style);
		return new EObjectDescriptionCheckboxTableViewer(table);
	}

	void setDescriptions(Iterable<IEObjectDescription> possibleValues){
		for (IEObjectDescription ieObjectDescription : possibleValues) {
			add(ieObjectDescription);
		}
	}

	public void setCheckedNames(String[] selected) {
		List<String> list=Arrays.asList(selected);
		TableItem[] items = getTable().getItems();
		for (TableItem tableItem : items) {
			if(tableItem.getData() instanceof IEObjectDescription){
				IEObjectDescription desc = (IEObjectDescription)tableItem.getData();
				if(list.contains(desc.getQualifiedName().toString())){
					setChecked(tableItem.getData(), true);
				}
			}
		}
	}

	public IEObjectDescription[] getChecked(){
		Object[] checkedElements = getCheckedElements();
		IEObjectDescription[] result=new IEObjectDescription[checkedElements.length];
		for (int i = 0; i < result.length; i++) {
			result[i]=((IEObjectDescription)checkedElements[i]);
		}
		return result;
	}

	public String[] getCheckedNames(){
		IEObjectDescription[] checked = getChecked();
		String[] selected=new String[checked.length];
		for (int i = 0; i < checked.length; i++) {
			selected[i]=checked[i].getQualifiedName().toString();
		}
		return selected;
	}
}

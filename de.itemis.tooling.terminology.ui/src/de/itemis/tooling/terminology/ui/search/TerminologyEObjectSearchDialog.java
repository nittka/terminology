package de.itemis.tooling.terminology.ui.search;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.internal.text.TableOwnerDrawSupport;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.ui.editor.IURIEditorOpener;
import org.eclipse.xtext.ui.label.AbstractLabelProvider;
import org.eclipse.xtext.ui.search.EObjectDescriptionContentProvider;

import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;

import de.itemis.tooling.terminology.terminology.TermStatus;
import de.itemis.tooling.terminology.terminology.TerminologyPackage;
import de.itemis.tooling.terminology.ui.internal.TerminologyActivator;
import de.itemis.tooling.terminology.ui.search.TerminologyEObjectSearch.TerminologySearchPattern;

/**
 * adapted from XtextEObjectSearchDialog
 */
public class TerminologyEObjectSearchDialog extends ListDialog {

	private static final String SETTINGS_SECTION = "Terminology_Search";

	protected Text searchControl;

	private String initialPatternText;

	private Label messageLabel;

	private Label searchStatusLabel;

	private IteratorJob sizeCalculationJob;

	private Label matchingElementsLabel;

	private TerminologyEObjectSearch searchEngine;

	private final ILabelProvider labelProvider;

	private boolean enableStyledLabels;

	private IURIEditorOpener uriOpener;

	private Button definitionControl;

	private Button usageControl;
	private Map<TermStatus, Button> statusControls;
	private Map<IEObjectDescription, Button> productControls;
	private Map<IEObjectDescription, Button> customerControls;

	private Iterable<IEObjectDescription> possibleProducs;
	private Iterable<IEObjectDescription> possibleCustomers;


	public TerminologyEObjectSearchDialog(Shell parent, TerminologyEObjectSearch searchEngine, ILabelProvider labelProvider, IURIEditorOpener uriOpener, IResourceDescriptions index) {
		super(parent);
		this.searchEngine = searchEngine;
		this.labelProvider = new AbstractLabelProvider(labelProvider) {
			@Override
			protected Object doGetText(Object element) {
				if(element instanceof IEObjectDescription){
					return ((IEObjectDescription)element).getQualifiedName().getLastSegment();
				}
				return super.doGetText(element);
			}
		};
		this.uriOpener = uriOpener;
		this.possibleProducs=index.getExportedObjectsByType(TerminologyPackage.Literals.PRODUCT);
		this.possibleCustomers=index.getExportedObjectsByType(TerminologyPackage.Literals.CUSTOMER);
		setTitle("Terminology Search");
		setMessage("Search for terms");
		setAddCancelButton(true);
		// super class needs an IStructuredContentProvider so we register this dummy and 
		// register the lazy one later
		setContentProvider(new IStructuredContentProvider() {
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}

			public void dispose() {
			}

			public Object[] getElements(Object inputElement) {
				return null;
			}
		});
		setLabelProvider(this.labelProvider);
		setHelpAvailable(false);
		setDialogBoundsSettings(getDialogSettings(), DIALOG_PERSISTLOCATION|DIALOG_PERSISTSIZE);
	}
	
	public TerminologyEObjectSearchDialog(Shell parent, TerminologyEObjectSearch searchEngine, ILabelProvider labelProvider, boolean enableStyledLabels, IURIEditorOpener uriOpener, IResourceDescriptions index) {
		this(parent, searchEngine, labelProvider,uriOpener, index);
		setShellStyle(SWT.DIALOG_TRIM | SWT.MAX | SWT.RESIZE
				| getDefaultOrientation());
		this.enableStyledLabels = enableStyledLabels;
	}

	/**
	 * @since 2.0
	 */
	protected TerminologyEObjectSearch getSearchEngine() {
		return searchEngine;
	}

	/**
	 * @since 2.0
	 */
	public void setInitialPattern(String text) {
		this.initialPatternText = text;
	}

	/**
	 * @since 2.0
	 */
	protected String getInitialPattern() {
		return initialPatternText;
	}

	@Override
	public int open() {
		if (getInitialPattern() == null) {
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (window != null) {
				ISelection selection = window.getSelectionService().getSelection();
				if (selection instanceof ITextSelection) {
					String text = ((ITextSelection) selection).getText();
					if (text != null) {
						text = text.trim();
						if (text.length() > 0) {
							setInitialPattern(text);
						}
					}
				}
			}
		}
		return super.open();
	}

	@Override
	protected int getTableStyle() {
		return super.getTableStyle() | SWT.VIRTUAL;
	}

	@Override
	protected Control createDialogArea(Composite container) {
		Composite parent = (Composite) super.createDialogArea(container);
		if (enableStyledLabels && labelProvider instanceof IStyledLabelProvider) {
			final Table table = getTableViewer().getTable();
			final IStyledLabelProvider styledLabelProvider = (IStyledLabelProvider) labelProvider;
			TableOwnerDrawSupport.install(table);
			Listener listener= new Listener() {
				public void handleEvent(Event event) {
					handleSetData(event);
				}
				protected void handleSetData(Event event) {
					TableItem item= (TableItem) event.item;
					IEObjectDescription description = (IEObjectDescription) item.getData();
					if (description != null) {
						StyledString styledString = styledLabelProvider.getStyledText(description);
						String displayString = styledString.toString();
						StyleRange[] styleRanges = styledString.getStyleRanges();
						item.setText(displayString);
						TableOwnerDrawSupport.storeStyleRanges(item, 0, styleRanges);
					}
				}
			};
			table.addListener(SWT.SetData, listener);
		}
		messageLabel = new Label(parent, SWT.NONE);
		setDefaultGridData(messageLabel);
		EObjectDescriptionContentProvider contentProvider = new EObjectDescriptionContentProvider();
		getTableViewer().setContentProvider(contentProvider);
		getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					if (!structuredSelection.isEmpty()) {
						Object firstElement = structuredSelection.getFirstElement();
						if (firstElement instanceof IEObjectDescription) {
							IEObjectDescription eObjectDescription = (IEObjectDescription) firstElement;
							URI resourceURI = eObjectDescription.getEObjectURI().trimFragment();
							if (resourceURI.isPlatform()) {
								messageLabel.setText(resourceURI.toPlatformString(true));
							} else if (resourceURI.isFile()) {
								messageLabel.setText(resourceURI.toFileString());
							} else {
								messageLabel.setText(resourceURI.toString());
							}
							return;
						}
					}
				}
				messageLabel.setText(""); //$NON-NLS-1$
			}
		});
		getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				try {
						ISelection selection =event.getSelection();
						if (selection instanceof StructuredSelection) {
							Object o=((StructuredSelection) selection).getFirstElement();
							if(o instanceof IEObjectDescription){
								IEObjectDescription selectedObjectDescription = (IEObjectDescription) o;
								uriOpener.open(selectedObjectDescription.getEObjectURI(), true);
							}
						}
				} catch (Exception e) {
//					LOG.error("Error opening editor", e);
//					throw new ExecutionException("Error opening editor", e);
				}

			}
		});

		applyFilter();
		
		return parent;
	}

	@Override
	protected void okPressed() {
		try {
			//disable tableViewer's doubleClickListener, so that the dialog stays open
			StackTraceElement traceElement = Thread.currentThread().getStackTrace()[2];
			if(traceElement.getMethodName().contains("Click")){
				return;
			}
		} catch (Exception e) {
		}
		storeSettings();
		super.okPressed();
	}

	private void storeSettings() {
		IDialogSettings settings = getDialogSettings();
		settings.put("definition", definitionControl.getSelection());
		settings.put("usage", usageControl.getSelection());
		for (TermStatus status : TermStatus.values()) {
			settings.put(status.toString(), statusControls.get(status).getSelection());
		}
		settings.put("products", getSelectedEObjects(productControls));
		settings.put("customers", getSelectedEObjects(customerControls));
	}

	private String[] getSelectedEObjects(Map<IEObjectDescription, Button> buttons){
		String[] selected=new String[0];
		for (Entry<IEObjectDescription, Button> entry : buttons.entrySet()) {
			if(entry.getValue().getSelection()){
				selected=ObjectArrays.concat(selected, entry.getKey().getQualifiedName().toString());
			}
		}
		return selected;
	}

	@Override
	protected Label createMessageArea(Composite parent2) {
		Label label = super.createMessageArea(parent2);
		searchControl = new Text(parent2, SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL);
		setDefaultGridData(searchControl);

		SelectionAdapter selectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				applyFilter();
			}
		};

		initSearchScopeSettings(parent2, selectionListener);
		initCustomersProductsSettings(parent2, selectionListener);

		Composite labelComposite = new Composite(parent2, SWT.NONE);
		setDefaultGridData(labelComposite);
		GridLayout labelCompositeLayout = new GridLayout(2, true);
		labelCompositeLayout.marginWidth = 0;
		labelComposite.setLayout(labelCompositeLayout);
		matchingElementsLabel = new Label(labelComposite, SWT.NONE);
		matchingElementsLabel.setText("Searching...");
		matchingElementsLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		searchStatusLabel = new Label(labelComposite, SWT.RIGHT);
		searchStatusLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		ModifyListener textModifyListener = new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				applyFilter();
			}
		};
		searchControl.addModifyListener(textModifyListener);

		searchControl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ARROW_DOWN) {
					definitionControl.setFocus();
				}
			}
		});

		definitionControl.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ARROW_DOWN) {
					TableViewer tableViewer = getTableViewer();
					tableViewer.getTable().setFocus();
					if (tableViewer.getSelection().isEmpty()) {
						Object firstElement = tableViewer.getElementAt(0);
						if (firstElement != null) {
							tableViewer.setSelection(new StructuredSelection(firstElement));
						}
					}
				}
			}
		});

		if (initialPatternText != null) {
			searchControl.setText(initialPatternText);
			searchControl.selectAll();
		}
		readSettings();
		return label;
	}

	//TODO checkbox table viewer, 2 columns
	private void initCustomersProductsSettings(Composite parent, SelectionAdapter selectionListener){
		ExpandableComposite exComposite = createStyleSection(parent, "products and customers", 3);
		Composite composite=new Composite(exComposite, SWT.NONE);
		exComposite.setClient(composite);
		composite.setLayout(parent.getLayout());

		//start products
		Label productRef=new Label(composite, SWT.NONE);
		productRef.setText("Terms for products");

		Composite productComposite = new Composite(composite, SWT.NONE);
		setDefaultGridData(productComposite);
		GridLayout productCompositeLayout = new GridLayout(3, true);
		productCompositeLayout.marginWidth = 0;
		productComposite.setLayout(productCompositeLayout);

		productControls=new LinkedHashMap<IEObjectDescription, Button>();
		for (IEObjectDescription product : possibleProducs) {
			Button productButton=new Button(productComposite, SWT.CHECK);
			productButton.setText(product.getQualifiedName().toString());
			productButton.addSelectionListener(selectionListener);
			productControls.put(product, productButton);
		}

//		ListViewer productViewer=new ListViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
//		for (IEObjectDescription product : possibleProducs) {
//			productViewer.getList().add(product.getQualifiedName().toString());
//		}
		//end products

		//start customers
		Label customerRef=new Label(composite, SWT.NONE);
		customerRef.setText("Terms for customers");

		Composite customerComposite = new Composite(composite, SWT.NONE);
		setDefaultGridData(customerComposite);
		GridLayout customerCompositeLayout = new GridLayout(3, true);
		customerCompositeLayout.marginWidth = 0;
		customerComposite.setLayout(customerCompositeLayout);

		customerControls=new LinkedHashMap<IEObjectDescription, Button>();
		for (IEObjectDescription customer : possibleCustomers) {
			Button customerButton=new Button(customerComposite, SWT.CHECK);
			customerButton.setText(customer.getQualifiedName().toString());
			customerButton.addSelectionListener(selectionListener);
			customerControls.put(customer, customerButton);
		}
		//end customers

	}

	private void initSearchScopeSettings(Composite parent, SelectionAdapter selectionListener){
		ExpandableComposite exComposite = createStyleSection(parent, "search parameters", 3);
		Composite composite=new Composite(exComposite, SWT.NONE);
		exComposite.setClient(composite);
		composite.setLayout(parent.getLayout());

		Label searchIn=new Label(composite, SWT.NONE);
		searchIn.setText("Also search in:");

		Composite checkboxComposite = new Composite(composite, SWT.NONE);
		setDefaultGridData(checkboxComposite);
		GridLayout checkboxCompositeLayout = new GridLayout(2, true);
		checkboxCompositeLayout.marginWidth = 0;
		checkboxComposite.setLayout(checkboxCompositeLayout);

		definitionControl=new Button(checkboxComposite, SWT.CHECK);
		definitionControl.setText("Definition");
		definitionControl.addSelectionListener(selectionListener);
		usageControl=new Button(checkboxComposite, SWT.CHECK);
		usageControl.setText("Usage");
		usageControl.addSelectionListener(selectionListener);

		Label searchStatus=new Label(composite, SWT.NONE);
		searchStatus.setText("Terms with status:");

		Composite checkboxComposite2 = new Composite(composite, SWT.NONE);
		setDefaultGridData(checkboxComposite2);
		GridLayout checkboxComposite2Layout = new GridLayout(5, true);
		checkboxComposite2Layout.marginWidth = 0;
		checkboxComposite2.setLayout(checkboxComposite2Layout);

		statusControls=new LinkedHashMap<TermStatus, Button>();
		for (TermStatus status: TermStatus.values()) {
			Button statusButton=new Button(checkboxComposite2, SWT.CHECK);
			statusButton.setText(status.toString());
			statusButton.addSelectionListener(selectionListener);
			statusControls.put(status, statusButton);
		}
	}

	protected ExpandableComposite createStyleSection(Composite parent, String label, int nColumns) {
		ExpandableComposite excomposite = new ExpandableComposite(parent, ExpandableComposite.TWISTIE
				| ExpandableComposite.CLIENT_INDENT);
		excomposite.setText(label);
		excomposite.setExpanded(false);
		excomposite.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		excomposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, nColumns, 1));
		excomposite.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				getShell().layout(true, true);
			}
		});
		return excomposite;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getOkButton().setText("Save Settings and Close");
		setButtonLayoutData(getOkButton());
		getButton(IDialogConstants.CANCEL_ID).setText("Close");
	}

	private void readSettings() {
		definitionControl.setSelection(getDialogSettings().getBoolean("definition"));
		usageControl.setSelection(getDialogSettings().getBoolean("usage"));
		for (TermStatus status : TermStatus.values()) {
			statusControls.get(status).setSelection(getDialogSettings().getBoolean(status.toString()));
		}
		readEObjectSettings("products", productControls);
		readEObjectSettings("customers", customerControls);
	}

	private void readEObjectSettings(String key, Map<IEObjectDescription, Button> buttons){
		if(getDialogSettings().getArray(key)!=null){
			List<String> selected = Lists.newArrayList(getDialogSettings().getArray(key));
			for (Entry<IEObjectDescription, Button> button :buttons.entrySet()) {
				if(selected.contains(button.getKey().getQualifiedName().toString())){
					button.getValue().setSelection(true);
				}
			}
		}
		
	}

	private IDialogSettings getDialogSettings(){
		IDialogSettings settings = TerminologyActivator.getInstance().getDialogSettings();
		if(settings.getSection(SETTINGS_SECTION)==null){
			initDefaultSettings(settings);
		}
		return settings.getSection(SETTINGS_SECTION);
	}

	private void initDefaultSettings(IDialogSettings settings) {
		IDialogSettings newSetting = settings.addNewSection(SETTINGS_SECTION);
		for (TermStatus status : TermStatus.values()) {
			newSetting.put(status.toString(), true);
		}
	}

	private void setDefaultGridData(Control control) {
		control.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
	}

	/**
	 * Called when the dialog is initially opened and whenever the input text changes. Applies the search filter as 
	 * specified by {@link #searchControl} and {@link #typeSearchControl} using {@link #getSearchEngine()} and updates
	 * the result using {@link #startSizeCalculation(Iterable)}.
	 * 
	 * @since 2.0
	 */
	protected void applyFilter() {
		TerminologySearchPattern pattern=new TerminologySearchPattern();
		pattern.setTextPattern(searchControl.getText());
		pattern.setInDefinition(definitionControl.getSelection());
		pattern.setInUsage(usageControl.getSelection());
		pattern.setStatus(statusControls);
		pattern.setProducts(productControls);
		pattern.setCustomers(customerControls);

		//		if (pattern.textPattern != null) {
			Iterable<IEObjectDescription> matches = getSearchEngine().findMatches(pattern);
			startSizeCalculation(matches);
//		}
	}

	public void updateMatches(final Collection<IEObjectDescription> matches, final boolean isFinished) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (getShell() != null) {
					if (getTableViewer() != null) {
						getTableViewer().setItemCount(matches.size());
						getTableViewer().setInput(matches);
						if (getTableViewer().getSelection().isEmpty() && matches.size() > 0) 
							getTableViewer().getTable().select(0);
					}
					searchStatusLabel
							.setText((isFinished) ? "" : "Search"); //$NON-NLS-1$
					matchingElementsLabel.setText(matches.size() + " hits"); //$NON-NLS-1$
				}
			}
		});
	}

	/**
	 * Called by {@link #applyFilter()} and is responsible for calling {@link #updateMatches(Collection, boolean)} with
	 * an appropriately sorted list of matches.
	 * 
	 * @since 2.0
	 */
	protected void startSizeCalculation(Iterable<IEObjectDescription> matches) {
		if (getTableViewer() != null) {
			if (sizeCalculationJob != null) {
				sizeCalculationJob.cancel();
				try {
					sizeCalculationJob.join();
				} catch (InterruptedException e) {
					sizeCalculationJob = new IteratorJob(this);
				}
			} else {
				sizeCalculationJob = new IteratorJob(this);
			}
			sizeCalculationJob.init(matches);
			sizeCalculationJob.schedule();
		}
	}

}

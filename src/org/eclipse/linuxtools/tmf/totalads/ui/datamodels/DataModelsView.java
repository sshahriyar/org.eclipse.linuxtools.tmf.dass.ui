/**
 * 
 */
package org.eclipse.linuxtools.tmf.totalads.ui.datamodels;

import java.util.HashSet;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.ui.views.TmfView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.jface.viewers.StructuredSelection;  

/**
 * This class creates a view to display the models
 * @author <p> efraimlopez </p>
 * 			<p> Syed Shariyar Murtaza justsshary@hotmail.com</p>
 *
 */
public class DataModelsView extends  ViewPart implements ISelectionProvider{
	
	public static final String ID = "org.eclipse.linuxtools.tmf.totalads.ModelsView";
	private ModelsList modelListViewer = null;
	private ListenerList listeners ; 
	private HashSet<String> selection; 
	/**
	 * 
	 * Inner class representing a Table for ModelsList
	 *
	 */
	private  class ModelsList{
		CheckboxTableViewer viewer = null;
		Table table=null;
		
		public ModelsList(Composite parent){
			
			table =new Table(parent, SWT.MULTI | SWT.H_SCROLL|SWT.CHECK
				      | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			viewer = new CheckboxTableViewer(table);
			
					
			TableViewerColumn modelName = new TableViewerColumn(viewer, SWT.NONE);
			modelName.getColumn().setWidth(100);
			modelName.getColumn().setText("Model Name");
			modelName.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					DataModel model = (DataModel) element;
					return model.getId();
				}
				@Override
				public Image getImage(Object element) {
				  return null;
				}
			});
						
						
			viewer.setContentProvider(new DataModelTableContentProvider());			
			viewer.setInput(TotalAdsState.INSTANCE.getModels());
			
			// Event handler for check ticks in the Table
			viewer.addCheckStateListener(new ICheckStateListener() {
				
				@Override
				public void checkStateChanged(CheckStateChangedEvent event) {
					
					MessageBox msgBox=new MessageBox(Display.getCurrent().getActiveShell(), SWT.ERROR);
					DataModel model=(DataModel)event.getElement();
					String modelName=model.getId();
					String []modelAcronym=modelName.split("_");
					
					if (event.getChecked()){
						
						if (selection.size()>=5){
							msgBox.setMessage("Please select less than six models only");
						    msgBox.open();
						    uncheckedSelectedModel(modelName, viewer);
						    return;
						   //Making sure that it is not a database that already exist in the db 
						} else if(modelAcronym==null ||  modelAcronym.length<2){
							msgBox.setMessage(modelName+ " is not a valid model created by TotalADS!");
							msgBox.open();
							uncheckedSelectedModel(modelName, viewer);
							return;
							
						}else if (AlgorithmFactory.getInstance().getAlgorithmByAcronym(modelAcronym[1])==null){
								msgBox.setMessage(modelName +" is not a valid model created by TotalADS!");
								msgBox.open();
								uncheckedSelectedModel(modelName, viewer);
								return;
						}
						
						selection.add(modelName);
						
					}else if (!event.getChecked()){
						
						selection.remove(modelName);
					} // end of event checking
					// now calling listeners
					
					setSelection(new StructuredSelection(selection.clone()));
				}
			});
			
		}
	}

	/**
	 * Constructor
	 */
	public DataModelsView() {
		listeners = new ListenerList(); 
		selection=new HashSet<String>(); 
	}
	
	/**
	 * Unchecked selected model in the table view
	 * @param modelName
	 * @param viewer
	 */
	private void uncheckedSelectedModel(String modelName, CheckboxTableViewer viewer ){
		for (int i=0;i<viewer.getTable().getItemCount(); i++)
	    	if (viewer.getTable().getItem(i).getText().equals(modelName)){
	    		     viewer.getTable().getItem(i).setChecked(false);
	    		     break;
	    	}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		modelListViewer = new ModelsList(parent);
		// Registering viewer as a provider with Eclipse to monitor changes 
		getSite().setSelectionProvider(this);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO

	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.add(listener);  
		
	}

	@Override
	public ISelection getSelection() {
		  return new StructuredSelection(selection);  
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		listeners.remove(listener);  
		
	}

	@Override
	public void setSelection(ISelection selection) {
		 Object[] list = listeners.getListeners();  
		  for (int i = 0; i < list.length; i++) {  
		      ((ISelectionChangedListener) list[i])  
		            .selectionChanged(new SelectionChangedEvent(this, selection));  
		  }  
		
	}

}

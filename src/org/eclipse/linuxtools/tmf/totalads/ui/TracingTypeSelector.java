package org.eclipse.linuxtools.tmf.totalads.ui;

import java.util.ArrayList;

import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class TracingTypeSelector {
	Combo cmbTraceTypes;
	TraceTypeFactory  traceFac=TraceTypeFactory.getInstance();
	//ArrayList<IObserver> observers=new ArrayList<IObserver>();
	/**
	 * Creates a combo box and populates it with all the trace types registered with the TraceTypeFactory
	 * @param parent
	 */
	public TracingTypeSelector(Composite parent){
		/*
		 * Trace Type Selection
		 */
		

		
		cmbTraceTypes= new Combo(parent,SWT.READ_ONLY);
		
		cmbTraceTypes.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false,1,1));
	
		populateCombo(cmbTraceTypes);
		
		/*cmbTraceTypes.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String key= cmbTraceTypes.getItem(cmbTraceTypes.getSelectionIndex());
				String traceAcronym= traceFac.getTraceReader(key).getAcronym();
			    notifyObservers(traceAcronym);
				
			}
			
		});*/
		
	}
	/**
	 * 
	 * @param combTraceTypes
	 */
	private void populateCombo(Combo combTraceTypes){
		
	    // populating anomaly detection models		
		String []traceReaders  = traceFac.getAllTraceTypeReaderKeys();
		
		if (traceReaders!=null)
			for (int i=0;i <traceReaders.length;i++)
				combTraceTypes.add(traceReaders[i]);
		   
		combTraceTypes.select(0);
	}
	/**
	 * Returns the ITraceTypeReader for the selected trace type
	 * @return
	 */
	public ITraceTypeReader getSelectedType(){
		
		
		String key= cmbTraceTypes.getItem(cmbTraceTypes.getSelectionIndex());
		return traceFac.getTraceReader(key);
	}

	/**
	 * Selects a trace type reader in the combo box
	 * @param traceTypeName
	 */
	public void selectTraceType(String traceTypeName){
		for (int i=0;i<cmbTraceTypes.getItemCount();i++)
			if (cmbTraceTypes.getItem(i).equalsIgnoreCase(traceTypeName)){
				cmbTraceTypes.select(i);
				break;
			}
		}
	
  //Observer methods implemented without any Subject/Observee interface
	/*public void addObserver(IObserver observer){
		observers.add(observer);
		
	}
	public void removeObserver(IObserver observer){
		observers.remove(observer);
		
	}
	
	public void notifyObservers(String traceAcronym){
		for (IObserver ob: observers)
			ob.update(traceAcronym);
	}
	*/
}

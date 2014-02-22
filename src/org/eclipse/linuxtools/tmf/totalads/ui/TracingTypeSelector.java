package org.eclipse.linuxtools.tmf.totalads.ui;

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

public class TracingTypeSelector {
	Combo cmbTraceTypes;
	TraceTypeFactory  traceFac=TraceTypeFactory.getInstance();
	/**
	 * Creates a combo box and populates it with all the trace types registered with the TraceTypeFactory
	 * @param parent
	 */
	public TracingTypeSelector(Composite parent){
		/*
		 * Trace Type Selection
		 */
		

		
		cmbTraceTypes= new Combo(parent,SWT.BORDER);
		cmbTraceTypes.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		//cmbTraceTypes.add("LTTng Kernel");
		//cmbTraceTypes.add("LTTng UST");
		//cmbTraceTypes.add("Regular Expression");
		//cmbTraceTypes.select(0);
		populateCombo(cmbTraceTypes);
		
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


}

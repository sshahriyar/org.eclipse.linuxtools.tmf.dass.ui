package org.eclipse.linuxtools.tmf.totalads.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

public class TracingTypeSelector {
	
	public TracingTypeSelector(Composite parent){
		/*
		 * Trace Type Selection
		 */
		
	
		
	}
	/**
	 * 
	 * @param combTraceTypes
	 */
	private void populateCombo(Combo combTraceTypes){
		TraceTypeFactory  traceFac=TraceTypeFactory.getInstance();
	    // populating anomaly detection models		
		String []traceReaders  = traceFac.getAllTraceTypeReaderKeys();
		
		if (traceReaders!=null)
			for (int i=0;i <traceReaders.length;i++)
				combTraceTypes.add(traceReaders[i]);
		   
	}


}

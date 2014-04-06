/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/

package org.eclipse.linuxtools.tmf.totalads.ui;

//import java.util.ArrayList;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Group;
//import org.eclipse.swt.widgets.Label;
//import org.eclipse.swt.widgets.Text;
//import org.eclipse.swt.widgets.TreeItem;
//import org.eclipse.wb.swt.SWTResourceManager;
//import org.eclipse.swt.events.SelectionAdapter;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.events.SelectionListener;
/**
 * Creates a combo box and populates it with all the trace types registered with the TraceTypeFactory
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class TracingTypeSelector {
	private Combo cmbTraceTypes;
	private TraceTypeFactory  traceFac;
	//ArrayList<IObserver> observers=new ArrayList<IObserver>();
	/**
	 * Constructor
	 * @param parent Composite
	 */
	public TracingTypeSelector(Composite parent){
		/*
		 * Trace Type Selection
		 */
		traceFac=TraceTypeFactory.getInstance();
		cmbTraceTypes= new Combo(parent,SWT.READ_ONLY);
		cmbTraceTypes.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false,1,1));
		populateCombo(cmbTraceTypes);
	
		
	}
	/**
	 * Populates the combo box with the Trace readers
	 * @param combTraceTypes Combo box which need to be populated
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
	 * @return A trace reader
	 */
	public ITraceTypeReader getSelectedType(){
		
		String key= cmbTraceTypes.getItem(cmbTraceTypes.getSelectionIndex());
		return traceFac.getTraceReader(key);
	}

	/**
	 * Selects a trace type reader in the combo box
	 * @param traceTypeName Type of the trace
	 */
	public void selectTraceType(String traceTypeName){
		for (int i=0;i<cmbTraceTypes.getItemCount();i++)
			if (cmbTraceTypes.getItem(i).equalsIgnoreCase(traceTypeName)){
				cmbTraceTypes.select(i);
				break;
			}
		}
	
}

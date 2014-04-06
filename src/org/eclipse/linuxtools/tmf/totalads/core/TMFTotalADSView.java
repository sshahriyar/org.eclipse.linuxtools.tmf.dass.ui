/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.core;

import org.eclipse.linuxtools.tmf.core.signal.TmfSignalHandler;
import org.eclipse.linuxtools.tmf.core.signal.TmfTraceSelectedSignal;
import org.eclipse.linuxtools.tmf.core.trace.ITmfTrace;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
import org.eclipse.linuxtools.tmf.totalads.ui.TotalADS;
import org.eclipse.linuxtools.tmf.ui.views.TmfView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * This class lauches TotalADS as a view of TMF
 * @author <p>Syed Shariyar Murtaza justsshary@hotmail.com</p>
 *
 */

public class TMFTotalADSView extends TmfView {
	//Variables
	private static final String VIEW_ID = "org.eclipse.linuxtools.tmf.totalads.ui.ADS01";
	private ITmfTrace currentTrace;
	private TotalADS comp;
	/**
	 * Constructor
	 */
	public TMFTotalADSView() {
		super(VIEW_ID);
		
	}
	/**
	 * Initalizes TotalADS
	 */
	@Override
	public void createPartControl(Composite parent) {
		
		comp=new TotalADS(parent,SWT.NONE);
		comp.setFocus();
        ITmfTrace trace = getActiveTrace();
        if (trace != null) {
            traceSelected(new TmfTraceSelectedSignal(this, trace));
        }
       
         
       
	}
   /**
    * Puts the focus on TotalADS
    */
	@Override
	public void setFocus() {
		comp.setFocus();
	}
	/**
	 * Gets called from TMF when a trace is selected
	 * @param signal
	 */
	@TmfSignalHandler
    public void traceSelected(final TmfTraceSelectedSignal signal) {
        // Don't populate the view again if we're already showing this trace
        if (currentTrace == signal.getTrace()) {
            return;
        }
        currentTrace = signal.getTrace();
     
            
        ITmfTrace trace = signal.getTrace();
    	// Right now we are not sure how to determine whether a trace is a user space trace or kernel space trace
        // so we are only considering kernel space traces
        Boolean isKernelSpace=true;
		ITraceTypeReader traceReader=TraceTypeFactory.getInstance().getCTFKernelorUserReader(isKernelSpace);

        comp.notifyOnTraceSelection(trace.getPath(), traceReader.getName());
        // trace.sendRequest(req);
    }
	
}

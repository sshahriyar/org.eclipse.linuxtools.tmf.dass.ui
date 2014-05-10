/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.ui.diagnosis;

import java.util.HashSet;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.linuxtools.tmf.core.signal.TmfSignalHandler;
import org.eclipse.linuxtools.tmf.core.signal.TmfTraceSelectedSignal;
import org.eclipse.linuxtools.tmf.core.trace.ITmfTrace;
import org.eclipse.linuxtools.tmf.totalads.core.TotalAdsPerspectiveFactory;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
import org.eclipse.linuxtools.tmf.totalads.ui.AnomaliesView;
import org.eclipse.linuxtools.tmf.totalads.ui.models.DataModelsView;
import org.eclipse.linuxtools.tmf.ui.views.TmfView;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
/**
 * This class creates the Diagnosis view
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class DiagnosisView extends TmfView {

	public static final String VIEW_ID = "org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.DiagnosisView";
	private ITmfTrace currentTrace;
	private Diagnosis diagnosis;
	private ResultsAndFeedback resultsAndFeedback;
	//private AlgorithmFactory algFactory;
	//private TraceTypeFactory trcTypeFactory;
	//private Handler handler;
	
	private AnomaliesView anomView;
	/**
	 * An inner class implementing the listener for other views initialised in {@link TotalAdsPerspectiveFactory} 
	 */
	 private class PerspectiveViewsListener implements IPartListener {
		 /// Registers a listener to Eclipse to get the object of  ResultsAndfeedback in anomView
		 // So it can be filled when evaluate button is pressed in the Diagnosis View
		 @Override  
	        public void partOpened(IWorkbenchPart part) {
			 System.out.println ("Part Opened "+part.getTitle());
	  		   if (part instanceof AnomaliesView) {
	  		    anomView = (AnomaliesView)part;
	  		    resultsAndFeedback=anomView.getResultsAndFeddbackInstance();
	  		    diagnosis.setResultsAndFeedbackInstance(resultsAndFeedback);
	  		   }
	  		  }
	
			@Override
			public void partActivated(IWorkbenchPart part) {
				System.out.println ("Part Activated "+part.getTitle());
				
			}
	
			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
				System.out.println ("Part BroughtToTop "+part.getTitle());
				System.out.println(getSite().getWorkbenchWindow().getPartService().getActivePart().getTitle());
				
			}
	
			@Override
			public void partClosed(IWorkbenchPart part) {
				System.out.println ("Part Closed "+part.getTitle());
				
			}
	
			@Override
			public void partDeactivated(IWorkbenchPart part) {
				System.out.println ("Part Deactivated "+part.getTitle());
				
			}
  	}
	 
	/**
	 * Constructor
	 */
	public DiagnosisView() {
		super(VIEW_ID);
		//System.out.println("In Diagnosis View");
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		//init();
		diagnosis=new Diagnosis(parent);
	
        ITmfTrace trace = getActiveTrace();
        if (trace != null) {
            traceSelected(new TmfTraceSelectedSignal(this, trace));
        } 
        
        /// Registers a listener to Eclipse to get the list of models selected (checked) by the user 
        getSite().getPage().addSelectionListener(DataModelsView.ID,	new ISelectionListener() {
			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		
				 if (part instanceof DataModelsView) {  
					   Object obj = ((org.eclipse.jface.viewers.StructuredSelection) selection).getFirstElement();
					   HashSet<String> modelList= (HashSet<String>)obj;
					   diagnosis.updateonModelSelection(modelList); 
				    }  
				}
		});
        
        /// Registers a listener to Eclipse to get the object of another view      		
  		getSite().getWorkbenchWindow().getPartService().addPartListener(new PerspectiveViewsListener());

	}

	
	
	
	/**
	 * Sets the focus
	 */
	@Override
	public void setFocus() {
		

	}

	/**
	 * Gets called from TMF when a trace is selected
	 * @param signal
	 */
	@TmfSignalHandler
    public void traceSelected(final TmfTraceSelectedSignal signal) {
      
        currentTrace = signal.getTrace();
     
            
        //ITmfTrace trace = signal.getTrace();
    	// Right now we are not sure how to determine whether a trace is a user space trace or kernel space trace
        // so we are only considering kernel space traces
        Boolean isKernelSpace=true;
		ITraceTypeReader traceReader=TraceTypeFactory.getInstance().getCTFKernelorUserReader(isKernelSpace);

        diagnosis.updateOnTraceSelection(currentTrace.getPath(), traceReader.getName());
        // trace.sendRequest(req);
    }
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.ui.views.TmfView#dispose()
	 */
	@Override
	public void dispose(){
		super.dispose();
	}
}

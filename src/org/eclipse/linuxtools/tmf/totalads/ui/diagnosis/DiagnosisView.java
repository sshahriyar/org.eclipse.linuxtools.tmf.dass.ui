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
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.linuxtools.tmf.core.signal.TmfSignalHandler;
import org.eclipse.linuxtools.tmf.core.signal.TmfTraceSelectedSignal;
import org.eclipse.linuxtools.tmf.core.trace.ITmfTrace;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.core.TotalAdsPerspectiveFactory;
import org.eclipse.linuxtools.tmf.totalads.dbms.IDBMS;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
import org.eclipse.linuxtools.tmf.totalads.ui.AnomaliesView;
import org.eclipse.linuxtools.tmf.totalads.ui.TotalADS;
import org.eclipse.linuxtools.tmf.totalads.ui.models.DataModelsView;
import org.eclipse.linuxtools.tmf.ui.views.TmfView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
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
	private AlgorithmFactory algFactory;
	private TraceTypeFactory trcTypeFactory;
	private Handler handler;
	
	private AnomaliesView anomView;
	/**
	 * An inner class implementing the listener for other views initialised in {@link TotalAdsPerspectiveFactory} 
	 */
	 private class PerspectiveViewsListener implements IPartListener {
		 /// Registers a listener to Eclipse to get the object of  ResultsAndfeedback in anomView
		 // So it can be filled whenme evaluate button is pressed in the Diagnosis View
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
		System.out.println("In Diagnosis View");
	}
	/**
	 * An overriden method gets called from Eclipse when the view is initialised
	 */
	@Override
	public void createPartControl(Composite parent) {
		init();
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
		// TODO

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
	 /**
		 * 
		 * Initialises TotalADS
		 * 
		 */
		private void init(){
			try{
			    Configuration.connection=new IDBMS();
				//	Configuration.connection.connect(Configuration.host, Configuration.port, "u","p");
				//String error=Configuration.connection.connect(Configuration.host, Configuration.port);
			
			//if (!error.isEmpty()){
		//			MessageBox msg=new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),SWT.ICON_ERROR);
			//	    msg.setMessage(error);
			//	    msg.open();
			//}	    
			
			algFactory= AlgorithmFactory.getInstance();
			algFactory.initialize();
			
			trcTypeFactory=TraceTypeFactory.getInstance();
			trcTypeFactory.initialize();
			// Initialise the logger
			handler=null;
			handler= new  FileHandler("totaladslog.xml");
	        Logger.getLogger("").addHandler(handler);	
				
			} catch (Exception ex) { // capture all the exceptions here, which are missed by Diagnois and Modeling classes
				
			   MessageBox msg=new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),SWT.ICON_ERROR);
			   if (ex.getMessage()!=null){
			      msg.setMessage(ex.getMessage());
			      msg.open();
			   }
			   Logger.getLogger(TotalADS.class.getName()).log(Level.SEVERE,null,ex);
			}
		}
	/**
	 * Disposes the view
	 */
	@Override
	public void dispose(){
		super.dispose();
		Configuration.connection.closeConnection();
		// This code deinitializes the  Factory instance. It was necessary because
		// if TotalADS plugin is reopened in running Eclipse, the static objects are not 
		// deinitialized on previous close of the plugin. 
		AlgorithmFactory.destroyInstance();
		TraceTypeFactory.destroyInstance();
		
	
	}
}

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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.linuxtools.tmf.core.signal.TmfSignalHandler;
import org.eclipse.linuxtools.tmf.core.signal.TmfTraceSelectedSignal;
import org.eclipse.linuxtools.tmf.core.trace.ITmfTrace;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
import org.eclipse.linuxtools.tmf.totalads.ui.models.DataModelsView;
import org.eclipse.linuxtools.tmf.totalads.ui.results.ResultsView;
import org.eclipse.linuxtools.tmf.totalads.ui.results.ResultsAndFeedback;
import org.eclipse.linuxtools.tmf.ui.views.TmfView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.jface.viewers.StructuredSelection;
/**
 * This class creates the Diagnosis view
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class DiagnosisView extends TmfView implements IDiagnosisObserver, ISelectionListener{

	public static final String VIEW_ID = "org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.DiagnosisView"; //$NON-NLS-1$
	private ITmfTrace currentTrace;
	private Diagnosis diagnosis;
	private HashSet<String> modelList;
	private DiagnosisPartListener partListener;

	/**
	 * Constructor
	 */
	public DiagnosisView() {
		super(VIEW_ID);
		diagnosis=new Diagnosis();
		partListener=new DiagnosisPartListener();
		partListener.addObserver(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite compParent) {


		diagnosis.createControl(compParent);

        ITmfTrace trace = getActiveTrace();
        if (trace != null) {
            traceSelected(new TmfTraceSelectedSignal(this, trace));
        }


        try {

	         // Trying to clear the already selected instances in the models view when this view is opened in the middle of execution
	         // If the view is opened in the middle, already selected models are not available using the event handler
	         IViewPart dataModelsView= getSite().getWorkbenchWindow().getActivePage().showView(DataModelsView.ID);
	         if (dataModelsView instanceof DataModelsView) {
                ((DataModelsView)dataModelsView).refresh();
            }

	     	/// Registers a listener to Eclipse to get the list of models selected (checked) by the user
	         getSite().getPage().addSelectionListener(DataModelsView.ID, this);

	        IViewPart viewRes= getSite().getWorkbenchWindow().getActivePage().showView(ResultsView.VIEW_ID);
	  		ResultsView resView=(ResultsView)viewRes;
	  		diagnosis.setResultsAndFeedbackInstance(resView.getResultsAndFeddbackInstance());

	  		//Registers a part listener
	  		getSite().getPage().addPartListener(partListener);


        } catch (PartInitException e) {

        	MessageBox msgBox=new MessageBox(getSite().getShell(),SWT.OK);
			if(e.getMessage()!=null){
				msgBox.setMessage(e.getMessage());
			} else {
                msgBox.setMessage("Unable to launch a view");
            }
			msgBox.open();
			Logger.getLogger(DiagnosisView.class.getName()).log(Level.SEVERE,null,e);
     	}


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
		getSite().getPage().removeSelectionListener(DataModelsView.ID, this);
		partListener.removeObserver(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.IDiagnosisObserver#update(org.eclipse.linuxtools.tmf.totalads.ui.results.ResultsAndFeedback)
	 */
	@Override
	public void update(ResultsAndFeedback results) {
		diagnosis.setResultsAndFeedbackInstance(results);


	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

		 if (part instanceof DataModelsView) {
			   Object obj = ((StructuredSelection) selection).getFirstElement();
			   modelList= (HashSet<String>)obj;
			   diagnosis.updateonModelSelection(modelList);
		    }
		}

}

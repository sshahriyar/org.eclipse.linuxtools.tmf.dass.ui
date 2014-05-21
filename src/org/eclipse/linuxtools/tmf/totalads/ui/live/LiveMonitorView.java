/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.ui.live;

import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.linuxtools.tmf.totalads.ui.models.DataModelsView;
import org.eclipse.linuxtools.tmf.totalads.ui.results.ResultsAndFeedback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * This class implements the LiveMonitor View
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class LiveMonitorView extends ViewPart  implements ISelectionListener,ILiveObserver{
	/**
	 * VIEW ID
	 */
    public static final String VIEW_ID = "org.eclipse.linuxtools.tmf.totalads.ui.live.LiveMonitorView"; //$NON-NLS-1$
	// Variables declaration
    private LiveMonitor fLiveMonitor;
	private LivePartListener fPartListener;


	 /**
	  * Constructor
	  */
	public LiveMonitorView() {
		fLiveMonitor=new LiveMonitor();
		fPartListener=new LivePartListener();
		fPartListener.addObserver(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite compParent) {
		fLiveMonitor.createControls(compParent);

		/// Registers a listener to Eclipse to get the list of models selected (checked) by the user
        getSite().getPage().addSelectionListener(DataModelsView.ID,	this );

        //Registers a part listener to Eclipse
        getSite().getPage().addPartListener(fPartListener);
       try {


			IViewPart viewRes= getSite().getWorkbenchWindow().getActivePage().showView(LiveResultsView.VIEW_ID);
			LiveResultsView liveView = (LiveResultsView)viewRes;

		    fLiveMonitor.setLiveChart(liveView.getLiveChart());
		    fLiveMonitor.setResultsAndFeedback(liveView.getResults());

		 // Trying to clear the already selected instances in the models view when this view is opened in the middle of execution
	         // If the view is opened in the middle, already selected models are not available using the event handler
	         IViewPart dataModelsView= getSite().getWorkbenchWindow().getActivePage().showView(DataModelsView.ID);
	         if (dataModelsView instanceof DataModelsView) {
                ((DataModelsView)dataModelsView).refresh();
            }


	    } catch (PartInitException e) {
	    	    MessageBox msgBox=new MessageBox(getSite().getShell(),SWT.OK);
				if(e.getMessage()!=null){
					msgBox.setMessage(e.getMessage());
				} else {
                    msgBox.setMessage("Unable to launch a view");
                }
				msgBox.open();
				Logger.getLogger(LiveMonitor.class.getName()).log(Level.SEVERE,null,e);

	    }

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose(){
		getSite().getPage().removeSelectionListener(DataModelsView.ID,this);
		fPartListener.removeObserver(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.ui.live.ILiveObserver#update(org.eclipse.linuxtools.tmf.totalads.ui.results.ResultsAndFeedback)
	 */
	@Override
	public void update(ResultsAndFeedback results) {
		fLiveMonitor.setResultsAndFeedback(results);

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

			 if (part instanceof DataModelsView) {
				   Object obj = ((org.eclipse.jface.viewers.StructuredSelection) selection).getFirstElement();
				   HashSet<String> modelList= (HashSet<String>)obj;
				   fLiveMonitor.updateOnModelSelction(modelList);
			    }
	}



}

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
import org.eclipse.linuxtools.tmf.totalads.ui.results.ResultsView;
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
	public static final String VIEW_ID = "org.eclipse.linuxtools.tmf.totalads.ui.live.LiveMonitorView";
	private LiveMonitor liveMonitor;
	private LivePartListener partListener;
	
	
	 /**
	  * Constructor
	  */
	public LiveMonitorView() {
		liveMonitor=new LiveMonitor();
		partListener=new LivePartListener();
		partListener.addObserver(this);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite compParent) {
		liveMonitor.createControls(compParent);
		
		/// Registers a listener to Eclipse to get the list of models selected (checked) by the user 
        getSite().getPage().addSelectionListener(DataModelsView.ID,	this );
        
        //Registers a part listener to Eclipse
        getSite().getPage().addPartListener(partListener);
       try {
		
		          
			IViewPart viewChart= getSite().getWorkbenchWindow().getActivePage().showView(LiveResultsView.VIEW_ID);
			LiveResultsView liveView = (LiveResultsView)viewChart;
			
		    liveMonitor.setLiveChart(liveView.getLiveChart());
		    liveMonitor.setResultsAndFeedback(liveView.getResults());
		    
			
	    } catch (PartInitException e) {
	    	    MessageBox msgBox=new MessageBox(getSite().getShell(),SWT.OK);
				if(e.getMessage()!=null){
					msgBox.setMessage(e.getMessage());
				}else
					msgBox.setMessage("Unable to launch a view");
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
		partListener.removeObserver(this);
	}
	
	/*
	 * 
	 */
	@Override
	public void update(ResultsAndFeedback results) {
		liveMonitor.setResultsAndFeedback(results);
		
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
				   liveMonitor.updateOnModelSelction(modelList); 
			    }  
	}

	

}

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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.linuxtools.tmf.totalads.core.TotalAdsPerspectiveFactory;
import org.eclipse.linuxtools.tmf.totalads.ui.models.DataModelsView;
import org.eclipse.linuxtools.tmf.totalads.ui.results.ResultsView;
//import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.ResultsAndFeedback;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

/**
 * This class implements the LiveMonitor View
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class LiveMonitorView extends ViewPart {
	public static final String VIEW_ID = "org.eclipse.linuxtools.tmf.totalads.ui.live.LiveMonitorView";
	
	//private ResultsView anomView;
	private LiveMonitor liveMonitor;
	/**
	 * An inner class implementing the listener for other views initialised in {@link TotalAdsPerspectiveFactory} 
	 */
	 private class PerspectiveViewsListener implements IPartListener {
		 /// Registers a listener to Eclipse to get the object of  ResultsAndfeedback in anomView
		 // So it can be filled when the evaluate button is pressed in the Diagnosis View
		 @Override  
	        public void partOpened(IWorkbenchPart part) {
	  		   if (part instanceof ResultsView) {
	  			   ResultsView anomView = (ResultsView)part;
	  		       liveMonitor.setResultsAndFeedback(anomView.getResultsAndFeddbackInstance());
	  		    
	  		   } else if (part instanceof LiveChartView){
	  			    LiveChartView chartView = (LiveChartView)part;
		  		    liveMonitor.setLiveChart(chartView.getLiveChart());
	  		   }
	  		   
	  		  }
	
			@Override
			public void partActivated(IWorkbenchPart part) {
				// TODO 
				
			}
	
			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
				// TODO 
				
			}
	
			@Override
			public void partClosed(IWorkbenchPart part) {
				// TODO 
				
			}
	
			@Override
			public void partDeactivated(IWorkbenchPart part) {
				// TODO 
				
			}
  	}
	 /**
	  * Constructor
	  */
	public LiveMonitorView() {
		liveMonitor=new LiveMonitor();
	}

	@Override
	public void createPartControl(Composite compParent) {
		liveMonitor.createControls(compParent);;
		
		/// Registers a listener to Eclipse to get the list of models selected (checked) by the user 
        getSite().getPage().addSelectionListener(DataModelsView.ID,	new ISelectionListener() {
			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		
				 if (part instanceof DataModelsView) {  
					   Object obj = ((org.eclipse.jface.viewers.StructuredSelection) selection).getFirstElement();
					   HashSet<String> modelList= (HashSet<String>)obj;
					   liveMonitor.updateOnModelSelction(modelList); 
				    }  
				}
		});
        
        /// Registers a listener to Eclipse to get the object of another view      		
  		getSite().getWorkbenchWindow().getPartService().addPartListener(new PerspectiveViewsListener());

	}

	@Override
	public void setFocus() {

	}

}

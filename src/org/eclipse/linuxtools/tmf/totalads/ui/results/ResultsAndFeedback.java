/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/

package org.eclipse.linuxtools.tmf.totalads.ui.results;

//import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.linuxtools.tmf.totalads.algorithms.Results;
import org.eclipse.swt.SWT;
//import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
//import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
//import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
//import org.eclipse.wb.swt.SWTResourceManager;
/**
 * This class creates GUI wdigets for the results and feedback.
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com</p>
 *
 */
public class ResultsAndFeedback {
	private Tree treeTraceResults;
	private Text txtAnomalyType;
	private Text txtAnomalySummary;
	private Text txtAnalysisDetails;
	private Label lblAnalysisCurrentAnomaly;
	private Group grpAnalysisResults;
	private Label lblAnomalySummary;
	private Label lblDetails;
	private Group grpResults;
	private Composite compTraceList;
	private Composite compSummary;
	private Label lblTreeTraceResult;
	private Composite compResAndFeedback;
	private Label lblTraceSummary;
	private Label lblSelectModel;
	private Combo cmbModels;
	private Text txtTraceSummary;
	private Integer maxAllowableTraces;
	private HashMap<String,Double> modelAndAnomalyCount;
	private Display display;
	private	 Shell dialogShel;
	private String traceToDelete;

	/**
	 * Constructor
	 * @param parent Composite object
	 * @param isDiagnosis false if model combo box is to be made visible
	 */
	public ResultsAndFeedback(Composite parent, Boolean isDiagnois) {
		detailsAndFeedBack(parent,isDiagnois);
		maxAllowableTraces=5000;
	}

	/**
	 * Constructor to create results object as a separate dialog form
	 * 
	 */
	public ResultsAndFeedback() {
		display = Display.getDefault();
		dialogShel = new Shell(display, SWT.BORDER | SWT.CLOSE |SWT.V_SCROLL);
		dialogShel.setLayout(new GridLayout(4, false));
	
		detailsAndFeedBack(dialogShel,false);
		maxAllowableTraces=5000;
		//adding event to avoid disposing it off
		dialogShel.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				event.doit=false;
				dialogShel.setVisible(false);
			}
		});
	}
	
	/**
	 * Shows the modal form
	 */
	public void showForm(){
		// open the form as a modal only if it has been created like this
		if (dialogShel!=null){
			dialogShel.open();
			while (!dialogShel.isDisposed()) {
			    if (!display.readAndDispatch()) {
			        display.sleep();
			    }
			}
			
		}
		
	}// end function ShowForm
	/**
	 * Close the shell dialog
	 */
	public void destroy(){
		if (dialogShel!=null)
		 dialogShel.dispose();
	}
	/**
	 * Creates widgets for details and results
	 * @param compParent Composite object
	 * @param isDiagnosis It is false if it is from live monitor and true if it s from diagnosis
	 */
	private void detailsAndFeedBack(Composite compParent, Boolean isDiagnosis){
		//Group "Feedback: Is it anomaly?"
				grpResults = new Group(compParent, SWT.NONE);
				grpResults.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,1,4));
				//grpAnalysisIdentify.setText("Results and Feedback");
				grpResults.setText("Results");
				grpResults.setLayout(new GridLayout(2,false));
				
				/////////////////////////////////////
				// Widgets for summary
				//////////////////////////////////////
				compSummary=new Composite(grpResults, SWT.None);
				compSummary.setLayout(new GridLayout(4,false));
				compSummary.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
				
				lblTraceSummary = new Label(compSummary, SWT.NONE);
				lblTraceSummary.setLayoutData(new GridData(SWT.LEFT,SWT.BOTTOM,false,false,2,1));
				lblTraceSummary.setText("Total Traces");
				
				txtTraceSummary= new Text(compSummary,SWT.BORDER);
				txtTraceSummary.setEditable(false);
				txtTraceSummary.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false,2,1));
				
				lblSelectModel = new Label(compSummary, SWT.NONE);
				lblSelectModel.setLayoutData(new GridData(SWT.LEFT,SWT.BOTTOM,false,false,1,1));
				lblSelectModel.setText("Select Models");
				
				cmbModels= new Combo(compSummary,SWT.BORDER|SWT.READ_ONLY);
				cmbModels.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false,1,1));
				if (isDiagnosis==true){
					cmbModels.setVisible(false);
					lblSelectModel.setVisible(false);
				}
				lblAnomalySummary = new Label(compSummary, SWT.NONE);
				lblAnomalySummary.setLayoutData(new GridData(SWT.RIGHT,SWT.BOTTOM,false,false,1,1));
				lblAnomalySummary.setText("Total Anomalies (%)");
				
				txtAnomalySummary= new Text(compSummary,SWT.BORDER);
				txtAnomalySummary.setEditable(false);
				txtAnomalySummary.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false,1,1));
				
				
				
                ///////////////////////////////////
				/// Widgets for Trace list
				/////////////////////////////////
				compTraceList=new Composite(grpResults, SWT.None);
				compTraceList.setLayout(new GridLayout(1,false));
				compTraceList.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true));
				
				lblTreeTraceResult=new Label(compTraceList,SWT.NONE);
				lblTreeTraceResult.setLayoutData(new GridData(SWT.FILL,SWT.TOP,false,false,1,1));
				lblTreeTraceResult.setText("Trace List (Select to See Results)");
				
				treeTraceResults=new Tree(compTraceList , SWT.BORDER |  SWT.FULL_SELECTION| SWT.V_SCROLL | SWT.H_SCROLL);
				treeTraceResults.setLinesVisible(true);
				treeTraceResults.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true,1,5));
				
				compResAndFeedback=new Composite(grpResults, SWT.NONE);
			    compResAndFeedback.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			    compResAndFeedback.setLayout(new GridLayout(1,false));
			    
			    results(compResAndFeedback);
			    
			    addHandlers();
			    
				//*** End Trace list
				
				//*** Group Feedback: To be included in the next version.The code is commented at the moment.
			    /*
				Group grpFeedback= new Group(compResAndFeedback,SWT.NONE);
				grpFeedback.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
				grpFeedback.setLayout(new GridLayout(2,false));
				grpFeedback.setText("Feedback");
				
			
				Label lblFeedback= new Label(grpFeedback,SWT.NONE);
				lblFeedback.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
				lblFeedback.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
				lblFeedback.setText("If anomaly, name its type and save it");
				
				Text txtFeedback = new Text(grpFeedback, SWT.BORDER);
				txtFeedback.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,2,1));
				txtFeedback.setTextLimit(15);
				
				Button btnSubmit = new Button(grpFeedback, SWT.NONE);
				btnSubmit.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, true,1,1));
				btnSubmit.setText("      Save      ");
				btnSubmit.addSelectionListener(new SelectionAdapter() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						// if there is a selected trace then process, else do nothing
						if (currentTraceResults !=null){
								//currentTraceResults.tracePath;
								//if (!txtFeedback.getText().isEmpty());
								//     disable save
								//		start a thread
								//      get the same reader, pass it to the classification model
							    //        in classification model
								//           extract events and their frequencies, store trace id in another collection
							    //            in the same db
								//     
							    // else
								// 		
						}
						
					}
					
				});
				*/
			  //**** End of group Feedback
			
			
				

	}
	/**
	 * Creates widgets or GUI elements for the results 
	 * @param compParent Composite object
	 */
	 private void results(Composite compParent ){
		/**
		 * 
		 * Result
		 *
		 */
		grpAnalysisResults=new Group(compParent,SWT.NONE);
		GridData gridDataResult=new GridData(SWT.FILL,SWT.FILL,true,true);
		gridDataResult.horizontalSpan=1;
		grpAnalysisResults.setLayoutData(gridDataResult);
		grpAnalysisResults.setLayout(new GridLayout(2,false));
		
		lblAnalysisCurrentAnomaly = new Label(grpAnalysisResults, SWT.NONE);
		lblAnalysisCurrentAnomaly.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));//gridDataResultLabels);
		lblAnalysisCurrentAnomaly.setText("Anomaly ");
		
		txtAnomalyType= new Text(grpAnalysisResults,SWT.BORDER);
		txtAnomalyType.setEditable(false);
		txtAnomalyType.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));//gridDataResultText);
			
		lblDetails=new Label(grpAnalysisResults,SWT.NONE);
		lblDetails.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,true,false,2,1));
		lblDetails.setText("Details");
		
		txtAnalysisDetails = new Text(grpAnalysisResults, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		txtAnalysisDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,2,2));
	
		/**
		 * End result group
		 * 
		 */
	}
	
	 /**
	  * This function adds the handlers for the different events called on widgets
	  */
	 private void addHandlers(){
		 //
			// Event handler for the tree
			//
			treeTraceResults.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					
					String modelName=cmbModels.getItem(cmbModels.getSelectionIndex());
					if (modelName==null || modelName.isEmpty())
						return;
					
					TreeItem item =(TreeItem)e.item;
					HashMap<String,Results> modelResults=(HashMap<String,Results>)item.getData();
					
					Results results=modelResults.get(modelName);
					
					//currentTraceResults=results;
					if (results.getAnomaly() && (results.getAnomalyType()!=null && !results.getAnomalyType().isEmpty()))
						txtAnomalyType.setText(results.getAnomalyType());
					else
						txtAnomalyType.setText(booleanAnomalyToString(results.getAnomaly()));
					
				 txtAnalysisDetails.setText(results.getDetails().toString());	
				}
			});
			
			/**
			 * Add Combo handlers
			 */
			cmbModels.addSelectionListener(new SelectionAdapter() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					String item=cmbModels.getItem(cmbModels.getSelectionIndex());
					if (item!=null && !item.isEmpty()){
						if (modelAndAnomalyCount!=null){
							Double anomalies=modelAndAnomalyCount.get(item);
							if (anomalies!=null){
								txtAnomalySummary.setText(anomalies.toString());
								txtAnalysisDetails.setText("");
								txtAnomalyType.setText("");
								
							}
						}
					}
					
				}
			});
	 }
	 
	 /**
	  * Add all model names
	  * @param modelNames Array of all the models whose results will appear in the results section
	  */
	 public void registerAllModelNames(final String []modelNames){
		 Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				 for (int j=0; j<modelNames.length; j++)
						cmbModels.add(modelNames[j]);
				 cmbModels.select(0);
			}
			
			 
		 });
				
	 }
	 
	 /**
	  * This function sets the maximum traces that are allowed to be displayed in the results sections
	  * @param maxTraces
	  */
	 public void  setMaxAllowableTrace(Integer maxTraces){
		 maxAllowableTraces=maxTraces;
	 }

	
	/**
	 * Assigns a trace and its results to appropriate widgets for viewing in Results Section.
	 * If the number of traces increase beyond maximum allowable traces then the first trace is removed
	 *  and its name is returned. First call registerAllModels function before calling this function.
	 * @param traceName Name of the trace
	 * @param modelResults Results of all the models as a HashMap
	 * @return Name of the trace removed or empty if none is removed
	 */
	public String addTraceResult(final String traceName, final HashMap<String,Results> modelResults){
		
		Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
		
				traceToDelete="";
				
				if (!traceName.isEmpty() && modelResults !=null){
					//Check if the same name already exists
					TreeItem []allItems=treeTraceResults.getItems();
					for (int j=0; j<allItems.length;j++)
						if (allItems[j].getText().equalsIgnoreCase(traceName))
							return; // if it exists do not add it again
							
					//Add if no such name is there						
					TreeItem item= new TreeItem(treeTraceResults, SWT.NONE);
					item.setText(traceName);
					item.setData(modelResults);
				
					if (treeTraceResults.getItemCount() >maxAllowableTraces){
						traceToDelete=treeTraceResults.getItem(0).getText();
						treeTraceResults.getItem(0).dispose();
					}
					
				
						
				}
			}
		});
		
		return traceToDelete;
	}
	
	/**
	 * Converts a boolean to displayable string
	 * @param anomaly
	 * @return A String value to be displayed
	 */
	private String booleanAnomalyToString(Boolean anomaly){
		if (anomaly)
			return "Yes, an anomaly.";
		else
			return "No, not an anomaly";
	}
	/** 
	 * 
	 * Clears the tree 
	 */
	public void clearData(){
		Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
		
				treeTraceResults.removeAll();
				txtAnomalyType.setText("");
				txtAnalysisDetails.setText("");
				txtAnomalySummary.setText("");
				txtTraceSummary.setText("");
				cmbModels.removeAll();

			}
		});
	}
	
	/**
	 * Sets the summary of results
	 * @param anomalyCount AnomalyCount as string
	 */
	public void setTotalAnomalyCount(final HashMap<String, Double> modelAnomCount){
		Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
		
				modelAndAnomalyCount=modelAnomCount;
				if (cmbModels.getItemCount() >0){
					int index=cmbModels.getSelectionIndex();
					if (index==-1) index=0;
					Double anomCount=modelAndAnomalyCount.get(cmbModels.getItem(index));
					if (anomCount!=null)
							txtAnomalySummary.setText(anomCount.toString());
					
				}
			}
		});
	}
	/**
	 * Sets the total trace count
	 * @param traceCount Total trace count
	 */
	public void setTotalTraceCount(final String traceCount){
		Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				txtTraceSummary.setText(traceCount);
				
			}
		});
		
	}
}

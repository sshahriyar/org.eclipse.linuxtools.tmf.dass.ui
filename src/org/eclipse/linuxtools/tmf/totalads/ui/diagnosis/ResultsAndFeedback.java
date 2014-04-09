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

//import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.algorithms.Results;
import org.eclipse.swt.SWT;
//import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
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
	private Text txtAnalysisCurrentAnomaly;
	private Text txtAnomalySummary;
	private Text txtAnalysisDetails;
	private Results currentTraceResults;
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
	private Text txtTraceSummary;
	/**
	 * Constructor
	 * @param parent Composite object
	 */
	public ResultsAndFeedback(Composite parent) {
		detailsAndFeedBack(parent);
		currentTraceResults=null;
	}

	/**
	 * Creates widgets for details and results
	 * @param compParent Composite object
	 */
	private void detailsAndFeedBack(Composite compParent){
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
				compSummary.setLayout(new GridLayout(7,false));
				compSummary.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
				
				lblTraceSummary = new Label(compSummary, SWT.NONE);
				lblTraceSummary.setLayoutData(new GridData(SWT.LEFT,SWT.BOTTOM,false,false,1,1));
				lblTraceSummary.setText("Total Traces");
				
				txtTraceSummary= new Text(compSummary,SWT.BORDER);
				txtTraceSummary.setEditable(false);
				txtTraceSummary.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false,1,1));
				
				lblAnomalySummary = new Label(compSummary, SWT.NONE);
				lblAnomalySummary.setLayoutData(new GridData(SWT.LEFT,SWT.BOTTOM,false,false,1,1));
				lblAnomalySummary.setText("Total Anomalies");
				
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
				//
				// Event handler for the tree
				//
				treeTraceResults.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						TreeItem item =(TreeItem)e.item;
						Results results=(Results)item.getData();
						currentTraceResults=results;
						if (results.getAnomaly() && (results.getAnomalyType()!=null && !results.getAnomalyType().isEmpty()))
							txtAnalysisCurrentAnomaly.setText(results.getAnomalyType());
						else
							txtAnalysisCurrentAnomaly.setText(booleanAnomalyToString(results.getAnomaly()));
						
					 txtAnalysisDetails.setText(results.getDetails().toString());	
					}
				});
				
				
				compResAndFeedback=new Composite(grpResults, SWT.NONE);
			    compResAndFeedback.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			    compResAndFeedback.setLayout(new GridLayout(1,false));
			    
			    results(compResAndFeedback);
			    
			    
			    
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
		
		txtAnalysisCurrentAnomaly= new Text(grpAnalysisResults,SWT.BORDER);
		txtAnalysisCurrentAnomaly.setEditable(false);
		txtAnalysisCurrentAnomaly.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));//gridDataResultText);
			
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
	 * Assigns a trace and its results to appropriate widgets for viewing in Results and Feedback Section 
	 * @param traceName Trace name
	 * @param results Results object
	 */
	
	public void addTraceResult(String traceName, Results results){
		
		if (!traceName.isEmpty() && results!=null){
		
			TreeItem item= new TreeItem(treeTraceResults, SWT.NONE);
			item.setText(traceName);
			item.setData(results);
			if (treeTraceResults.getItemCount()==1){
				
				if (results.getAnomaly() && (results.getAnomalyType()!=null && !results.getAnomalyType().isEmpty()))
					txtAnalysisCurrentAnomaly.setText(results.getAnomalyType());
				else
					txtAnalysisCurrentAnomaly.setText(booleanAnomalyToString(results.getAnomaly()));
				
				
				txtAnalysisDetails.setText(results.getDetails().toString());
			}
				
		}
		
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
		treeTraceResults.removeAll();
		txtAnalysisCurrentAnomaly.setText("");
		txtAnalysisDetails.setText("");
		txtAnomalySummary.setText("");
		txtTraceSummary.setText("");
	}
	/**
	 * Sets the summary of results
	 * @param anomalyCount AnomalyCount as string
	 */
	public void setTotalAnomalyCount(String anomalyCount){
		txtAnomalySummary.setText(anomalyCount);
		
	}
	/**
	 * Sets the total trace count
	 * @param traceCount Total trace count
	 */
	public void setTotalTraceCount(String traceCount){
		txtTraceSummary.setText(traceCount);
	}
}

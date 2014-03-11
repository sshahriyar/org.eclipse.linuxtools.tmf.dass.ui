package org.eclipse.linuxtools.tmf.totalads.ui;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class ResultsAndFeedback {
	Tree treeTraceResults;
	Text txtAnalysisCurrentAnomaly;
	Text txtAnalysisDetails;
	
	public ResultsAndFeedback(Composite parent) {
		detailsAndFeedBack(parent);
	}

	/**
	 * 
	 * @param comptbtmAnalysis
	 */
	private void detailsAndFeedBack(Composite comptbtmAnalysis){
		//Group "Feedback: Is it anomaly?"
				Group grpAnalysisIdentify = new Group(comptbtmAnalysis, SWT.NONE);
				grpAnalysisIdentify.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,1,4));
				grpAnalysisIdentify.setText("Results and Feedback");
				grpAnalysisIdentify.setLayout(new GridLayout(2,false));
                
				/// Trace list
				Composite compTraceList=new Composite(grpAnalysisIdentify, SWT.None);
				compTraceList.setLayout(new GridLayout(1,false));
				compTraceList.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
				
				Label lblTreeTraceResult=new Label(compTraceList,SWT.NONE);
				lblTreeTraceResult.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
				lblTreeTraceResult.setText("Trace List (Select to see results)");
				
				treeTraceResults=new Tree(compTraceList , SWT.BORDER |  SWT.FULL_SELECTION| SWT.V_SCROLL | SWT.H_SCROLL);
				treeTraceResults.setLinesVisible(true);
				treeTraceResults.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,1,5));
				
				treeTraceResults.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						TreeItem item =(TreeItem)e.item;
						IDetectionModels.Results results=(IDetectionModels.Results)item.getData();
						
						if (results.isAnomaly && (results.anomalyType!=null && !results.anomalyType.isEmpty()))
							txtAnalysisCurrentAnomaly.setText(results.anomalyType);
						else
							txtAnalysisCurrentAnomaly.setText(results.isAnomaly.toString());
						
					 txtAnalysisDetails.setText(results.details.toString());	
					}
				});
				
				
				Composite compResAndFeedback=new Composite(grpAnalysisIdentify, SWT.NONE);
			    compResAndFeedback.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			    compResAndFeedback.setLayout(new GridLayout(1,false));
			    
			    results(compResAndFeedback);
				//*** End Trace list
				
				//*** Group Feedback
			    
				Group grpFeedback= new Group(compResAndFeedback,SWT.NONE);
				grpFeedback.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,4));
				grpFeedback.setLayout(new GridLayout(2,false));
				grpFeedback.setText("Feedback");
				
				Button btnYes=new Button(grpFeedback,SWT.RADIO);
				btnYes.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true,2,1));
				btnYes.setText("Yes");
				
				Button btnNo=new Button(grpFeedback,SWT.RADIO);
				btnNo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true,2,1));
				btnNo.setText("No");
				
				Button btnOtherwise=new Button(grpFeedback,SWT.RADIO);
				btnOtherwise.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false,1,1));
				btnOtherwise.setText("Otherwise");
				
				Text txtAnalysisIdentify = new Text(grpFeedback, SWT.BORDER);
				txtAnalysisIdentify.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
				txtAnalysisIdentify.setEnabled(false);

				Button btnSubmit = new Button(grpFeedback, SWT.NONE);
				btnSubmit.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true,2,1));
				btnSubmit.setText("Submit");
			  //**** End of group Feedback
			
			
				

	}
	/**
	 * 
	 * @param comptbtmAnalysis
	 */
	private void results(Composite comptbtmAnalysis ){
		/**
		 * Result
		 *
		 */
		Group grpAnalysisResults=new Group(comptbtmAnalysis,SWT.NONE);
		GridData gridDataResult=new GridData(SWT.FILL,SWT.FILL,true,true);
		gridDataResult.horizontalSpan=1;
		grpAnalysisResults.setLayoutData(gridDataResult);
		grpAnalysisResults.setLayout(new GridLayout(2,false));
		grpAnalysisResults.setText("Results");

		
		CLabel lblAnalysisCurrentAnomaly = new CLabel(grpAnalysisResults, SWT.NONE);
		lblAnalysisCurrentAnomaly.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));//gridDataResultLabels);
		lblAnalysisCurrentAnomaly.setText("Anomaly (Anomaly Type)");
		
		txtAnalysisCurrentAnomaly= new Text(grpAnalysisResults,SWT.BORDER);
		txtAnalysisCurrentAnomaly.setEditable(false);
		txtAnalysisCurrentAnomaly.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));//gridDataResultText);
		//txtAnalysisCurrentAnomaly.setText("Stuxtnet-0XA");
		
		Label lblDetails=new Label(grpAnalysisResults,SWT.NONE);
		lblDetails.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
		lblDetails.setText("Details");
		
		txtAnalysisDetails = new Text(grpAnalysisResults, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		txtAnalysisDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,2,2));
		//txtAnalysisDetails.setText("\"FS\" : 0.53\n \"MM\" : 0.12\n \"KL\" : 0.18\n \"AC\" : 0.01 \n\"IPC\" : 0\n \"NT\" : 0.01\n \"SC\" : 0\n \"UN\" : 0.18");
		//txtAnalysisDetails.setMessage("Details");
				/**
		 * End result group
		 * 
		 */
	}
	/**
	 * Assigns a trace and its results to appropriate widgets for viewing in Results and Feedback Section 
	 * @param traceName
	 * @param results
	 */
	//HashMap <String, IDetectionModels.Results> resultsList=new HashMap<String, IDetectionModels.Results>();
	public void addTraceResult(String traceName, IDetectionModels.Results results){
		
		if (!traceName.isEmpty() && results!=null){
		
			TreeItem item= new TreeItem(treeTraceResults, SWT.NONE);
			item.setText(traceName);
			item.setData(results);
			if (treeTraceResults.getItemCount()==1){
				
				if (results.isAnomaly && (results.anomalyType!=null && !results.anomalyType.isEmpty()))
					txtAnalysisCurrentAnomaly.setText(results.anomalyType);
				else
					txtAnalysisCurrentAnomaly.setText(results.isAnomaly.toString());
				
				txtAnalysisDetails.setText(results.details.toString());
			}
				
		}
		
	}
	/** Clears the tree 
	 * 
	 */
	public void clearTree(){
		treeTraceResults.removeAll();
	}
}

package org.eclipse.linuxtools.tmf.totalads.ui;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class Diagnosis {

	public Diagnosis(CTabFolder tabFolderDetector){
		
		CTabItem tbtmAnalysis = new CTabItem(tabFolderDetector, SWT.NONE);
		tbtmAnalysis.setText("Diagnosis");
		
		ScrolledComposite scrolCompAnom=new ScrolledComposite(tabFolderDetector, SWT.H_SCROLL | SWT.V_SCROLL);
		
		Composite comptbtmAnalysis = new Composite(scrolCompAnom,SWT.NONE);
		tbtmAnalysis.setControl(scrolCompAnom);
		
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan=1;
		comptbtmAnalysis.setLayoutData(gridData);
		comptbtmAnalysis.setLayout(new GridLayout(2, false));
		
		selectTracesAndDirectory(comptbtmAnalysis);
		
		
		ModelSelector mdlSelector=new ModelSelector(comptbtmAnalysis);
		

		resultsandFeedBack(comptbtmAnalysis);
		ModelDetailsRenderer modRenderer=new ModelDetailsRenderer(comptbtmAnalysis);
		
		scrolCompAnom.setContent(comptbtmAnalysis);
		 // Set the minimum size
		scrolCompAnom.setMinSize(600, 600);

	    // Expand both horizontally and vertically
		scrolCompAnom.setExpandHorizontal(true);
		scrolCompAnom.setExpandVertical(true);
	}

	/**
	 * 
	 * @param comptbtmAnalysis
	 */
	private void resultsandFeedBack(Composite comptbtmAnalysis ){
		/**
		 * Result
		 *
		 */
		Group grpAnalysisResults=new Group(comptbtmAnalysis,SWT.NONE);
		GridData gridDataResult=new GridData(SWT.FILL,SWT.TOP,true,false);
		gridDataResult.horizontalSpan=2;
		grpAnalysisResults.setLayoutData(gridDataResult);
		grpAnalysisResults.setLayout(new GridLayout(2,false));
		grpAnalysisResults.setText("Results");
		
		//Composite comp
		Composite compResult=new Composite(grpAnalysisResults, SWT.BORDER);
		compResult.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		compResult.setLayout(new GridLayout(2,false));
		
		CLabel lblAnalysisCurrentTrace= new CLabel(compResult, SWT.NONE);
		lblAnalysisCurrentTrace.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
		lblAnalysisCurrentTrace.setText("Current Trace:");
		
		Text txtAnalysisCurrentTrace= new Text(compResult,SWT.BORDER);
		txtAnalysisCurrentTrace.setEditable(false);
		txtAnalysisCurrentTrace.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		txtAnalysisCurrentTrace.setText("kernel-session-01");
		
		CLabel lblAnalysisCurrentModel = new CLabel(compResult, SWT.NONE);
		lblAnalysisCurrentModel.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));//gridDataResultsLabel
		//lblAnalysisCurrentModel.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblAnalysisCurrentModel.setText("Current Model:");
		
		Combo combAnalysisCurrentModel=new Combo(compResult,SWT.BORDER);
		combAnalysisCurrentModel.add("KSM");
		combAnalysisCurrentModel.add("Decision Tree");
		combAnalysisCurrentModel.select(0);
		combAnalysisCurrentModel.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));//gridDataResultText);
		//Text txtAnalysisCurrentModel= new Text(compResult,SWT.BORDER);
		//txtAnalysisCurrentModel.setEditable(false);
		//txtAnalysisCurrentModel.setLayoutData(gridDataResultText);
		///txtAnalysisCurrentModel.setText("KSM");
		
		CLabel lblAnalysisCurrentAnomaly = new CLabel(compResult, SWT.NONE);
		lblAnalysisCurrentAnomaly.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));//gridDataResultLabels);
		lblAnalysisCurrentAnomaly.setText("Anomaly:");
		
		Text txtAnalysisCurrentAnomaly= new Text(compResult,SWT.BORDER);
		txtAnalysisCurrentAnomaly.setEditable(false);
		txtAnalysisCurrentAnomaly.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));//gridDataResultText);
		txtAnalysisCurrentAnomaly.setText("Yes");
		
		//Group "Feedback: Is it anomaly?"
		Group grpAnalysisIdentify = new Group(grpAnalysisResults, SWT.NONE);
		grpAnalysisIdentify.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,2));
		grpAnalysisIdentify.setText("Feddback: Is it Anomaly?");
		grpAnalysisIdentify.setLayout(new GridLayout(2,false));

		
		Combo comboAnalysisIdentify = new Combo(grpAnalysisIdentify, SWT.NONE);
		comboAnalysisIdentify.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));
		comboAnalysisIdentify.setItems(new String[] {"Yes", "No", "Other"});
		comboAnalysisIdentify.select(0);
		
		Text txtAnalysisIdentify = new Text(grpAnalysisIdentify, SWT.BORDER);
		txtAnalysisIdentify.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		txtAnalysisIdentify.setEnabled(false);
		txtAnalysisIdentify.setText("Enter other type");
		
		CLabel lblAnalysisGrpEmpty = new CLabel(grpAnalysisIdentify, SWT.NONE);
		Button btnSubmit = new Button(grpAnalysisIdentify, SWT.NONE);
		btnSubmit.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		btnSubmit.setText("Submit");
		new Label(grpAnalysisResults, SWT.NONE);
		// End group "Feedback: Is it Anomaly?"		
		/**
		 * End result group
		 * 
		 */
	}
	
	private void selectTracesAndDirectory(Composite comptbtmAnalysis){
		/**
		 *  Group trace selection
		 */
		Group grpTraceSelection = new Group(comptbtmAnalysis, SWT.NONE);
		grpTraceSelection.setText("Select Traces");
		
		grpTraceSelection.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		grpTraceSelection.setLayout(new GridLayout(3,false));
		
		Button btnCheckTraces = new Button(grpTraceSelection, SWT.CHECK);
		btnCheckTraces.setText("Select Directory");
		btnCheckTraces.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false, false));
		
		
		Text txtTestDir=new Text(grpTraceSelection, SWT.BORDER);
		txtTestDir.setEnabled(false);
		txtTestDir.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		
		Button btnTestSelectTraces = new Button(grpTraceSelection, SWT.NONE);
		btnTestSelectTraces.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,true,false));
		btnTestSelectTraces.setText("Browse");
		btnTestSelectTraces.setEnabled(false);
		
		
		//table traceList
		Composite comptblTraceList = new Composite(grpTraceSelection,  SWT.BORDER );
		comptblTraceList.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,3,1));
		
		
		Table tblAnalysisTraceList = new Table(comptblTraceList, SWT.BORDER | SWT.FULL_SELECTION|SWT.H_SCROLL|SWT.V_SCROLL);
		tblAnalysisTraceList.setHeaderVisible(true);
		tblAnalysisTraceList.setLinesVisible(true);
		//tblAnalysisTraceList.setLayoutData(new GridData(400,tblAnalysisTraceList.getit*3));
		
		TableColumn tblclmnAnalysisTime = new TableColumn(tblAnalysisTraceList, SWT.NONE);
		tblclmnAnalysisTime.setText("Time");
		
		TableColumn tblclmnAnalysisTraceid = new TableColumn(tblAnalysisTraceList, SWT.NONE);
		tblclmnAnalysisTraceid.setText("Trace ID");
		
	    //auto adjust column width
		TableColumnLayout tblColumnLayout = new TableColumnLayout();

		comptblTraceList.setLayout( tblColumnLayout );
		tblColumnLayout.setColumnData( tblclmnAnalysisTime, new ColumnWeightData( 50 ) );
		tblColumnLayout.setColumnData( tblclmnAnalysisTraceid , new ColumnWeightData( 50 ) );
		
		//add data to columns
		TableItem tableItemAnalysisAnomHistory = new TableItem(tblAnalysisTraceList, SWT.NONE);
		tableItemAnalysisAnomHistory.setText(new String[] {"07-12-2013: 2:40", "kernel-session-01-03"});

		TableItem tableItem2AnalysisAnomHistory = new TableItem(tblAnalysisTraceList, SWT.NONE);
		tableItem2AnalysisAnomHistory.setText(new String[] {"08-12-2013: 3:45", "kernel-session-12-2013"});
		
//		TableItem tableItem3AnalysisAnomHistory = new TableItem(tblAnalysisTraceList, SWT.NONE);
		//tableItem3AnalysisAnomHistory.setText(new String[] {"08-12-2013: 3:45", "kernel-session-12-2013"});
		
		//int desiredHeight = tblAnalysisTraceList.getItemHeight() * 2 + tblAnalysisTraceList.getHeaderHeight();
		int itemHeight = tblAnalysisTraceList.getItemHeight();
		GridData dataGridTable = new GridData(SWT.FILL, SWT.FILL, true, false);
		dataGridTable.heightHint = 20 * itemHeight;
		
		//tblAnalysisTraceList.setLayoutData(dataGridTable);
		//tblAnalysisTraceList.setLayoutData(new GridData(400,itemHeight*3));
		//tblAnalysisTraceList.setItemCount(2);
		
		MultipleTracesLoader traceDispLoader= new MultipleTracesLoader();
		
		/**
		 * End group trace selection
		 */
	}

}

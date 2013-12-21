package org.eclipse.linuxtools.tmf.dass.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.layout.GridData;

import swing2swt.layout.FlowLayout;
import swing2swt.layout.BoxLayout;

import org.eclipse.swt.custom.StackLayout;

import swing2swt.layout.BorderLayout;

import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.TableItem;

//public class CompDetective extends Composite {
public class CompDetective  {
	private Table tblAnalysisTraceList;
	private Text txtAnalysisIdentify;
	private Text txtAnalysisDetails;
	private Text txtTraceTypeRegularExpression;
	private Text lblAnomaliesTrain;
	private Text lblAnomaliesValidate;
	private Text lblAnomaliesTest;
	private Text txtAnomaliesProgress;
	private Table tableClassificationPredictions;
	
	
	
	//GridData gridDataFullFill=new GridData(SWT.FILL, SWT.FILL, true, true );
	//GridData gridDataHorizontalFill=new GridData(SWT.FILL, SWT.TOP, true, false );
	//GridData gridDataVerticalFill=new GridData(SWT.TOP, SWT.FILL, false, true );
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CompDetective(Composite parent, int style) {
		
		//super(parent, style);
		parent.setLayout(new GridLayout(2,false));
		
		leftPane(parent);
		CTabFolder tabFolderDetector = new CTabFolder(parent, SWT.BORDER);
		tabFolderDetector.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan=1;
		tabFolderDetector.setLayoutData(gridData);
		tabFolderDetector.setFocus();
		anomalyDetectionTab(tabFolderDetector);
		modelingTab(tabFolderDetector);



	}
	private void leftPane(Composite parent){
		
		
		Composite compLeftPane=new Composite(parent,SWT.BORDER);
		
		GridData gridData=new GridData(SWT.LEFT, SWT.FILL, false, true);
		gridData.horizontalSpan=1;
		compLeftPane.setLayoutData(gridData);
		compLeftPane.setLayout(new GridLayout(1,false));
		
		CLabel lblSelectSystem = new CLabel(compLeftPane, SWT.NONE);
		lblSelectSystem.setLayoutData(new GridData(SWT.FILL, SWT.TOP,true,false));
		lblSelectSystem.setText("Hosts");
		lblSelectSystem.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));

		//button
		Button btnAnomaliesAddSystem = new Button(compLeftPane, SWT.NONE);
		btnAnomaliesAddSystem.setLayoutData(new GridData(SWT.FILL, SWT.TOP,true,false));
		btnAnomaliesAddSystem.setText("Add New Host");
		
		
		//tree
		Tree treeAnomaliesSystems = new Tree(compLeftPane, SWT.BORDER | SWT.CHECK);
		//treeAnomaliesSystems.setLayoutData(gridData);
		treeAnomaliesSystems.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
			
		///////data
		TreeItem item1AnomS = new TreeItem(treeAnomaliesSystems, SWT.NONE);
	    item1AnomS.setText("Host-app-01");
		TreeItem item2AnomS = new TreeItem(treeAnomaliesSystems, SWT.NONE);
		item2AnomS.setText("Android-01s");
		TreeItem item3AnomS = new TreeItem(treeAnomaliesSystems, SWT.NONE);
		item3AnomS.setText("Host-Sys-01");
		
		/*
		 * Trace Type Selection
		 */
		
		Group grpTraceType = new Group(compLeftPane, SWT.NONE);
		grpTraceType.setText("Tracing Mode");
		grpTraceType.setLayoutData(new GridData(SWT.LEFT,SWT.BOTTOM,true,false));
		grpTraceType.setLayout(new GridLayout(1,false));
		
		GridData traceTypeGridData=new GridData(SWT.FILL,SWT.TOP,true,false);
		
		Button btnTraceTypeLttngkernel = new Button(grpTraceType, SWT.CHECK);
		btnTraceTypeLttngkernel.setLayoutData(traceTypeGridData);
		btnTraceTypeLttngkernel.setText("LTTng-kernel");
		
		Button btnTraceTypeLttngust = new Button(grpTraceType, SWT.CHECK);
		btnTraceTypeLttngust.setLayoutData(traceTypeGridData);
		btnTraceTypeLttngust.setText("LTTng-UST");
		
		Button btnTraceTypeText = new Button(grpTraceType, SWT.CHECK);
		btnTraceTypeText.setLayoutData(traceTypeGridData);
		btnTraceTypeText.setText("Text");
		
		txtTraceTypeRegularExpression = new Text(grpTraceType, SWT.BORDER);
		txtTraceTypeRegularExpression.setLayoutData(traceTypeGridData);
		txtTraceTypeRegularExpression.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		txtTraceTypeRegularExpression.setText("Enter Regular Expression");
		
		
		
	}
	/**
	 * 
	 * @param tabFolderDetector
	 */
	private void anomalyDetectionTab(CTabFolder tabFolderDetector){
		
		CTabItem tbtmAnalysis = new CTabItem(tabFolderDetector, SWT.NONE);
		tbtmAnalysis.setText("Anomaly Detection");
		
		Composite comptbtmAnalysis = new Composite(tabFolderDetector,SWT.NONE);
		tbtmAnalysis.setControl(comptbtmAnalysis);
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan=1;
		comptbtmAnalysis.setLayoutData(gridData);
		comptbtmAnalysis.setLayout(new GridLayout(2, false));
		
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
		
		
		//table anomaly history
		Composite comptblTraceList = new Composite(grpTraceSelection,  SWT.BORDER );
		comptblTraceList.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,3,1));
		
		
		tblAnalysisTraceList = new Table(comptblTraceList, SWT.BORDER | SWT.FULL_SELECTION|SWT.H_SCROLL|SWT.V_SCROLL);
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
		
	//	TableItem tableItem3AnalysisAnomHistory = new TableItem(tblAnalysisTraceList, SWT.NONE);
		//tableItem3AnalysisAnomHistory.setText(new String[] {"08-12-2013: 3:45", "kernel-session-12-2013"});
		
		//int desiredHeight = tblAnalysisTraceList.getItemHeight() * 2 + tblAnalysisTraceList.getHeaderHeight();
		int itemHeight = tblAnalysisTraceList.getItemHeight();
		GridData dataGridTable = new GridData(SWT.FILL, SWT.FILL, true, false);
		dataGridTable.heightHint = 20 * itemHeight;
		
		//tblAnalysisTraceList.setLayoutData(dataGridTable);
		//tblAnalysisTraceList.setLayoutData(new GridData(400,itemHeight*3));
		//tblAnalysisTraceList.setItemCount(2);
		
		/**
		 * End group trace selection
		 */

		/**
		 *  Group model selection
		 */
		
		Group grpAnalysisModelSelection=new Group(comptbtmAnalysis,SWT.NONE);	
		grpAnalysisModelSelection.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		grpAnalysisModelSelection.setLayout(new GridLayout(2,false));
		grpAnalysisModelSelection.setText("Select Models");
		
		Tree treeAnalysisModels = new Tree(grpAnalysisModelSelection, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION| SWT.V_SCROLL | SWT.H_SCROLL);
		treeAnalysisModels.setLinesVisible(true);
		treeAnalysisModels.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,2,4));
		///////data
		TreeItem item1 = new TreeItem(treeAnalysisModels, SWT.NONE);
	    item1.setText("KSM (alpha=0.04)");
	    TreeItem item2 = new TreeItem(treeAnalysisModels, SWT.NONE);
	    item2.setText("Sliding Window (width=5)");
	    TreeItem item3 = new TreeItem(treeAnalysisModels, SWT.NONE);
	    item3.setText("HMM (states=10)");
		
	    Button btnAnalysisEvaluateModels=new Button(grpAnalysisModelSelection, SWT.NONE);
		btnAnalysisEvaluateModels.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,false,false,2,1));
		btnAnalysisEvaluateModels.setText("Evaluate Models");
				
		/**
		 * End group model selection 
		*/

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
		GridData gridDataResultLabels=new GridData(SWT.FILL,SWT.BOTTOM,true,false);
		GridData gridDataResultText=new GridData(SWT.FILL,SWT.TOP,true,false);
		
		CLabel lblAnalysisCurrentTrace= new CLabel(compResult, SWT.NONE);
		lblAnalysisCurrentTrace.setLayoutData(gridDataResultLabels);
		lblAnalysisCurrentTrace.setText("Current Trace:");
		
		Text txtAnalysisCurrentTrace= new Text(compResult,SWT.BORDER);
		txtAnalysisCurrentTrace.setEditable(false);
		txtAnalysisCurrentTrace.setLayoutData(gridDataResultText);
		txtAnalysisCurrentTrace.setText("kernel-session-01");
		
		CLabel lblAnalysisCurrentModel = new CLabel(compResult, SWT.NONE);
		lblAnalysisCurrentModel.setLayoutData(gridDataResultLabels);
		//lblAnalysisCurrentModel.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblAnalysisCurrentModel.setText("Current Model:");
		
		Text txtAnalysisCurrentModel= new Text(compResult,SWT.BORDER);
		txtAnalysisCurrentModel.setEditable(false);
		txtAnalysisCurrentModel.setLayoutData(gridDataResultText);
		txtAnalysisCurrentModel.setText("KSM");
		
		CLabel lblAnalysisCurrentAnomaly = new CLabel(compResult, SWT.NONE);
		lblAnalysisCurrentAnomaly.setLayoutData(gridDataResultLabels);
		lblAnalysisCurrentAnomaly.setText("Anomaly:");
		
		Text txtAnalysisCurrentAnomaly= new Text(compResult,SWT.BORDER);
		txtAnalysisCurrentAnomaly.setEditable(false);
		txtAnalysisCurrentAnomaly.setLayoutData(gridDataResultText);
		txtAnalysisCurrentAnomaly.setText("Yes");
		
		//Group Identify anomaly
		Group grpAnalysisIdentify = new Group(grpAnalysisResults, SWT.NONE);
		grpAnalysisIdentify.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,2));
		grpAnalysisIdentify.setText("Identify Anomaly");
		grpAnalysisIdentify.setLayout(new GridLayout(2,false));

		
		Combo comboAnalysisIdentify = new Combo(grpAnalysisIdentify, SWT.NONE);
		comboAnalysisIdentify.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false));
		comboAnalysisIdentify.setItems(new String[] {"Yes", "No", "Other"});
		comboAnalysisIdentify.select(0);
		
		txtAnalysisIdentify = new Text(grpAnalysisIdentify, SWT.BORDER);
		txtAnalysisIdentify.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		txtAnalysisIdentify.setEnabled(false);
		txtAnalysisIdentify.setText("Enter other type");
		
		CLabel lblAnalysisGrpEmpty = new CLabel(grpAnalysisIdentify, SWT.NONE);
		Button btnSubmit = new Button(grpAnalysisIdentify, SWT.NONE);
		btnSubmit.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		btnSubmit.setText("Submit");
		// End group identify anomaly		
		/**
		 * End result group
		 * 
		 */
		
				
		txtAnalysisDetails = new Text(comptbtmAnalysis, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		txtAnalysisDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		txtAnalysisDetails.setText("\"FS\" : 0.53\n \"MM\" : 0.12\n \"KL\" : 0.18\n \"AC\" : 0.01 \n\"IPC\" : 0\n \"NT\" : 0.01\n \"SC\" : 0\n \"UN\" : 0.18");
		//txtAnalysisDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,1,10));
				
		CLabel lblAnalysisChart = new CLabel(comptbtmAnalysis, SWT.BORDER);
		lblAnalysisChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		lblAnalysisChart.setImage(SWTResourceManager.getImage("/home/umroot/experiments/workspace/tmf-ads/"
				+ "org.eclipse.linuxtools/lttng/org.eclipse.linuxtools.tmf.dass.ui/icons/java-twiki-metrpreter2.png"));
		//lblAnalysisChart.setText("");
	}
	

	private void modelingTab(CTabFolder tabFolderDetector){
		
		CTabItem tbtmModeling = new CTabItem(tabFolderDetector, SWT.NONE);
		tbtmModeling.setText("Modeling");
		
		GridLayout gridTwoColumns=new GridLayout(2,false);
		GridLayout gridOneColumn=new GridLayout(1,false);
		
		Composite comptbtmModeling = new Composite(tabFolderDetector, SWT.NONE);
		comptbtmModeling.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		comptbtmModeling.setLayout(gridTwoColumns);
		tbtmModeling.setControl(comptbtmModeling);
		
		/**
		 * Group modeling type and traces
		 */
		Group grpTracesModeling=new Group(comptbtmModeling, SWT.NONE);
		grpTracesModeling.setText("Select Traces and Modeling Type");
		grpTracesModeling.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
		grpTracesModeling.setLayout(gridTwoColumns);
		
		Button btnModelingTrain = new Button(grpTracesModeling, SWT.CHECK);
		btnModelingTrain.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		btnModelingTrain.setText("Training");
		
		Button btnModelingValidation = new Button(grpTracesModeling, SWT.CHECK);
		btnModelingValidation.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		btnModelingValidation.setText("Validation");
		
		Text txtModelingTraces = new Text(grpTracesModeling, SWT.BORDER);
		txtModelingTraces.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		Button btnModelingBrowse =new Button(grpTracesModeling, SWT.NONE);
		btnModelingBrowse.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false));
		btnModelingBrowse.setText("Browse Directory");
		
		
		/**
		 * End group modeling type and traces
		 */
		/**
		 * Group anomaly detection models
		 */
		Group grpAnomalyDetection=new Group(comptbtmModeling, SWT.NONE);
		grpAnomalyDetection.setText("Anomaly Detection Models");
		grpAnomalyDetection.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,4));
		grpAnomalyDetection.setLayout(gridOneColumn);
				
		Tree treeAnomaliesModels = new Tree(grpAnomalyDetection, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.MULTI);
		treeAnomaliesModels.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,4));
		treeAnomaliesModels.setLinesVisible(true);
		
		///////data
		TreeItem item1Anom = new TreeItem(treeAnomaliesModels, SWT.NONE);
	    item1Anom.setText("KSM");
	     TreeItem item2Anom = new TreeItem(treeAnomaliesModels, SWT.NONE);
	    item2Anom.setText("Sliding Window");
	    TreeItem item3Anom = new TreeItem(treeAnomaliesModels, SWT.NONE);
	    item3Anom.setText("HMM");
	    
	    Button btnAnomalyDetection=new Button(grpAnomalyDetection,SWT.NONE);
	    btnAnomalyDetection.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,false,false));
	    btnAnomalyDetection.setText("Evaluate");
	    /**
		 * End group anomaly detection models
		 */
	    /**
		 * Group classification  models
		 */
		Group grpClassification=new Group(comptbtmModeling, SWT.NONE);
		grpClassification.setText("Classification Models");
		grpClassification.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,4));
		grpClassification.setLayout(gridOneColumn);
				
		Tree treeClassificationModels = new Tree(grpClassification, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION | SWT.MULTI);
		treeClassificationModels.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		treeClassificationModels.setLinesVisible(true);
		
		///////data
		TreeItem item1Classification = new TreeItem(treeClassificationModels, SWT.NONE);
		item1Classification.setText("Decision Trees");
	     
	    Button btnClassification=new Button(grpClassification,SWT.NONE);
	    btnClassification.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,false,false));
	    btnClassification.setText("Evaluate");
	    /**
		 * End group classification  models
		 */
	    
	    
	    /**
		 * Progress Console
		 * 		
		 */
			
		
		CLabel lblProgressConsole = new CLabel(comptbtmModeling, SWT.NONE);
		lblProgressConsole.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,true,false,2,1));
		lblProgressConsole.setText("Progress Console");
		lblProgressConsole.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		
		
		txtAnomaliesProgress = new Text(comptbtmModeling, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		txtAnomaliesProgress.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,2,2));
		txtAnomaliesProgress.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		txtAnomaliesProgress.setText("Reading Trace Kernel-session-27-13\nTransforming to states\nInserting into the database host-app-01\n.....................\n");
		
		
	
		
	}
	
	
	//@Override
//	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	//}
	
	//public void SWTApp(Display display) {
	       
	        
	  //  }


	   


	    public static void main(String[] args) {
	        Display display = new Display();
	        org.eclipse.swt.widgets.Shell shell= new org.eclipse.swt.widgets.Shell(display);
	        //shell.setText("Center");
	       // shell.setSize(250, 200);
	       // shell.setLayout(new GridLayout(3,false));
	       //center(shell);
	        CompDetective det=new CompDetective(shell, SWT.BORDER);
	      
	        /// centre
	        org.eclipse.swt.graphics.Rectangle bds = shell.getDisplay().getBounds();

	        org.eclipse.swt.graphics.Point p = shell.getSize();

	        int nLeft = (bds.width - p.x) / 2;
	        int nTop = (bds.height - p.y) / 2;

	        shell.setBounds(nLeft, nTop, p.x, p.y);
	        
	        
	        //det.pack();
	        shell.pack();
	        shell.open();
	        

	        while (!shell.isDisposed()) {
	          if (!display.readAndDispatch()) {
	            display.sleep();
	          }
	        }
	        
	        display.dispose();
	    }
}

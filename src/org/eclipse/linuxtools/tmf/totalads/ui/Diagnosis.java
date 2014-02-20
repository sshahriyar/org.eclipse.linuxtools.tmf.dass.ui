package org.eclipse.linuxtools.tmf.totalads.ui;

import java.lang.reflect.Method;

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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.sun.media.sound.ModelConnectionBlock;

public class Diagnosis {
	Table tblAnalysisTraceList;

	public Diagnosis(CTabFolder tabFolderDetector) throws SecurityException, NoSuchMethodException{
		
		CTabItem tbtmAnalysis = new CTabItem(tabFolderDetector, SWT.NONE);
		tbtmAnalysis.setText("Diagnosis");
		
		ScrolledComposite scrolCompAnom=new ScrolledComposite(tabFolderDetector, SWT.H_SCROLL | SWT.V_SCROLL);
		
		Composite comptbtmAnalysis = new Composite(scrolCompAnom,SWT.NONE);
		tbtmAnalysis.setControl(scrolCompAnom);
		
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan=1;
		comptbtmAnalysis.setLayoutData(gridData);
		comptbtmAnalysis.setLayout(new GridLayout(2, false));
		
		selectTraceTypeAndTraces(comptbtmAnalysis);
		currentlySelectedTrace(comptbtmAnalysis);
		
		
		Class []parameterTypes= new Class[1];
		parameterTypes[0]=IDetectionModels[].class;
		Method modelObserver=Diagnosis.class.getMethod("observeSelectedModels", parameterTypes);
		
		ModelLoader mdlLoader=new ModelLoader(comptbtmAnalysis,this,modelObserver);
		

	//	results(comptbtmAnalysis);
		detailsAndFeedBack(comptbtmAnalysis);
	//	ModelDetailsRenderer modRenderer=new ModelDetailsRenderer(comptbtmAnalysis);
		
		scrolCompAnom.setContent(comptbtmAnalysis);
		 // Set the minimum size
		scrolCompAnom.setMinSize(500, 500);

	    // Expand both horizontally and vertically
		scrolCompAnom.setExpandHorizontal(true);
		scrolCompAnom.setExpandVertical(true);
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
		
		//Composite comp
	//	Composite compResult=new Composite(grpAnalysisResults, SWT.BORDER);
		//compResult.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		//compResult.setLayout(new GridLayout(2,false));
		
		CLabel lblAnalysisCurrentTrace= new CLabel(grpAnalysisResults, SWT.NONE);
		lblAnalysisCurrentTrace.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
		lblAnalysisCurrentTrace.setText("Current Trace");
		
		Text txtAnalysisCurrentTrace= new Text(grpAnalysisResults,SWT.BORDER);
		txtAnalysisCurrentTrace.setEditable(false);
		txtAnalysisCurrentTrace.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		txtAnalysisCurrentTrace.setText("kernel-session-01");
		
		CLabel lblAnalysisCurrentModel = new CLabel(grpAnalysisResults, SWT.NONE);
		lblAnalysisCurrentModel.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));//gridDataResultsLabel
		//lblAnalysisCurrentModel.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblAnalysisCurrentModel.setText("Current Model");
		
		Combo combAnalysisCurrentModel=new Combo(grpAnalysisResults,SWT.BORDER);
		combAnalysisCurrentModel.add("KSM");
		combAnalysisCurrentModel.add("Decision Tree");
		combAnalysisCurrentModel.select(0);
		combAnalysisCurrentModel.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));//gridDataResultText);
		//Text txtAnalysisCurrentModel= new Text(compResult,SWT.BORDER);
		//txtAnalysisCurrentModel.setEditable(false);
		//txtAnalysisCurrentModel.setLayoutData(gridDataResultText);
		///txtAnalysisCurrentModel.setText("KSM");
		
		CLabel lblAnalysisCurrentAnomaly = new CLabel(grpAnalysisResults, SWT.NONE);
		lblAnalysisCurrentAnomaly.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));//gridDataResultLabels);
		lblAnalysisCurrentAnomaly.setText("Anomaly");
		
		Text txtAnalysisCurrentAnomaly= new Text(grpAnalysisResults,SWT.BORDER);
		txtAnalysisCurrentAnomaly.setEditable(false);
		txtAnalysisCurrentAnomaly.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));//gridDataResultText);
		txtAnalysisCurrentAnomaly.setText("Stuxtnet-0XA");
		
		Label lblDetails=new Label(grpAnalysisResults,SWT.NONE);
		lblDetails.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
		lblDetails.setText("Details");
		
		Text txtAnalysisDetails = new Text(grpAnalysisResults, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		txtAnalysisDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,2,2));
		txtAnalysisDetails.setText("\"FS\" : 0.53\n \"MM\" : 0.12\n \"KL\" : 0.18\n \"AC\" : 0.01 \n\"IPC\" : 0\n \"NT\" : 0.01\n \"SC\" : 0\n \"UN\" : 0.18");
		txtAnalysisDetails.setMessage("Details");
				/**
		 * End result group
		 * 
		 */
	}
	/**
	 * 
	 * @param comptbtmAnalysis
	 */
	private void selectTraceTypeAndTraces(Composite comptbtmAnalysis){
		/**
		 *  Group trace selection
		 */
		Group grpTraceSelection = new Group(comptbtmAnalysis, SWT.NONE);
		grpTraceSelection.setText("Select Traces");
		
		grpTraceSelection.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		grpTraceSelection.setLayout(new GridLayout(2,false));
		
		Label lblTraceType= new Label(grpTraceSelection, SWT.BORDER);
		lblTraceType.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		lblTraceType.setText("Select Trace Type");
		
		Combo cmbTraceTypes= new Combo(grpTraceSelection,SWT.BORDER);
		cmbTraceTypes.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false,1,1));
		cmbTraceTypes.add("LTTng Kernel");
		cmbTraceTypes.add("LTTng UST");
		cmbTraceTypes.add("Regular Expression");
		cmbTraceTypes.select(0);
		
		Label lblSelTestTraces = new Label(grpTraceSelection, SWT.CHECK);
		lblSelTestTraces.setText("Select a Trace or a Directory");
		lblSelTestTraces.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true, false,2,1));
		
		Text txtTestDir=new Text(grpTraceSelection, SWT.BORDER);
		txtTestDir.setEnabled(true);
		txtTestDir.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		
		
		Button btnTestSelectTraces = new Button(grpTraceSelection, SWT.NONE);
		btnTestSelectTraces.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,true,false));
		btnTestSelectTraces.setText("Browse Traces");
		
	
		
		
		
		//table traceList
		/*Composite comptblTraceList = new Composite(grpTraceSelection,  SWT.BORDER );
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
		
//		TableItem tableItem3AnalysisAnomHistory = new TableItem(tblAnalysisTraceList, SWT.NONE);
		//tableItem3AnalysisAnomHistory.setText(new String[] {"08-12-2013: 3:45", "kernel-session-12-2013"});
		
		//int desiredHeight = tblAnalysisTraceList.getItemHeight() * 2 + tblAnalysisTraceList.getHeaderHeight();
		int itemHeight = tblAnalysisTraceList.getItemHeight();
		GridData dataGridTable = new GridData(SWT.FILL, SWT.FILL, true, false);
		dataGridTable.heightHint = 20 * itemHeight;
		
		//tblAnalysisTraceList.setLayoutData(dataGridTable);
		//tblAnalysisTraceList.setLayoutData(new GridData(400,itemHeight*3));
		//tblAnalysisTraceList.setItemCount(2);
		*/
		MultipleTracesLoader traceDispLoader= new MultipleTracesLoader();
		
		/**
		 * End group trace selection
		 */
	}
	/**
	 * 
	 * @param comptbtmAnalysis
	 */
	private void currentlySelectedTrace(Composite comptbtmAnalysis){
		 
			Group grpCurrentTrace = new Group(comptbtmAnalysis, SWT.NONE);
			grpCurrentTrace.setText("Currently Slected Trace");
			
			grpCurrentTrace.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
			grpCurrentTrace.setLayout(new GridLayout(2,false));
			
			Label lblTraceID= new Label(grpCurrentTrace, SWT.BORDER);
			lblTraceID.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
			lblTraceID.setText("Trace ID");
			
			Text txtTraceID=new Text(grpCurrentTrace,SWT.BORDER);
			txtTraceID.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
			txtTraceID.setEditable(false);
			txtTraceID.setText("Sample trace");
			
			Label lblTraceSource= new Label(grpCurrentTrace, SWT.BORDER);
			lblTraceSource.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
			lblTraceSource.setText("Source");
			
			Text txtTraceSource=new Text(grpCurrentTrace,SWT.BORDER);
			txtTraceSource.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
			txtTraceSource.setEditable(false);
			txtTraceSource.setText("TMF");
			
			Label lblTraceType= new Label(grpCurrentTrace, SWT.BORDER);
			lblTraceType.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
			lblTraceType.setText("Trace Type");
			
			Text txtTraceType=new Text(grpCurrentTrace,SWT.BORDER);
			txtTraceType.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
			txtTraceType.setEditable(false);
			txtTraceType.setText("LTTng Kernel");
	}
	
	/**
	 * 
	 * @param comptbtmAnalysis
	 */
	private void detailsAndFeedBack(Composite comptbtmAnalysis){
		//Group "Feedback: Is it anomaly?"
				Group grpAnalysisIdentify = new Group(comptbtmAnalysis, SWT.NONE);
				grpAnalysisIdentify.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,2,4));
				grpAnalysisIdentify.setText("Results and Feedback");
				grpAnalysisIdentify.setLayout(new GridLayout(3,false));
                
				/// Trace list
				Composite compTraceList=new Composite(grpAnalysisIdentify, SWT.None);
				compTraceList.setLayout(new GridLayout(1,false));
				compTraceList.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
				
				Label lblTreeTraceResult=new Label(compTraceList,SWT.NONE);
				lblTreeTraceResult.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
				lblTreeTraceResult.setText("Trace List (Select to see results)");
				
				Tree treeTraceResults=new Tree(compTraceList , SWT.BORDER |  SWT.FULL_SELECTION| SWT.V_SCROLL | SWT.H_SCROLL);
				treeTraceResults.setLinesVisible(true);
				treeTraceResults.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,1,5));
				
				TreeItem []items=new TreeItem[5];
				for (int i=0;i <items.length;i++){
					items[i]=new TreeItem(treeTraceResults,SWT.NONE);
					items[i].setText("Trace "+i);
					items[i].setData("Trace "+i);
				}
				
			    results(grpAnalysisIdentify);
				//*** End Trace list
				
				//*** Group Feedback
				Group grpFeedback= new Group(grpAnalysisIdentify,SWT.NONE);
				grpFeedback.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true,1,4));
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
			
			
				//new Label(grpAnalysisIdentify,SWT.None);// Empty Label
				//Button btnVisualize = new Button(grpFeedback, SWT.NONE);
				//btnVisualize.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true, true,2,1));
				//btnVisualize.setText("Visualize");
				

	}
	/**
	 * 
	 * @param traceBuffer
	 * @param tracePath
	 * @param traceTypeName
	 */
	public void updateOnTraceSelection(char []trace,String tracePath, String traceTypeName){
		tblAnalysisTraceList.removeAll();
		String traceName=tracePath.substring(tracePath.lastIndexOf('/')+1, tracePath.length());
		TableItem tableItemAnalysisAnomHistory = new TableItem(tblAnalysisTraceList, SWT.NONE);
		tableItemAnalysisAnomHistory.setText(new String[] {tracePath,  traceName});
	
//		Controller ctrl=new Controller();
    	//DBMS conn= new  DBMS();
   	    //ctrl.addModels(new KernelStateModeling(conn));
   	    //ctrl.testTraceUsingModels(traceBuffer,tracePath);
   	    //conn.closeConnection();
   	
	}
	/**
	 * 
	 * @return
	 */
	public void observeSelectedModels(IDetectionModels []models){
		
		TableItem []tblItem= tblAnalysisTraceList.getSelection();
		String trace=tblItem[0].getText(0);
		System.out.println(trace);
		
		for (int modlCount=0; modlCount<models.length;modlCount++){
			System.out.println(models[modlCount].getName());
		}
		//return trace;
		
	}


}

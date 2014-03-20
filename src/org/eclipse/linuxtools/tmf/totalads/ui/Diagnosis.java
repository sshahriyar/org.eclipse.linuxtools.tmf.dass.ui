package org.eclipse.linuxtools.tmf.totalads.ui;



//import org.eclipse.jface.layout.TableColumnLayout;
//import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
//import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wb.swt.SWTResourceManager;

import com.sun.media.sound.ModelConnectionBlock;

public class Diagnosis {
	//Table tblAnalysisTraceList;
	TracingTypeSelector traceTypeSelector;
	Text txtTraceID;
	Text txtTraceSource;
	Text txtTraceCount;
	Text txtTestTraceDir;
	TraceBrowser traceBrowser;
	StringBuilder tracePath=new StringBuilder();
	ModelLoader modelLoader;
	ResultsAndFeedback resultsAndFeedback;
	
	public Diagnosis(CTabFolder tabFolderDetector) throws Exception{
		
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
		
		
		//Class []parameterTypes= new Class[1];
		//parameterTypes[0]=IDetectionModels[].class;
		//Method modelObserver=Diagnosis.class.getMethod("observeSelectedModels", parameterTypes);
		
		modelLoader=new ModelLoader(comptbtmAnalysis);
		modelLoader.setTrace(tracePath);
		modelLoader.setTraceTypeSelector(traceTypeSelector);

	//	results(comptbtmAnalysis);
		resultsAndFeedback=new ResultsAndFeedback(comptbtmAnalysis);
		modelLoader.setResultsAndFeedback(resultsAndFeedback);
		
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
		lblTraceType.setText("Select a trace type");
		
	    traceTypeSelector=new TracingTypeSelector(grpTraceSelection);
		
		Label lblSelTestTraces = new Label(grpTraceSelection, SWT.CHECK);
		lblSelTestTraces.setText("Select a folder containing traces or a single trace");
		lblSelTestTraces.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true, false,2,1));
		
		txtTestTraceDir=new Text(grpTraceSelection, SWT.BORDER);
		txtTestTraceDir.setEnabled(true);
		txtTestTraceDir.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		
		traceBrowser= new TraceBrowser(grpTraceSelection,txtTestTraceDir,new GridData(SWT.LEFT,SWT.TOP,true,false));
		
		txtTestTraceDir.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				tracePath.delete(0, tracePath.length());
				tracePath.append(txtTestTraceDir.getText());
				txtTraceID.setText("Manually typed!");
				txtTraceSource.setText("Manually typed!");
				txtTraceCount.setText("Manually typed!");
			}
		});	
		
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
		//TraceBrowser traceDispLoader= new TraceBrowser();
		
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
			grpCurrentTrace.setText("Currently Selected Trace(s)");
			
			grpCurrentTrace.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
			grpCurrentTrace.setLayout(new GridLayout(2,false));
			
			Label lblTraceID= new Label(grpCurrentTrace, SWT.BORDER);
			lblTraceID.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
			lblTraceID.setText("Trace id");
			
			txtTraceID=new Text(grpCurrentTrace,SWT.BORDER);
			txtTraceID.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
			txtTraceID.setEditable(false);
		//	txtTraceID.setText("Sample trace");
			
			Label lblTraceSource= new Label(grpCurrentTrace, SWT.BORDER);
			lblTraceSource.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
			lblTraceSource.setText("Source");
			
			txtTraceSource=new Text(grpCurrentTrace,SWT.BORDER);
			txtTraceSource.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
			txtTraceSource.setEditable(false);
			//txtTraceSource.setText("TMF");
			
			Label lblTraceCount= new Label(grpCurrentTrace, SWT.BORDER);
			lblTraceCount.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
			lblTraceCount.setText("Trace Count");
			
			txtTraceCount=new Text(grpCurrentTrace,SWT.BORDER);
			txtTraceCount.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
			txtTraceCount.setEditable(false);
			//txtTraceCount.setText("1"); 
			
			traceBrowser.setTextSelectedTraceFields(txtTraceID,txtTraceSource,txtTraceCount);
			
	}
	

	/**
	 * 
	 * @param traceBuffer
	 * @param tracePath
	 * @param traceTypeName
	 */
	
	public void updateOnTraceSelection(String traceLocation, String traceTypeName){
		txtTestTraceDir.setText("");
		tracePath.delete(0, tracePath.length());
		tracePath.append(traceLocation);
		
		String traceName=traceLocation.substring(traceLocation.lastIndexOf('/')+1, traceLocation.length());
		txtTraceID.setText(traceName);
		txtTraceSource.setText("TMF:"+traceTypeName);
		txtTraceCount.setText("1");
		
		traceTypeSelector.selectTraceType(traceTypeName);
	
	}
	/*
		public void observeSelectedModels(IDetectionModels []models){
		
		//TableItem []tblItem= tblAnalysisTraceList.getSelection();
		//String trace=tblItem[0].getText(0);
	//	System.out.println(trace);
		
		for (int modlCount=0; modlCount<models.length;modlCount++){
			System.out.println(models[modlCount].getName());
		}
		//return trace;
		
	}*/


}

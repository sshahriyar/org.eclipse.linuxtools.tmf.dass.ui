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


import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.core.TMFTotalADSView;
import org.eclipse.linuxtools.tmf.totalads.readers.ctfreaders.CTFLTTngSysCallTraceReader;
import org.eclipse.linuxtools.tmf.totalads.ui.TotalADS;
import org.eclipse.linuxtools.tmf.totalads.ui.TraceBrowser;
import org.eclipse.linuxtools.tmf.totalads.ui.TracingTypeSelector;
import org.eclipse.linuxtools.tmf.totalads.ui.modeling.StatusBar;
import org.eclipse.linuxtools.tmf.totalads.ui.utilities.SWTResourceManager;
import org.eclipse.swt.SWT;
//import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
//import org.eclipse.swt.widgets.Button;
//import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
//import org.eclipse.swt.widgets.TabItem;
//import org.eclipse.swt.widgets.Table;
//import org.eclipse.swt.widgets.TableColumn;
//import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * This class creates the GUI elements/widgets for diagnosis.
 * It creates a diagnosis tab and then creates other GUI widgets by instantiating other classes and SWT widgets.
 * 
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class Diagnosis {
	//Initializes variables
	private TracingTypeSelector traceTypeSelector;
	private Text txtTMFTraceID;
	private Text txtTestTraceDir;
	private TraceBrowser traceBrowser;
	private StringBuilder tmfTracePath;
	private StringBuilder currentlySelectedTracesPath;
	private ModelLoader modelLoader;
	private ResultsAndFeedback resultsAndFeedback;
	private Button btnSelTestTraces;
	private Button btnSelTMFTrace;

	/**
	 * Constructor of the Diagnosis class
	 * @param tabFolderParent TabFolder object
	 *
	 */
	public Diagnosis(Composite tabFolderParent){
		tmfTracePath=new StringBuilder();
		currentlySelectedTracesPath=new StringBuilder();
		//Diagnosis Tab Item
		//CTabItem tbItmDiagnosis = new CTabItem(tabFolderParent, SWT.NONE);
		//tbItmDiagnosis.setText("Diagnosis");
		//Making scrollable tab item 
		
		ScrolledComposite scrolCompAnom=new ScrolledComposite(tabFolderParent, SWT.H_SCROLL | SWT.V_SCROLL);
		Composite comptbItmDiagnosis = new Composite(scrolCompAnom,SWT.NONE);
		
		//tbItmDiagnosis.setControl(scrolCompAnom);
		
		//Desiging the Layout of the GUI Items  for the Diagnosis Tab Item
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan=1;
		comptbItmDiagnosis.setLayoutData(gridData);
		comptbItmDiagnosis.setLayout(new GridLayout(1, false));
	
		///////////////////////////////////////////////////////////////////////////
		//Creating GUI widgets for selection of a trace type and a selection of the model
		///////////////////////////////////////////////////////////////////
		Composite compTraceTypeAndModel=new Composite(comptbItmDiagnosis, SWT.NONE);
		compTraceTypeAndModel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		compTraceTypeAndModel.setLayout(new GridLayout(1, false));
		
	
		selectTraceTypeAndTraces(compTraceTypeAndModel);
		// Create GUI elements for a selection of a trace
		
		//Initialize a class which loads model names from db and create appropriate GUI elements
		
		
/*		efraim
 * modelLoader=new ModelLoader(compTraceTypeAndModel);
		modelLoader.setTrace(currentlySelectedTracesPath);///////////////////////////////
		modelLoader.setTraceTypeSelector(traceTypeSelector);*/
		
		
		//////////////////////////////////////////////////////////////////////
		// Creating GUI widgets for status, results and feedback
		//////////////////////////////////////////////////////////////////
/*		efraim
 * Composite compStatusResults=new Composite(comptbItmDiagnosis, SWT.NONE);
		compStatusResults.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		compStatusResults.setLayout(new GridLayout(1, false));
		StatusBar statusBar=new StatusBar(compStatusResults);*/
		
		/*Composite compStatus=new Composite(compStatusResults, SWT.NONE);
		compStatus.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		compStatus.setLayout(new GridLayout(2, false));
		
		Label lblProgress= new Label(compStatus, SWT.NONE);
		lblProgress.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		lblProgress.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		lblProgress.setText("Connected to localhost..");
		//lblProgress.setVisible(false);
		
		
		Label lblStatus= new Label(compStatus, SWT.BORDER);
		lblStatus.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,true,false,1,1));
		lblStatus.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
		lblStatus.setText("        "); */
		
		//ProgressBar progressBar = new ProgressBar(compStatus, SWT.NONE);
		//progressBar.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,false,false,1,1));
		//progressBar.setMaximum(5);
		
		//progressBar.setSelection(5);
		
		
/*		efraim
 * resultsAndFeedback=new ResultsAndFeedback(compStatusResults);
		modelLoader.setResultsAndFeedback(resultsAndFeedback);
		modelLoader.setStautsBar(statusBar);*/
		
		//Adjust settings for scrollable Diagnosis Tab Item
		scrolCompAnom.setContent(comptbItmDiagnosis);
		 // Set the minimum size
		scrolCompAnom.setMinSize(200, 200);
	    // Expand both horizontally and vertically
		scrolCompAnom.setExpandHorizontal(true);
		scrolCompAnom.setExpandVertical(true);
	}

	
	
	/**
	 * Creates GUI widgets for a selection of traces and trace types
	 * @param compDiagnosis Composite of Diagnosis
	 */
	private void selectTraceTypeAndTraces(Composite compDiagnosis){
		/**
		 *  Group trace selection
		 */
		Group grpTraceSelection = new Group(compDiagnosis, SWT.NONE);
		grpTraceSelection.setText("Select Traces and Trace Type");
		
		grpTraceSelection.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,false));
		grpTraceSelection.setLayout(new GridLayout(1,false));
		
		// Creating widgets for the selection of a trace type
		Composite compTraceType=new Composite(grpTraceSelection, SWT.NONE);
		compTraceType.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		compTraceType.setLayout(new GridLayout(2,false));
		
		Label lblTraceType= new Label(compTraceType, SWT.NONE);
		lblTraceType.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false,1,1));
		lblTraceType.setText("Select a Trace Type    ");
		
	    traceTypeSelector=new TracingTypeSelector(compTraceType,new GridData(SWT.LEFT, SWT.TOP, false, false,1,1));
		
	    // 
		btnSelTestTraces = new Button(grpTraceSelection, SWT.RADIO);
		btnSelTestTraces.setText("Select the  Folder Containing Test Traces");
		btnSelTestTraces.setLayoutData(new GridData(SWT.FILL,SWT.TOP,false, false,2,1));
		btnSelTestTraces.setSelection(true);
		
		txtTestTraceDir=new Text(grpTraceSelection, SWT.BORDER);
		txtTestTraceDir.setEnabled(true);
		txtTestTraceDir.setLayoutData(new GridData(SWT.FILL,SWT.TOP,false,false));
		
		traceBrowser= new TraceBrowser(grpTraceSelection,txtTestTraceDir,new GridData(SWT.LEFT,SWT.TOP,false,false));
	
		
		btnSelTMFTrace = new Button(grpTraceSelection, SWT.RADIO);
		btnSelTMFTrace.setText("Select the Trace Selected in TMF (Only Kernel Trace)");
		btnSelTMFTrace.setLayoutData(new GridData(SWT.FILL,SWT.TOP,false, false,2,1));
		
		txtTMFTraceID=new Text(grpTraceSelection,SWT.BORDER);
		txtTMFTraceID.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false,1,1));
		txtTMFTraceID.setEditable(false);
		txtTMFTraceID.setEnabled(false);
		
		// Adding an event handler for the radio button btnSelTestTraces
		btnSelTestTraces.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				txtTestTraceDir.setEnabled(true);
				txtTMFTraceID.setEnabled(false);
				traceBrowser.enableBrowsing();
				txtTMFTraceID.setText("");
				txtTestTraceDir.setText("");
				currentlySelectedTracesPath.delete(0, currentlySelectedTracesPath.length());//first clearing current path
				
			}
		});
		
		// Adding an event handler for the radio button btnSelTMFTrace
		btnSelTMFTrace.addSelectionListener(new SelectionAdapter() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						
						txtTestTraceDir.setEnabled(false);
						txtTMFTraceID.setEnabled(true);
						traceBrowser.disableBrowsing();
						
						setTMFTraceToCurrentTracePath();
						
						
					}
					
		});
		
		//
		// Adding an event handler for the text box of txtTestTraceDir to update the path
		txtTestTraceDir.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				
				currentlySelectedTracesPath.delete(0, currentlySelectedTracesPath.length());//first clearing current path
				currentlySelectedTracesPath.append(txtTestTraceDir.getText());
			}
		});
		/**
		 * End group trace selection
		 */
	}
	
	
	/**
	 * This function gets called from {@link TotalADS} and {@link TMFTotalADSView} to notify which
	 * trace is selected by a user in the TMF
	 * @param traceLocation
	 * @param traceTypeName
	 */
	public void updateOnTraceSelection(String traceLocation, String traceTypeName){
		
		tmfTracePath.delete(0, tmfTracePath.length());
		tmfTracePath.append(traceLocation);
		
		
		if (btnSelTMFTrace.getSelection()){
			setTMFTraceToCurrentTracePath();
		}
		
	
	}

	/**
	 * Sets the current trace path to the path of a TMF trace and also adjusts the text boxes
	 */
	private void setTMFTraceToCurrentTracePath(){
		txtTestTraceDir.setText("");
		String traceName=tmfTracePath.substring(tmfTracePath.toString().lastIndexOf('/')+1, tmfTracePath.length());
		
		txtTMFTraceID.setText("");
		
		// clear it and copy the path, don't make a copy of the reference of the object because it is a different object
		currentlySelectedTracesPath.delete(0, currentlySelectedTracesPath.length());
		
		if (!traceName.isEmpty()){
			txtTMFTraceID.setText("Trace in TMF: "+traceName);
			currentlySelectedTracesPath.append(tmfTracePath.toString());
			// Make sure that only Lttng sytemc call trace reader is selected for TMF traces
			CTFLTTngSysCallTraceReader lttngReader=new CTFLTTngSysCallTraceReader();
			traceTypeSelector.selectTraceType(lttngReader.getName());
		}
			
	}
}

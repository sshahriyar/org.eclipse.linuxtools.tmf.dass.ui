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


import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.ctfreaders.CTFLTTngSysCallTraceReader;
import org.eclipse.linuxtools.tmf.totalads.ui.io.DirectoryBrowser;
import org.eclipse.linuxtools.tmf.totalads.ui.io.TracingTypeSelector;
import org.eclipse.linuxtools.tmf.totalads.ui.results.ResultsAndFeedback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
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
	private DirectoryBrowser traceBrowser;
	private StringBuilder tmfTracePath;
	private StringBuilder currentlySelectedTracesPath;
	private ResultsAndFeedback resultsAndFeedback;
	private Button btnSelTestTraces;
	private Button btnSelTMFTrace;
	private HashSet<String> modelsList;
	private MessageBox msgBox;
	private Button btnEvaluateModels;

	/**
	 * Constructor
	 */
	public  Diagnosis(){

		modelsList=new HashSet<>();
		msgBox= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,SWT.ICON_ERROR|SWT.OK);

	}
	/**
	 * Creates GUI widgets
	 * @param compParent Composite
	 */
	public void createControl(Composite compParent){
		tmfTracePath=new StringBuilder();
		currentlySelectedTracesPath=new StringBuilder();

		//LiveMonitor Tab Item
		///CTabItem tbItmDiagnosis = new CTabItem(tabFolderParent, SWT.NONE);
		//tbItmDiagnosis.setText("Diagnosis");

		//Diagnosis Tab Item
		//CTabItem tbItmDiagnosis = new CTabItem(tabFolderParent, SWT.NONE);
		//tbItmDiagnosis.setText("Diagnosis");

		//Making scrollable tab item

		ScrolledComposite scrolCompDiag=new ScrolledComposite(compParent, SWT.H_SCROLL | SWT.V_SCROLL);
		Composite compDiagnosis = new Composite(scrolCompDiag,SWT.NONE);

		//tbItmDiagnosis.setControl(scrolCompAnom);

		//Desiging the Layout of the GUI Items  for the LiveMonitor Tab Item
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.horizontalSpan=1;
		compDiagnosis.setLayoutData(gridData);
		compDiagnosis.setLayout(new GridLayout(1, false));

		///////////////////////////////////////////////////////////////////////////
		//Creating GUI widgets for selection of a trace type and a selection of the model
		///////////////////////////////////////////////////////////////////
		//Composite compTraceTypeAndModel=new Composite(comptbItmDiagnosis, SWT.NONE);
		//compTraceTypeAndModel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		//compTraceTypeAndModel.setLayout(new GridLayout(1, false));


		selectTraceTypeAndTraces(compDiagnosis);
		addEvaluateButton(compDiagnosis);
		// Create GUI elements for a selection of a trace





		//Adjust settings for scrollable LiveMonitor Tab Item
		scrolCompDiag.setContent(compDiagnosis);
		 // Set the minimum size
		scrolCompDiag.setMinSize(200, 200);
	    // Expand both horizontally and vertically
		scrolCompDiag.setExpandHorizontal(true);
		scrolCompDiag.setExpandVertical(true);
	}



	/**
	 * Creates GUI widgets for a selection of traces and trace types
	 * @param compDiagnosis Composite of LiveMonitor
	 */
	private void selectTraceTypeAndTraces(Composite compDiagnosis){
		/**
		 *  Group trace selection
		 */
		Group grpTraceSelection = new Group(compDiagnosis, SWT.NONE);
		grpTraceSelection.setText("Select Traces for Testing");

		grpTraceSelection.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		grpTraceSelection.setLayout(new GridLayout(4,false));

		// Creating widgets for the selection of a trace type
		Composite compTraceType=new Composite(grpTraceSelection, SWT.NONE);
		compTraceType.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,4,1));
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

		btnSelTMFTrace = new Button(grpTraceSelection, SWT.RADIO);
		btnSelTMFTrace.setText("Select the Trace Selected in TMF (Only Kernel Trace)");
		btnSelTMFTrace.setLayoutData(new GridData(SWT.FILL,SWT.TOP,false, false,2,1));

		txtTestTraceDir=new Text(grpTraceSelection, SWT.BORDER);
		txtTestTraceDir.setEnabled(true);
		txtTestTraceDir.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));

		traceBrowser= new DirectoryBrowser(grpTraceSelection,txtTestTraceDir,new GridData(SWT.RIGHT,SWT.TOP,false,false));


		txtTMFTraceID=new Text(grpTraceSelection,SWT.BORDER);
		txtTMFTraceID.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
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
	 * Adds the evalaution button
	 * @param compParent Composite
	 */
	private void addEvaluateButton(Composite compParent){
		btnEvaluateModels=new Button(compParent, SWT.NONE);
		btnEvaluateModels.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,true,false));
		btnEvaluateModels.setText("    Evaluate    ");

		/**
		 * Event handler for the evaluate button
		 */
		btnEvaluateModels.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {

				if (modelsList.size()<=0){
					msgBox.setMessage("Please, select a model first!");
					msgBox.open();
					return;
				}
				if (currentlySelectedTracesPath.length()<=0){
					msgBox.setMessage("Please, first select a trace!");
					msgBox.open();
					return;
				}

				Iterator<String> it=modelsList.iterator();

				ITraceTypeReader traceReader=traceTypeSelector.getSelectedType();
				AlgorithmFactory modFac= AlgorithmFactory.getInstance();

				IDetectionAlgorithm []algorithm =new IDetectionAlgorithm[modelsList.size()];
				String []database=new String[modelsList.size()];

				int counter=0;
				while (it.hasNext()){

					    database[counter]=it.next();
						String []modelKey=database[counter].split("_");

						/*if(modelKey==null ||  modelKey.length<2){
							msgBox.setMessage("Not a valid model created by TotalADS!");
							msgBox.open();
							return;
						}*/


						algorithm[counter]= modFac.getAlgorithmByAcronym(modelKey[1]);

						/*if(algorithm[counter]==null){
							msgBox.setMessage("This doesn't seem to be a valid model created by TotalADS!");
							msgBox.open();
							return;
						}*/
				  counter++;
				}
					resultsAndFeedback.clearData();

				btnEvaluateModels.setEnabled(false);


				BackgroundTesting testTheModel=new BackgroundTesting(currentlySelectedTracesPath.toString(), traceReader,
						    algorithm, database, btnEvaluateModels, resultsAndFeedback );
				 ExecutorService executor = Executors.newSingleThreadExecutor();
				 executor.execute(testTheModel);
				 executor.shutdown();

			}
		});
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
	 * Sets the ResultsAndFeddback object to a local variable
	 * @param resultsAndFeedback An object to display results
	 */
	public void setResultsAndFeedbackInstance(ResultsAndFeedback resultsAndFeedback){

		this.resultsAndFeedback=resultsAndFeedback;
	}
	/**
	 * This function gets called from {@link DiagnosisView} to get updated for the currently selected models
	 * @param listOfModels List of models
	 */
	public void updateonModelSelection(HashSet<String> listOfModels){
	 this.modelsList=listOfModels;
	}

	/**
	 * Sets the current trace path to the path of a TMF trace and also adjusts the text boxes
	 */
	private void setTMFTraceToCurrentTracePath(){
		txtTestTraceDir.setText(""); //$NON-NLS-1$
		System.out.println(tmfTracePath.toString());
		String traceName=tmfTracePath.substring(tmfTracePath.toString().lastIndexOf(File.separator)+1, tmfTracePath.length());

		txtTMFTraceID.setText(""); //$NON-NLS-1$

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

/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.ui.modeling;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmOutStream;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.ui.io.DirectoryBrowser;
import org.eclipse.linuxtools.tmf.totalads.ui.io.TracingTypeSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

/**
 * This class creates GUI widgets for the Modeling Tab of TotalADS
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class Modeling {

	private TracingTypeSelector traceTypeSelector;
	private Text txtTrainingTraces;
	private Text txtValidationTraces;
	private MessageBox msgBox;
	private AlgorithmOutStream  progConsole;
	private Button btnBuildModel;
	private HashSet<String> modelsList;
	
	/**
	 * Constructor
	 */
	public Modeling(){
		modelsList=new HashSet<String>();
		msgBox= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
		           ,SWT.ICON_ERROR|SWT.OK);
	}
	/**
	 * Creates Modeling widgets
	 * @param compParent
	 */
	public void createControls(Composite compParent){
		
	
		// Create a modeling tab
		//CTabItem tbItmModeling = new CTabItem(tabFolderParent, SWT.NONE);
		//tbItmModeling.setText("Modeling");
		
		
		
		//Make it scrollable 
		ScrolledComposite scrolCompModel=new ScrolledComposite(compParent, SWT.H_SCROLL | SWT.V_SCROLL);
		Composite comptbItmModeling = new Composite(scrolCompModel, SWT.NONE);
		comptbItmModeling.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		comptbItmModeling.setLayout(new GridLayout(1,true));
		
		selectTracesAndTraceTypes(comptbItmModeling);
				
		buildModel(comptbItmModeling);
	    
	    scrolCompModel.setContent(comptbItmModeling);
		 // Set the minimum size
		scrolCompModel.setMinSize(200, 200);
	    // Expand both horizontally and vertically
		scrolCompModel.setExpandHorizontal(true);
		scrolCompModel.setExpandVertical(true);
		
		
	
	}
	
	/**
	 * 	
	 * Creates GUI widgets for selection of training traces by a user
	 * @param comptbItmModeling Modeling composite
	 * 
	 */
	private void selectTracesAndTraceTypes(Composite comptbItmModeling){
		/**
		 * Group modeling type and traces
		 */
		Group grpTracesModeling=new Group(comptbItmModeling, SWT.NONE);
		grpTracesModeling.setText("Select Traces for Training and Validation");
		grpTracesModeling.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,2));
		grpTracesModeling.setLayout(new GridLayout(4,false));
	
		// creating widgets for the selection of trace type
		Composite compTraceType=new Composite(grpTracesModeling, SWT.NONE);
		compTraceType.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,4,1));
		compTraceType.setLayout(new GridLayout(2,false));
		
		Label lblTraceType= new Label(compTraceType, SWT.NONE);
		lblTraceType.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false,1,1));
	    lblTraceType.setText("Select a Trace Type       ");
		
		traceTypeSelector=new TracingTypeSelector(compTraceType,new GridData(SWT.LEFT, SWT.TOP, false, false,1,1));
		
		/// Training Traces selection widgets
		Label lblTrainingTraces= new Label(grpTracesModeling, SWT.NONE);
		lblTrainingTraces.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false,2,1));
		lblTrainingTraces.setText("Select the Folder Containing Training Traces");
		
		txtTrainingTraces = new Text(grpTracesModeling, SWT.BORDER);
		txtTrainingTraces.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		// instantiate an object of trace browser
		DirectoryBrowser traceBrowserTraining=new DirectoryBrowser(grpTracesModeling,txtTrainingTraces,
							new GridData(SWT.LEFT,SWT.TOP,false,false));
		
		// Widgets for Validation traces
		validation(grpTracesModeling);
		
		
				
	}
	
	
	/**
	 * Creates GUI widgets for the selection of validation traces
	 * @param compParent
	 */
	public void validation(Composite compParent){
		
			
		Label lblValidationTraces= new Label(compParent, SWT.NONE);
		lblValidationTraces.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false,2,1));
		lblValidationTraces.setText("Select the Folder Containing Validation Traces");
		
		txtValidationTraces = new Text(compParent, SWT.BORDER);
		//txtValidationTraces.setText("");
		txtValidationTraces.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		DirectoryBrowser traceBrowser= new DirectoryBrowser(compParent, txtValidationTraces, 
						new GridData(SWT.LEFT,SWT.TOP,false,false));

	
	}
	
	/**
	 * Method to handle model building button
	 * @param compParent Composite
	 */
	
	public void buildModel(Composite compParent){
		Composite compSettingAndEvaluation=new Composite(compParent,SWT.NONE);
	    compSettingAndEvaluation.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,4,2));
	    compSettingAndEvaluation.setLayout(new GridLayout(1, false));
		btnBuildModel=new Button(compSettingAndEvaluation,SWT.NONE);
		btnBuildModel.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,true,false,1,1));
		btnBuildModel.setText("Start Modeling");
		
		//
		//Event handler for mouse up event
		//
		btnBuildModel.addMouseListener(new MouseAdapter() {
		
		@Override
		public void mouseUp(MouseEvent e) {
				
					
						
						
						String trainingTraces=txtTrainingTraces.getText().trim();
						String validationTraces=txtValidationTraces.getText().trim();
						
						if (modelsList.size()<=0){
							msgBox.setMessage("Please, select a model first!");
							msgBox.open();
							return;
						} 
						else if (trainingTraces.isEmpty()){
						
							msgBox.setMessage("Please, select training traces.");
							msgBox.open();
							btnBuildModel.setEnabled(true);
							return;
						}
						else if (validationTraces.isEmpty()){
							
							msgBox.setMessage("Please, select validation traces.");
							msgBox.open();
							btnBuildModel.setEnabled(true);
							return;
						}
						btnBuildModel.setEnabled(false);
						   // get the database name from the text box or combo
						
						
						ITraceTypeReader traceReader=traceTypeSelector.getSelectedType();	 
						BackgroundModeling modeling=new BackgroundModeling(trainingTraces, 
												validationTraces,traceReader,modelsList,	btnBuildModel);
						ExecutorService executor = Executors.newSingleThreadExecutor();
						executor.execute(modeling);
						executor.shutdown();
				
				
			}
		 });
	}
	
	/**
	 * This function gets called from {@link ModelingView} to get updated for the currently selected models
	 * @param modelsList
	 */
	public void updateonModelSelection(HashSet<String> modelsList){
	 this.modelsList=modelsList;
	}

		
}


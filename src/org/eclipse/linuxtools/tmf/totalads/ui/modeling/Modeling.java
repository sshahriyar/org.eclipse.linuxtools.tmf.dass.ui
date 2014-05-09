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

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmOutStream;
import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.dbms.IDBMSObserver;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.ui.io.DirectoryBrowser;
import org.eclipse.linuxtools.tmf.totalads.ui.io.TracingTypeSelector;
import org.eclipse.linuxtools.tmf.totalads.ui.live.BackgroundLiveMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
//import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
//import org.eclipse.swt.events.SelectionAdapter;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;

/**
 * This class creates GUI widgets for the Modeling Tab of TotalADS
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class Modeling {
	private AlgorithmModelSelector algorithmSelector;
	private TracingTypeSelector traceTypeSelector;
	private Text txtTrainingTraces;
	private Text txtValidationTraces;
	private MessageBox msgBox;
	private AlgorithmOutStream  progConsole;
	private Button btnBuildModel;
	
	
	/**
	 * Constructor
	 * @param tabFolderParent Parent tabFolder object  
	 */
	public Modeling(Composite tabFolderParent){
		
	
		// Create a modeling tab
		//CTabItem tbItmModeling = new CTabItem(tabFolderParent, SWT.NONE);
		//tbItmModeling.setText("Modeling");
		
		GridLayout gridTwoColumns=new GridLayout(2,true);
		
		//Make it scrollable 
		ScrolledComposite scrolCompModel=new ScrolledComposite(tabFolderParent, SWT.H_SCROLL | SWT.V_SCROLL);
		Composite comptbItmModeling = new Composite(scrolCompModel, SWT.NONE);
		comptbItmModeling.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		comptbItmModeling.setLayout(gridTwoColumns);
		
		//tbItmModeling.setControl(scrolCompModel);
		
		selectTracesAndTraceTypes(comptbItmModeling);
		
		Group grpAlgorithmModel=new Group(comptbItmModeling, SWT.NONE);
		grpAlgorithmModel.setText("Select Algorithms and DataModels");
		grpAlgorithmModel.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,2));
		grpAlgorithmModel.setLayout(new GridLayout(2,false));//gridTwoColumns);
		
		algorithmSelector=new AlgorithmModelSelector(grpAlgorithmModel);
		
		Composite compSettingAndEvaluation=new Composite(comptbItmModeling,SWT.NONE);
	    compSettingAndEvaluation.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,2));
	    compSettingAndEvaluation.setLayout(new GridLayout(6, false));
	    
		//createAnEmptyModel(compSettingAndEvaluation);
	    adjustSettings(compSettingAndEvaluation);
		buildModel(compSettingAndEvaluation);
		
		
	    scrolCompModel.setContent(comptbItmModeling);
		 // Set the minimum size
		scrolCompModel.setMinSize(600, 600);
	    // Expand both horizontally and vertically
		scrolCompModel.setExpandHorizontal(true);
		scrolCompModel.setExpandVertical(true);
		
		msgBox= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
			           ,SWT.ICON_ERROR|SWT.OK);
	
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
		grpTracesModeling.setText("Select Traces and Trace Types");
		grpTracesModeling.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,2));
		grpTracesModeling.setLayout(new GridLayout(2,false));//gridTwoColumns);
	
		// creating widgets for the selection of trace type
		Composite compTraceType=new Composite(grpTracesModeling, SWT.NONE);
		compTraceType.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
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
		
		/////////////////////////////////////////////////////////
		/////Cross validation will be available in the next version
		///////////////////////////////////////////////////////////
		/* 
		 Group grpValidation=new Group(comptbItmModeling, SWT.NONE);
		grpValidation.setText("Select Validation Traces");
		grpValidation.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,2));
		grpValidation.setLayout(new GridLayout(1,false));//gridTwoColumns);
				
		
		 Button radioBtnCrossVal=new Button(grpValidation, SWT.RADIO);
		radioBtnCrossVal.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,2,1));
		radioBtnCrossVal.setText("Cross Validation");
		
		
		Label lblCrossVal=new Label(grpValidation, SWT.BORDER);
		lblCrossVal.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, false,1,1));
		lblCrossVal.setText("Specify Folds:");
		Text txtCrossVal = new Text(grpValidation, SWT.BORDER);
		txtCrossVal.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		txtCrossVal.setText("3");
		
		Button radioBtnVal=new Button(grpValidation, SWT.RADIO);
		radioBtnVal.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,2,1));
		radioBtnVal.setText("Validation");
		radioBtnVal.setSelection(true);*/
		
		Label lblValidationTraces= new Label(compParent, SWT.NONE);
		lblValidationTraces.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false,2,1));
		lblValidationTraces.setText("Select the Folder Containing Validation Traces");
		
		txtValidationTraces = new Text(compParent, SWT.BORDER);
		//txtValidationTraces.setText("");
		txtValidationTraces.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		DirectoryBrowser traceBrowser= new DirectoryBrowser(compParent, txtValidationTraces, 
						new GridData(SWT.LEFT,SWT.TOP,false,false));

		//traceBrowser.setTextBox(txtValidationTraces);
		
	
		/**
		 * End group modeling type and traces
		 */
	}
	
	/**
	    * Creates GUI widgets for the creation of an empty model
	    * @param comptbItmModeling Composite of Modeling
	    */
		private void createAnEmptyModel(Composite comptbItmModeling){
			
			/*Button btnEmpty=new Button(comptbItmModeling,SWT.NONE);
			btnEmpty.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,true,false,1,1));
			btnEmpty.setText("Create an Empty DataModel");
			
			//Event handler for Empty model button
			btnEmpty.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseUp(MouseEvent e) {
					try {
						algorithmSelector.createAnEmptyModel();
						MessageBox msgBox= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
						           ,SWT.ICON_INFORMATION|SWT.OK);
						msgBox.setMessage("An empty model created");
						msgBox.open();
						
					} catch (TotalADSDBMSException ex) {
						msgBox.setMessage(ex.getMessage());
						msgBox.open();
					} catch (TotalADSGeneralException ex) {
						msgBox.setMessage(ex.getMessage());
						msgBox.open();
					} catch (Exception ex) {
							msgBox.setMessage(ex.getMessage());
							msgBox.open();
							Logger.getLogger(Modeling.class.getName()).log(Level.SEVERE,ex.getMessage(), ex);
							
					}
					
				}
			 });*/
		}
	
   /**
    * Creates GUI widgets for adjustment of settings of an algorithm
    * Shows setting dialog for a selected algorithm
    * @param comptbItmModeling Composite of Modeling
    */
	private void adjustSettings(Composite comptbItmModeling){
		
		Button btnSettings=new Button(comptbItmModeling,SWT.NONE);
		btnSettings.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,true,false,1,1));
		btnSettings.setText("Adjust SettingsForm");
		
		
	}

	
	/**
	 * Method to handle model building button
	 * @param comptbItmModeling Modeling composite
	 */
	
	public void buildModel(Composite comptbItmModeling){
		
		btnBuildModel=new Button(comptbItmModeling,SWT.NONE);
		btnBuildModel.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,false,false,1,1));
		btnBuildModel.setText("Start building the model");
		
		//
		//Event handler for mouse up event
		//
		btnBuildModel.addMouseListener(new MouseAdapter() {
		
		@Override
		public void mouseUp(MouseEvent e) {
				
						String selectedDB;
						Boolean isNewDB;
						btnBuildModel.setEnabled(false);
						String trainingTraces=txtTrainingTraces.getText().trim();
						String validationTraces=txtValidationTraces.getText().trim();
						
						if (trainingTraces.isEmpty()){
						
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
						
						   // get the database name from the text box or combo
						
						
						ITraceTypeReader traceReader=traceTypeSelector.getSelectedType();	 
						BackgroundModeling modeling=new BackgroundModeling(trainingTraces, 
												validationTraces,traceReader,algorithmSelector,	btnBuildModel);
						ExecutorService executor = Executors.newSingleThreadExecutor();
						executor.execute(modeling);
						executor.shutdown();
				
				
			}
		 });
	}

		
}


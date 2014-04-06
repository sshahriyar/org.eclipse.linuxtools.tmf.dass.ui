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

import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.dbms.IObserver;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.ui.TraceBrowser;
import org.eclipse.linuxtools.tmf.totalads.ui.TracingTypeSelector;
import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.ProgressConsole;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * This class creates GUI widgets for the Modeling Tab of TotalADS
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class Modeling {
	private AlgorithmSelector modelSelector;
	private TracingTypeSelector traceTypeSelector;
	private Text txtTrainingTraces;
	private Text txtValidationTraces;
	private MessageBox msgBox;
	private Combo cmbDBNames;
	private Text txtNewDBName;
	private ProgressConsole  progConsole;
	private Button btnBuildModel;
	/**
	 * Constructor
	 * @param tabFolderParent Parent tabFolder object  
	 */
	public Modeling(CTabFolder tabFolderParent){
		// Create a modeling tab
		CTabItem tbItmModeling = new CTabItem(tabFolderParent, SWT.NONE);
		tbItmModeling.setText("Modeling");
		
		GridLayout gridTwoColumns=new GridLayout(4,false);
		
		//Make it scrollable 
		ScrolledComposite scrolCompModel=new ScrolledComposite(tabFolderParent, SWT.H_SCROLL | SWT.V_SCROLL);
		Composite comptbtmModeling = new Composite(scrolCompModel, SWT.NONE);
		comptbtmModeling.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		comptbtmModeling.setLayout(gridTwoColumns);
		
		tbItmModeling.setControl(scrolCompModel);
		
		selectTrainingTraces(comptbtmModeling);
		selectTraceTypeAndDatabase(comptbtmModeling);
		
		modelSelector=new AlgorithmSelector(comptbtmModeling);
		
		validation(comptbtmModeling);
	    
		adjustSettings(comptbtmModeling);
		buildModel(comptbtmModeling);
		//Initialize progress console
	    progConsole=new ProgressConsole(comptbtmModeling);
		
	    scrolCompModel.setContent(comptbtmModeling);
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
	private void selectTrainingTraces(Composite comptbItmModeling){
		/**
		 * Group modeling type and traces
		 */
		Group grpTracesModeling=new Group(comptbItmModeling, SWT.NONE);
		grpTracesModeling.setText("Select Training Traces");
		grpTracesModeling.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
		grpTracesModeling.setLayout(new GridLayout(1,false));//gridTwoColumns);
		

		txtTrainingTraces = new Text(grpTracesModeling, SWT.BORDER);
		txtTrainingTraces.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		// instantiate an object of trace browser
		new TraceBrowser(grpTracesModeling,txtTrainingTraces,new GridData(SWT.LEFT,SWT.TOP,false,false));
				
	}
	
	
	
	
	/**
	 * 
	 * Creates GUI elements to select a trace type and a database
	 * @param comptbItmModeling Modeling composite
	 * 
	 */
	//Text txtModelingTraces;
	public void selectTraceTypeAndDatabase(Composite comptbItmModeling){
		/**
		 * Group modeling type and traces
		 */
		Group grpTraceTypesAndDB=new Group(comptbItmModeling, SWT.NONE);
		grpTraceTypesAndDB.setText("Trace Type and DB");
		grpTraceTypesAndDB.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,1));
		grpTraceTypesAndDB.setLayout(new GridLayout(3,false));//gridTwoColumns);
				
		Label lblTraceType= new Label(grpTraceTypesAndDB, SWT.BORDER);
		lblTraceType.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false,1,1));
		lblTraceType.setText("Select the Trace Type");
		
		traceTypeSelector=new TracingTypeSelector(grpTraceTypesAndDB);
	
		Label emptyLabel=new Label(grpTraceTypesAndDB, SWT.BORDER);// An empty label for a third cell
		emptyLabel.setVisible(false);
		
		Label lblDB=new Label(grpTraceTypesAndDB, SWT.BORDER);
		lblDB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false,1,1));
		lblDB.setText("Select or Enter a DB Name");
		
		cmbDBNames= new Combo(grpTraceTypesAndDB,SWT.READ_ONLY | SWT.V_SCROLL);
		cmbDBNames.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false,1,1));
		
		txtNewDBName = new Text(grpTraceTypesAndDB, SWT.BORDER);
		txtNewDBName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		//txtNewDBName.setText("Enter");
		txtNewDBName.setTextLimit(7);
		//txtNewDBName.setEnabled(false);
		populateComboWithDatabaseList(null);
		//
		//Event handler for db name combo box
		//
		cmbDBNames.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				//if (e.text.equalsIgnoreCase("Enter New  Name")){
				if ( cmbDBNames.getSelectionIndex()==0){
					txtNewDBName.setText("");
					txtNewDBName.setEnabled(true);
					
				}
				
				else {
					
					txtNewDBName.setText("Enter");
					txtNewDBName.setEnabled(false);
					//selectedDB=cmbDBNames.getItem(cmbDBNames.getSelectionIndex());
					
				}
			}
		});
		
	 // traceTypeSelector.addObserver(new Observer());
		
	  // Add an observer to DBMS connection to automatically update 
	  //the list of databases when new ones are created and old ones are deleted
	   Configuration.connection.addObserver( new IObserver() {
			@Override
			public void update() {
				Display.getDefault().asyncExec(new Runnable(){
					@Override
					public void run(){
						populateComboWithDatabaseList(null);
					}
				});
		
			}
		 });
		

		/**
		 * End group modeling type and traces
		 */
	}
	
	
	/**
	 *
	 * Populates the combo box with models (database) list
	 * @param filter Filter as a string which needs to be excluded from the list or null for no exclusion
	 *
	 */
	private void populateComboWithDatabaseList(String filter){
		
				cmbDBNames.removeAll(); // First clear it
				cmbDBNames.add("Enter a new database name");

				//Populate combo box
				if (Configuration.connection.isConnected()){
					
					List<String> modelsList=Configuration.connection.getDatabaseList();
					for (int j=0; j<modelsList.size();j++)
					    if (filter ==null)
						  cmbDBNames.add(modelsList.get(j));
					    else if (modelsList.get(j).contains(filter)){
					    	cmbDBNames.add(modelsList.get(j));
					    }
				
					// Select the first item in the combo box		
					cmbDBNames.select(0);
					txtNewDBName.setEnabled(true);
				}
	}
	
	/**
	 * Creates GUI widgets for the selection of validation traces
	 * @param comptbItmModeling
	 */
	public void validation(Composite comptbItmModeling){
		/**
		 * Group modeling type and traces
		 */
		Group grpValidation=new Group(comptbItmModeling, SWT.NONE);
		grpValidation.setText("Validation");
		grpValidation.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,2));
		grpValidation.setLayout(new GridLayout(2,false));//gridTwoColumns);
				
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
		radioBtnVal.setSelection(true);
		
		
		TraceBrowser traceBrowser= new TraceBrowser(grpValidation,new GridData(SWT.RIGHT,SWT.TOP,false,false));
		//Button btnValidationBrowse =new Button(grpValidation, SWT.NONE);
		//btnValidationBrowse.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,false,false));
		//btnValidationBrowse.setText("Browse Directory");
		
		txtValidationTraces = new Text(grpValidation, SWT.BORDER);
		//txtValidationTraces.setText("");
		txtValidationTraces.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		traceBrowser.setTextBox(txtValidationTraces);
		
	
		/**
		 * End group modeling type and traces
		 */
	}
   /**
    * Creates GUI widgets for adjustment of settings of an algorithm
    * Shows setting dialog for a selected algorithm
    * @param comptbItmModeling Composite of Modeling
    */
	private void adjustSettings(Composite comptbItmModeling){
		
		Button btnSettings=new Button(comptbItmModeling,SWT.NONE);
		btnSettings.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,true,false,1,1));
		btnSettings.setText("Adjust Settings");
		
		//Event handler for Settings button
		btnSettings.addMouseListener(new MouseAdapter() {
		
		@Override
		public void mouseUp(MouseEvent e) {
				try {
					modelSelector.showSettingsDialog();
				} catch (TotalADSUIException ex) {
					msgBox.setMessage(ex.getMessage());
					msgBox.open();
				}
				
			}
		 });
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
						 else if (txtNewDBName.getEnabled()==false){
								selectedDB=cmbDBNames.getItem(cmbDBNames.getSelectionIndex());
								isNewDB=false;
						 }
						 else if (txtNewDBName.getText().isEmpty()){
								msgBox.setMessage("Please, enter a database name.");
								msgBox.open();
								btnBuildModel.setEnabled(true);
								return;
						} else{
								 selectedDB=txtNewDBName.getText();
								 isNewDB=true;
						}
						
						ITraceTypeReader traceReader=traceTypeSelector.getSelectedType();	 
						BackgroundModeling modeling=new BackgroundModeling(trainingTraces, selectedDB, isNewDB, 
												validationTraces,traceReader,modelSelector,progConsole,
												btnBuildModel);
						modeling.start();
				
				
			}
		 });
	}

		
}


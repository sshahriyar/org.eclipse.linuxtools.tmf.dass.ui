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


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmTypes;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
import org.eclipse.linuxtools.tmf.totalads.dbms.IObserver;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
//<<<<<<< HEAD
//=======
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
import org.eclipse.linuxtools.tmf.totalads.readers.ctfreaders.CTFLTTngSysCallTraceReader;
import org.eclipse.linuxtools.tmf.totalads.readers.textreaders.TextLineTraceReader;
//>>>>>>> 82b1feda7ccaaf9f33cc8762456a2d6fa8156877
import org.eclipse.linuxtools.tmf.totalads.ui.Settings;
import org.eclipse.linuxtools.tmf.totalads.ui.io.ProgressConsole;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;


/**
 * This class loads algorithms from the {@link AlgorithmFactory} and creates appropriate GUI elements
 * @author <p> Syed Shariyar Murtaza jsutsshary@hotmail.com</p>
 *
 */

public class AlgorithmModelSelector {
	private Group grpAnalysisModelSelection;
	private Button btnAnalysisEvaluateModels;
	private Tree treeAnalysisModels;
	
	private TreeItem currentlySelectedTreeItem;
	private IDetectionAlgorithm currentlySelectedAlgorithm;
	private Settings settingsDialog;
	private String []algorithmOptions;
	private Combo cmbDBNames;
	private Text txtNewDBName;
	private Button btnEnterDB;
	private Button btnSelectDB;
	private Boolean isNewOrOldModel; 
	private String modelNameRetreival; 
	private String modelNameVerificationException;
	/**
	 * Constructor
	 * @param compParent An object of type composite
	 */
	public AlgorithmModelSelector(Composite compParent){
		
	
		/**
		 *  Group model selection
		 */
		
		grpAnalysisModelSelection=new Group(compParent,SWT.NONE);	
		grpAnalysisModelSelection.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,3));
		grpAnalysisModelSelection.setLayout(new GridLayout(1,false));
		//grpAnalysisModelSelection.setText("Select an Algorithm");
		Label lblAlgorithmSelector= new Label(grpAnalysisModelSelection, SWT.NONE);
		lblAlgorithmSelector.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false,1,1));
	    // adjusting font size which is too small
		lblAlgorithmSelector.setText("Select an Algorithm");
		
		treeAnalysisModels = new Tree(grpAnalysisModelSelection, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION| SWT.V_SCROLL | SWT.H_SCROLL);
		
		treeAnalysisModels.setLinesVisible(true);
		treeAnalysisModels.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,6));
		
		TreeItem treeItmAnom = new TreeItem(treeAnalysisModels, SWT.NONE);
		treeItmAnom.setText("Anomaly Detection");
		
		populateTreeItems(treeItmAnom,AlgorithmTypes.ANOMALY);
		
		treeItmAnom.setExpanded(true);

		
		/*//This code will be available in the next version. It has been commented out in this version
		TreeItem treeItmClassf = new TreeItem(treeAnalysisModels, SWT.NONE);
		treeItmClassf.setText("Classification");
		populateTreeItems(treeItmClassf,AlgorithmFactory.ModelTypes.Classification);
		treeItmClassf.setExpanded(true);
		*/
		// Event handler for the tree
		treeAnalysisModels.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectionEventHandlerForTree(e);	
			}
		});
		
		
		identifyModelInDatabase(compParent);
	
		/**
		 * End group model selection 
		*/
	}
	
	
	/**
	 * Handler for the tree selection
	 * @param e SelectionEvent from the tree
	 */
	private void selectionEventHandlerForTree (SelectionEvent e){
	
		
		TreeItem item=(TreeItem)e.item;
	      
		if (item.getParentItem()==null)
				item.setChecked(false);
		else{
				if (currentlySelectedTreeItem!=null)
						 currentlySelectedTreeItem.setChecked(false);
				 item.setChecked(true);
				 currentlySelectedTreeItem=item;
				 currentlySelectedAlgorithm= ((IDetectionAlgorithm)currentlySelectedTreeItem.getData());
		}
		
		
		populateComboWithDatabaseList();
				 
				
	}
	
	
	/**
	 * Populates the tree with the list of models from the model factory
	 */
	private void populateTreeItems(TreeItem treeItem,AlgorithmTypes algorithmTypes){
			///////data
			AlgorithmFactory  modFac=AlgorithmFactory.getInstance();
			
		    // populating anomaly detection models		
		    
			IDetectionAlgorithm []models  = modFac.getAlgorithms(algorithmTypes);
			
			if (models!=null){
				TreeItem []items=new TreeItem[models.length];
				for (int i=0;i <items.length;i++){
					items[i]=new TreeItem(treeItem,SWT.NONE);
					items[i].setText(models[i].getName());
					items[i].setData(models[i]);
					
				}
				items[0].setChecked(true);
				currentlySelectedTreeItem=items[0];
				currentlySelectedAlgorithm= ((IDetectionAlgorithm)currentlySelectedTreeItem.getData());
			}
			   
		    
	}
	
	/**
	 * Checks  selection of a model in the tree
	 */
	private boolean checkItemSelection(){
		
		if (currentlySelectedTreeItem ==null )
				return false;
		else
				return true;
		
	}
	/** 
	 * 
	 * Checks if database exists or not
	 * @return True if database exists, else false
	 */
	private boolean checkDBExistence(String database){
		
			return Configuration.connection.datbaseExists(database);
			
	}
	/**
	 * Shows the settings dialog box
	 * @throws TotalADSUIException
	 */
	public void showSettingsDialog() throws TotalADSUIException{
		if (!checkItemSelection())
			throw new TotalADSUIException("Please, first select an algorithm!");
		// Getting training options
		MessageBox msgBox= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
		           ,SWT.ICON_ERROR|SWT.OK);
		//Get the model/database name
		String exception=retreiveModelName();
		if (!exception.isEmpty()){
			msgBox.setMessage(exception);
			msgBox.open();
			return;
		}
		// we only need to know if it is a new model or an existing model with its name
		// so get the information from the class variables
		Boolean isCreateDB=isNewOrOldModel;
		String database=modelNameRetreival;
				
		
		String []options=currentlySelectedAlgorithm.getTrainingOptions(Configuration.connection, database,isCreateDB);
		if (options!=null){
			try{
				if (settingsDialog==null)
					settingsDialog= new Settings(options);
			
				settingsDialog.showForm();
				algorithmOptions=settingsDialog.getOptions();

			}catch (TotalADSUIException ex){
				msgBox.setMessage(ex.getMessage());
				msgBox.open();
			} catch (Exception ex){
				String msg="Problems in settings from "+currentlySelectedAlgorithm.getName()+"\n";
				if (ex.getMessage()!=null)
					msg+=ex.getMessage();
				msgBox.setMessage(msg);
				msgBox.open();
			}
			finally{
				settingsDialog=null;
			}
		}else{
			msgBox.setMessage("Not implemented yet");
			msgBox.open();
		}
		
	}
	
	/**
	 * 
	 * Creates GUI widgets to select a database name of a model or type a new one
	 * @param compParent Modeling composite
	 * 
	 */
	public void identifyModelInDatabase(Composite compParent){
		/**
		 * Group modeling type and traces
		 */
		Group grpIdentifyDB=new Group(compParent, SWT.NONE);
		//grpIdentifyDB.setText("DataModels");
		grpIdentifyDB.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		grpIdentifyDB.setLayout(new GridLayout(1,false));//gridTwoColumns);
				
		
	
		//Label emptyLabel=new Label(grpTraceTypesAndDB, SWT.BORDER);// An empty label for a third cell

		//Label lblDB=new Label(compParent, SWT.BORDER);
		btnEnterDB=new Button(grpIdentifyDB, SWT.RADIO);
		btnEnterDB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false,1,1));
		btnEnterDB.setText("Enter a New DataModel Name");
		btnEnterDB.setSelection(true);
		
		txtNewDBName = new Text(grpIdentifyDB, SWT.BORDER);
		txtNewDBName.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,1,1));
		//txtNewDBName.setText("Enter");
		txtNewDBName.setTextLimit(8);
		//txtNewDBName.setEnabled(false);
		
		btnSelectDB=new Button(grpIdentifyDB, SWT.RADIO);
		btnSelectDB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false,1,1));
		btnSelectDB.setText("Select an Existing DataModel Name");
		
		
		cmbDBNames= new Combo(grpIdentifyDB,SWT.READ_ONLY | SWT.V_SCROLL);
		cmbDBNames.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false,1,1));
		cmbDBNames.setEnabled(false);
		// Adds an event listener for a button
		btnEnterDB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
					
				txtNewDBName.setEnabled(true);
				cmbDBNames.setEnabled(false);
				cmbDBNames.removeAll();
				algorithmOptions=null;
			}
			
		});
		// Adds an event listener for a button
		btnSelectDB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				txtNewDBName.setText("");
				txtNewDBName.setEnabled(false);
				populateComboWithDatabaseList();
				
				
			}
			
		});
		// adds an event listener to combo selection
		cmbDBNames.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				algorithmOptions=null;
				
			}
			
		});
		
	   // Add an observer to DBMS connection to automatically update 
	  //the list of databases when new ones are created and old ones are deleted
	   Configuration.connection.addObserver( new IObserver() {
			@Override
			public void update() {
				Display.getDefault().asyncExec(new Runnable(){
					@Override
					public void run(){
						populateComboWithDatabaseList();
						
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
	private void populateComboWithDatabaseList(){
		algorithmOptions=null;
		if (checkItemSelection() && btnSelectDB.getSelection()){
			String filter =currentlySelectedAlgorithm.getAcronym();	
			// First clear it
				//Populate combo box
				if (Configuration.connection.isConnected()){
					cmbDBNames.setEnabled(true);	
					cmbDBNames.removeAll(); 
					List<String> modelsList;
					try{
						modelsList=Configuration.connection.getDatabaseList();
					} catch (Exception ex){// in case of no db connection or no list
					 	cmbDBNames.add("No connection/database list");
					    cmbDBNames.select(0);
						cmbDBNames.setEnabled(false);
						return;
					}
					
						
					for (int j=0; j<modelsList.size();j++)
					   	if (modelsList.get(j).contains(filter))
								cmbDBNames.add(modelsList.get(j));
					  // Select the first item in the combo box		
					if (cmbDBNames.getItemCount()<=0){
						cmbDBNames.add("No models created yet");
						cmbDBNames.select(0);
						cmbDBNames.setEnabled(false);
					}else
						cmbDBNames.select(0);
								
			   } else{
				   	cmbDBNames.add("No database connection");
				    cmbDBNames.select(0);
					cmbDBNames.setEnabled(false);
			   }
				
		}else if (btnSelectDB.getSelection()){
		   	cmbDBNames.add("Select an algorithm");
		    cmbDBNames.select(0);
			cmbDBNames.setEnabled(false);
	   }
		
			
		
		
	}

	/**
	 * Retrieves the model name and also retrieves if the model is new or old
	 * @throws TotalADSUIException
	 * @return exception message if any otherwise empty
	 */
	private String retreiveModelName() throws TotalADSUIException{
		
		Display.getDefault().syncExec(new Runnable() { // Due to a thread execution using BackgrounModeling class, we have execute this
													//code under a min thread using syncExec
			
			@Override
			public void run() {
					String selectedDB="";
					modelNameVerificationException="";
					 if (btnSelectDB.getSelection() && cmbDBNames.isEnabled()){
							selectedDB=cmbDBNames.getItem(cmbDBNames.getSelectionIndex());
							isNewOrOldModel=false;
					 }
					 else if (btnEnterDB.getSelection()){ 
						 if (txtNewDBName.getText().isEmpty()){
							 modelNameVerificationException="Please, enter a new model name";
							 return;
						 }
						 else{
							 if (txtNewDBName.getText().contains("_")){
								 modelNameVerificationException="DataModel name cannot contain underscore \"_\"";
								 return;
							 }
							 selectedDB=txtNewDBName.getText();
							 isNewOrOldModel=true;
						}
				      }
					 
					 if (selectedDB.isEmpty()){
						 modelNameVerificationException="Please, enter or select a model name";
						 return;
					 }
					
					 modelNameRetreival=selectedDB;
				
			}
		});
		return modelNameVerificationException;
		
	}
	///////////////////////////////////////////////////////////////////////////////////////
	////////// Training and Validation
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * This function trains a model
	 * @param trainDirectory Train Directory
	 * @throws TotalADSUIException
	 * @throws TotalADSDBMSException 
	 * @throws TotalADSReaderException 
	 * 
	 */
	public void trainAndValidateModels(String trainDirectory, String validationDirectory, ITraceTypeReader traceReader,  ProgressConsole console ) throws TotalADSUIException, TotalADSDBMSException, TotalADSReaderException {
		String database;
		// First, verify selections
		if (!checkItemSelection())
			throw new TotalADSUIException("Please, first select an algorithm!");
       
		Boolean isLastTrace=false;
		IDetectionAlgorithm algorithm=currentlySelectedAlgorithm.createInstance();
		
		//Get the model/database name
		String msg=retreiveModelName();
		if (!msg.isEmpty())
			 throw new TotalADSUIException(msg);
		// if there is no exception then get the db name and new or old db using the class variables	
		Boolean isCreateDB=isNewOrOldModel;
		database=modelNameRetreival;
		
		////////////////////
		///File verifications
		///////////////////
		File fileList[]=getDirectoryHandler(trainDirectory,traceReader);// Get a file handler
				
		try{ //Check for valid trace type reader and training traces before creating a database
			traceReader.getTraceIterator(fileList[0]);
		}catch (TotalADSReaderException ex){
			String message="Invalid training traces and the trace reader.\n"+ex.getMessage();
			throw new TotalADSUIException(message);
		}
		
	
		File validationFileList[]=getDirectoryHandler(validationDirectory,traceReader);
		
		try{ //Check for valid trace type reader and validation traces before creating a database
			traceReader.getTraceIterator(validationFileList[0]);
		}catch (TotalADSReaderException ex){
			String message="Invalid validation traces and the trace reader.\n"+ex.getMessage();
			throw new TotalADSUIException(message);
		}
	    ///////////////
		/// Db vverifications
		///////////////
		//Second,  create a database after verifications
		DBMS connection=Configuration.connection;
		if(isCreateDB){
				database=database.trim()+"_"+algorithm.getAcronym(); //+"_"+ traceReader.getAcronym();
				database=database.toUpperCase();
				//theModel.createDatabase(database, connection);// throws TotalADSDBMSException
				if (checkDBExistence(database)){
					throw new TotalADSUIException("Database already exists, select from the existing list!");
				}
			
		}
		else if (!checkDBExistence(database))
			throw new TotalADSUIException("Database does not exist!");
							
		//Third, start training
		console.clearText();
		console.printTextLn("Training the model....");
		
		for (int trcCnt=0; trcCnt<fileList.length; trcCnt++){
	
			if (trcCnt==fileList.length-1)
						 isLastTrace=true;
			 
			ITraceIterator trace=traceReader.getTraceIterator(fileList[trcCnt]);// get the trace
	 		
			console.printTextLn("Processing  training trace #"+(trcCnt+1)+": "+fileList[trcCnt].getName());
	 		algorithm.train(trace, isLastTrace, database,connection, console, algorithmOptions, isCreateDB);
		
		}
		//Fourth, start validation
		validateModels(validationFileList, traceReader,algorithm, database, console);
		
	}

	/**
	 * This functions validates a model for a given database of that model 
	 * @param fileList Array of files
	 * @param traceReader trace reader
	 * @param algorithm Algorithm object
	 * @param database Database name
	 * @param console console object
	 * @throws TotalADSUIException 
	 * @throws TotalADSReaderException
	 * @throws TotalADSDBMSException
	 */
	private void validateModels(File []fileList, ITraceTypeReader traceReader, IDetectionAlgorithm algorithm,
			String database,ProgressConsole console) throws TotalADSUIException, TotalADSReaderException, 
			TotalADSDBMSException {
		
				
		// process now
		console.printTextLn("Starting validation....");
		
		Boolean isLastTrace=false;
		
		for (int trcCnt=0; trcCnt<fileList.length; trcCnt++){
			 // get the trace
			if (trcCnt==fileList.length-1)
					isLastTrace=true;
			
 			ITraceIterator trace=traceReader.getTraceIterator(fileList[trcCnt]);
 		
 			console.printTextLn("Processing  validation trace #"+(trcCnt+1)+": "+fileList[trcCnt].getName());
 			
	 		algorithm.validate(trace, database, Configuration.connection, isLastTrace, console );

		}
		
		
		
	}
	/**
	 * Creates an empty model
	 * @throws TotalADSDBMSException 
	 
	public void createAnEmptyModel() throws TotalADSDBMSException, TotalADSUIException{
		
		    if (!btnEnterDB.getSelection() || txtNewDBName.getText().isEmpty() ){
				 throw new TotalADSUIException("Please, enter a new model name");
				 
			 }
			 else{
				 
				 if (txtNewDBName.getText().contains("_")){
					 throw new TotalADSUIException("DataModel name cannot contain underscore \"_\"");
				
				 }
				 String database=txtNewDBName.getText();
				 IDetectionAlgorithm algorithm=currentlySelectedAlgorithm.createInstance();
				 database=database.trim()+"_"+algorithm.getAcronym(); //+"_"+ traceReader.getAcronym();
				 database=database.toUpperCase();
				 algorithm.createDatabase(database, Configuration.connection);	
			}
	      
		 
	}*/
	
	/**
	 * 
	 * @param directory The name of the directory
	 * @param traceReader An object of the trace reader
	 * @return An array list of traces sutied for the appropriate type
	 * @throws TotalADSUIException
	 */
	
	private File[] getDirectoryHandler(String directory, ITraceTypeReader traceReader) throws TotalADSUIException{
		File traces=new File(directory);
		
		CTFLTTngSysCallTraceReader kernelReader=new CTFLTTngSysCallTraceReader();
		if (traceReader.getAcronym().equals(kernelReader.getAcronym()))
			return getDirectoryHandlerforLTTngTraces(traces);
		else // It is a text trace or any other 
			return getDirectoryHandlerforTextTraces(traces);
	}
	/**
	 * Get an array of trace list for a directory or just one file handler if there is only one file
	 * @param traces File object representing traces
	 * @return the file handler to the correct path
	 * @throws TotalADSUIException 
	 */
	private File[] getDirectoryHandlerforTextTraces(File traces) throws TotalADSUIException{
		
		File []fileList;
				
		if (traces.isDirectory()){ // if it is a directory return the list of all files
			    Boolean isAllFiles=false, isAllFolders=false;
			    fileList=traces.listFiles();
			    for (File file: fileList){
				
				   if (file.isDirectory())
					   isAllFolders=true;
				   else if (file.isFile())
					   isAllFiles=true;
			   
				   if (isAllFolders) // there is no need to continue further throw this msg	   
				  	   throw new TotalADSUIException("The folder "+traces.getName()+" contains"
					   		+ "	directories. Please put only trace files in it.");
			   
			    }
			    
			    if (!isAllFiles && !isAllFolders)
			    	 throw new TotalADSUIException("Empty directory: "+traces.getName());
				 
		}
	    else{// if it is a single file return the single file; however, this code will never be reached
	    	// as in GUI we are only using a directory handle, but if in futre we decide to change 
	    	// this could come handy
	            fileList= new File[1];
	            fileList[0]=traces;
	    }
		
			 return fileList;
	}
	
	/**
	 * Gets an array of list of directories 
	 * @param traces File object representing traces
	 * @return Handler to the correct path of files
	 * @throws TotalADSUIException
	 */
	private File[] getDirectoryHandlerforLTTngTraces(File traces) throws TotalADSUIException{
		
				
		if (traces.isDirectory()){
				File []fileList=traces.listFiles();
				File []fileHandler;
			    Boolean isAllFiles=false, isAllFolders=false;
			   
			    for (File file: fileList){
				
				   if (file.isDirectory())
					   isAllFolders=true;
				   else if (file.isFile())
					   isAllFiles=true;
			   
				   if (isAllFiles && isAllFolders) // there is no need to continue further throw this msg	   
				  	   throw new TotalADSUIException("The folder "+traces.getName()+" contains a mix of"+
					   		 " files and directories. Please put only LTTng traces' directories in it.");
			   
			    }
			    // if it has reached this far 
			    if (!isAllFiles && !isAllFolders)
			    	 throw new TotalADSUIException("Empty directory: "+traces.getName());
			    else if (isAllFiles){ // return the name of folder as a trace
			    	fileHandler =new File[1];
			    	fileHandler[0]=traces;
   		      } 
			    else // if all folders then return the list of all folders
			    	fileHandler=fileList;
			   
			     return fileHandler;
			   
			    
	    } else// this will not happen currently as we are only using the directory 
	    	  //loader in the main view
	    	throw new TotalADSUIException("You have selected a file"+traces.getName()+", select a folder");
	
	}
}

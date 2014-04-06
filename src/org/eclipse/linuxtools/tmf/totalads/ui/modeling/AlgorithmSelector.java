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

import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmTypes;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.ui.Settings;
import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.ProgressConsole;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


/**
 * This class loads algorithms from the {@link AlgorithmFactory} and creates appropriate GUI elements
 * @author <p> Syed Shariyar Murtaza jsutsshary@hotmail.com</p>
 *
 */

public class AlgorithmSelector {
	private Group grpAnalysisModelSelection;
	private Button btnAnalysisEvaluateModels;
	private Tree treeAnalysisModels;
	private MessageBox msgBox;
	private TreeItem currentlySelectedTreeItem;
	private IDetectionAlgorithm currentlySelectedModel;
	private Settings settingsDialog;
	private String []algorithmOptions;
	/**
	 * Constructor
	 * @param compParent An object of type composite
	 */
	public AlgorithmSelector(Composite compParent){
		

		/**
		 *  Group model selection
		 */
		
		grpAnalysisModelSelection=new Group(compParent,SWT.NONE);	
		grpAnalysisModelSelection.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,2,3));
		grpAnalysisModelSelection.setLayout(new GridLayout(2,false));
		grpAnalysisModelSelection.setText("Select an Algorithm");
		
		treeAnalysisModels = new Tree(grpAnalysisModelSelection, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION| SWT.V_SCROLL | SWT.H_SCROLL);
		
		treeAnalysisModels.setLinesVisible(true);
		treeAnalysisModels.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,2,6));
		
		TreeItem treeItmAnom = new TreeItem(treeAnalysisModels, SWT.NONE);
		treeItmAnom.setText("Anomaly Detection");
		
		populateTreeItems(treeItmAnom,AlgorithmTypes.ANOMALY);
		
		treeItmAnom.setExpanded(true);
		//TreeItem itm= new TreeItem(treeItmAnom, SWT.NONE);
		//TreeItem itm2= new TreeItem(treeItmAnom, SWT.NONE);
		/*//This code will be available in the next version. It has been commented out in this version
		TreeItem treeItmClassf = new TreeItem(treeAnalysisModels, SWT.NONE);
		treeItmClassf.setText("Classification");
		populateTreeItems(treeItmClassf,AlgorithmFactory.ModelTypes.Classification);
		treeItmClassf.setExpanded(true);
		*/
		
		treeAnalysisModels.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem item=(TreeItem)e.item;
			      
				if (item.getParentItem()==null)
						item.setChecked(false);
				else{
						if (currentlySelectedTreeItem!=null)
								 currentlySelectedTreeItem.setChecked(false);
						 item.setChecked(true);
						 currentlySelectedTreeItem=item;
						 currentlySelectedModel= ((IDetectionAlgorithm)currentlySelectedTreeItem.getData());
				}
					 
						
				
			
			}
		});
		
	
		/**
		 * End group model selection 
		*/
	}
	
	/**
	 * Populates the tree with the list of models from the model factory
	 */
	private void populateTreeItems(TreeItem treeItem,AlgorithmTypes algorithmTypes){
			///////data
			AlgorithmFactory  modFac=AlgorithmFactory.getInstance();
			
		    // populating anomaly detection models		
		    
			IDetectionAlgorithm []models  = modFac.getModels(algorithmTypes);
			
			if (models!=null){
				TreeItem []items=new TreeItem[models.length];
				for (int i=0;i <items.length;i++){
					items[i]=new TreeItem(treeItem,SWT.NONE);
					items[i].setText(models[i].getName());
					items[i].setData(models[i]);
					
				}
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
		settingsDialog= new Settings(currentlySelectedModel.getTrainingOptions());
		settingsDialog.showForm();
		algorithmOptions=settingsDialog.getOptions();
	}
	
	
	
	/**
	 * This function trains a model
	 * @param trainDirectory Train Directory
	 * @throws TotalADSUIException
	 * @throws TotalADSDBMSException 
	 * @throws TotalADSReaderException 
	 * 
	 */
	public void trainAndValidateModels(String trainDirectory, String validationDirectory, ITraceTypeReader traceReader, String database,
				Boolean isCreateDB, ProgressConsole console ) throws TotalADSUIException, TotalADSDBMSException, TotalADSReaderException {
		
		// First, verify selections
		if (!checkItemSelection())
			throw new TotalADSUIException("Please, first select an algorithm!");
       
		Boolean isLastTrace=false;
		IDetectionAlgorithm theModel=currentlySelectedModel.createInstance();
		
		
		File fileList[]=getDirectoryHandler(trainDirectory);// Get a file and a db handler
		DBMS connection=Configuration.connection;
		
		
		try{ //Check for valid trace type reader and training traces before creating a database
			traceReader.getTraceIterator(fileList[0]);
		}catch (TotalADSReaderException ex){
			String message="Invalid training traces and the trace reader.\n"+ex.getMessage();
			throw new TotalADSUIException(message);
		}
		
		File validationFileList[]=getDirectoryHandler(validationDirectory);
		
		try{ //Check for valid trace type reader and validation traces before creating a database
			traceReader.getTraceIterator(validationFileList[0]);
		}catch (TotalADSReaderException ex){
			String message="Invalid validation traces and the trace reader.\n"+ex.getMessage();
			throw new TotalADSUIException(message);
		}
		
		if(isCreateDB){
			if (database.contains("_"))
				throw new TotalADSUIException("Databse name cannot contain underscore \"_\"");
			else{
				database=database.trim()+"_"+theModel.getAcronym()+"_"+ traceReader.getAcronym();
				database=database.toUpperCase();
				theModel.createDatabase(database, connection);// throws TotalADSUIException
			}
		}
		else if (!checkDBExistence(database))
			throw new TotalADSUIException("Database does not exist!");
							
		// Second, start training
		console.clearText();
		console.printTextLn("Training the model....");
		
		for (int trcCnt=0; trcCnt<fileList.length; trcCnt++){
	
			if (trcCnt==fileList.length-1)
						 isLastTrace=true;
			 
			ITraceIterator trace=traceReader.getTraceIterator(fileList[trcCnt]);// get the trace
	 		
			console.printTextLn("Processing file "+fileList[trcCnt].getName());
	 		theModel.train(trace, isLastTrace, database,connection, console, algorithmOptions);
		
		}
		//Third, start validation
		validateModels(validationFileList, traceReader,theModel, database, console);
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
 		
 			console.printTextLn("Processing file "+fileList[trcCnt].getName());
 			
	 		algorithm.validate(trace, database, Configuration.connection, isLastTrace, console );

		}
		
		
		
	}
	
	/**
	 * Get an array of trace list for a directory
	 * @param directory Folder of traces
	 * @return
	 */
	private File[] getDirectoryHandler(String directory){
		File traces=new File(directory);
		File []fileList;
		
		
		if (traces.isDirectory())
	            fileList=traces.listFiles();
	    else{
	            fileList= new File[1];
	            fileList[0]=traces;
	    }
		return fileList;
	}
}

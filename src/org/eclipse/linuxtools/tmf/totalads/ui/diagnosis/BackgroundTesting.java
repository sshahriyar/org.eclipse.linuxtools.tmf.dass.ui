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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.algorithms.Results;
import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
import org.eclipse.linuxtools.tmf.totalads.readers.ctfreaders.CTFLTTngSysCallTraceReader;
import org.eclipse.linuxtools.tmf.totalads.ui.modeling.BackgroundModeling;
import org.eclipse.linuxtools.tmf.totalads.ui.modeling.StatusBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;

/**
 * This class evaluates an already created algorithm by running in background as thread. it is instantiated and executed 
 * from the {@link ModelLoader} class.
 * @author <p> Syed Shariyar Murtaza justssahry@hotmail.com </p>
 */

public class BackgroundTesting implements Runnable{
	private String testDirectory;
	private ITraceTypeReader traceReader;
	private IDetectionAlgorithm algorithm;
	private String database;
	private StatusBar statusBar;
	private Button btnDelete;
	private Button btnSettings;
	private Button btnAnalysisEvaluateModels;
	private ResultsAndFeedback resultsAndFeedback;
	private String []modelOptions;
	
	/**
	 * Constructor to create an object of BackgroundTesting
	 * @param testDirectory Test directory
	 * @param traceReader Trace reader
	 * @param algorithm Algorithm
	 * @param database Database
	 * @param statusBar An object of StatusBar
	 * @param btnDelete Delete button
	 * @param btnSettings Settings button
	 * @param btnEvaluate Evaluate button
	 * @param resultsAndFeedback Results and Feedback
	 * @param algorithmSettings Algorithm settings
	 */
	public BackgroundTesting(String testDirectory, ITraceTypeReader traceReader, IDetectionAlgorithm algorithm, String database,
				StatusBar statusBar, Button btnDelete, Button btnSettings, Button btnEvaluate, ResultsAndFeedback resultsAndFeedback
				, String []algorithmSettings){
		this.testDirectory=testDirectory;
		this.traceReader=traceReader;
		this.algorithm=algorithm;
		this.database=database;
		this.statusBar=statusBar;
		this.btnDelete=btnDelete;
		this.btnSettings=btnSettings;
		this.btnAnalysisEvaluateModels=btnEvaluate;
		this.resultsAndFeedback=resultsAndFeedback;
		this.modelOptions=algorithmSettings;
		this.resultsAndFeedback.registerAllModelNames(new String[]{database});
		
	}
	
	/**
	 * Overridden function to run a thread
	 */
		
	@Override
	public void run(){
			String msg=null;
			
			try {
				
				testTheModel(testDirectory, traceReader, algorithm, database);
							
			} 
			catch(TotalADSUIException ex){// handle UI exceptions here
										 //UI exceptions are simply notifications--no need to log them
				if (ex.getMessage()==null)
					msg="UI error";	
				else
					msg=ex.getMessage();
			}
			catch(TotalADSDBMSException ex){// handle DBMS exceptions here
				if (ex.getMessage()==null)
					msg="DBMS error: see log.";	
				else
					msg="DBMS error: "+ex.getMessage();
				Logger.getLogger(BackgroundModeling.class.getName()).log(Level.WARNING,msg,ex);
			}
			catch(TotalADSReaderException ex){// handle Reader exceptions here
				if (ex.getMessage()==null)
					msg="Reader error: see log.";	
				else
					msg="Reader error:"+ex.getMessage();
				Logger.getLogger(BackgroundModeling.class.getName()).log(Level.WARNING,msg,ex);
			}
			catch (Exception ex) { // handle all other exceptions here and log them too
				if (ex.getMessage()==null)
					msg="Severe error: see log.";	
				else
					msg=ex.getMessage();
				Logger.getLogger(BackgroundTesting.class.getName()).log(Level.SEVERE, msg, ex);
				// An exception could be thrown due to unavailability of the db, 
				// make sure that the connection is not lost
						Configuration.connection.connect(Configuration.host, Configuration.port);
				// We don't have to worry about exceptions here as the above function handles all the exceptions
				// and just returns a message. This function also initializes connection info to correct value
				// We cannot write above function under ConnectinException block because such exception is never thrown
				// and Eclipse starts throwing errors
			}
			finally{
				
				final String exception=msg;
						
				 Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						
						if (exception!=null){ // if there has been any exception then show its message
							MessageBox msgBox=new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,SWT.ICON_ERROR|SWT.OK);
							msgBox.setMessage(exception);
							msgBox.open();
						}
						btnAnalysisEvaluateModels.setEnabled(true);
						btnSettings.setEnabled(true);
						btnDelete.setEnabled(true);
						statusBar.initialState();
					}
				});
				
				
			}//End of finally
	}// end of function
			
	/**
	 * Tests the algorithm against a set of traces
	 * @param testDirectory Test directory
	 * @param traceReader Trace reader
	 * @param algorithm Algorithm of the algorithm
	 * @param database Databse
	 * @throws TotalADSUIException
	 * @throws TotalADSReaderException 
	 * @throws TotalADSDBMSException 
	 * 
	 */
	
	public void testTheModel(String testDirectory, ITraceTypeReader traceReader, IDetectionAlgorithm algorithm, String database )
			throws TotalADSUIException, TotalADSReaderException, TotalADSDBMSException {
				
				
			// First verify selections
			Boolean isLastTrace=false;
			Integer totalFiles;		
			//if (!checkItemSelection())
				//throw new TotalADSUIException("Please, first select a algorithm!");
	       if (testDirectory.isEmpty())
	    	   throw new TotalADSUIException("Please, first select a trace!");
			
			File fileList[]=getDirectoryHandler(testDirectory,traceReader);// Get a file and a db handler
			
			if (fileList.length >5000)
				throw new TotalADSUIException("More than 5000 traces can not be tested simultaneously.");
			
			DBMS connection=Configuration.connection;
			
			
			try{ //Check for valid trace type reader and traces before creating a database
				traceReader.getTraceIterator(fileList[0]);
			}catch (TotalADSReaderException ex){// this is just a validation error, cast it to UI exception
				String message="Invalid trace reader and traces: "+ex.getMessage();
				throw new TotalADSUIException(message);
			}
			
			
			// Second, start testing
			totalFiles=fileList.length;
			HashMap <String, Double> modelsAndAnomalyCount=new HashMap<String, Double>();
			
			
			for (int trcCnt=0; trcCnt<totalFiles; trcCnt++){
			  /// put for each model here
				HashMap<String,Results> modelResults=new HashMap<String, Results>();
				
				int counter=trcCnt+1;
				statusBar.setProgress("Processing trace #"+counter+"..."); 
					 
				ITraceIterator trace=traceReader.getTraceIterator(fileList[trcCnt]);// get the trace
		 					
		 		Results results= algorithm.test(trace, database, connection, modelOptions);
		 		modelResults.put(database,results);
		 		final String traceName=fileList[trcCnt].getName();
		 		
		 		resultsAndFeedback.addTraceResult(traceName, modelResults);
		 		
						
				}
	       
	     // Third, print summary
			
			Double totalAnoms=algorithm.getTotalAnomalyPercentage();
			modelsAndAnomalyCount.put(database,totalAnoms);
			resultsAndFeedback.setTotalAnomalyCount(modelsAndAnomalyCount);
			
			resultsAndFeedback.setTotalTraceCount(totalFiles.toString());
		
	
	}
	
	/**
	 *  Get a directory handle, if there is only one file it returns an array of size one
	 * @param testDirectory Test directory
	 * @param traceReader Trace reader
	 * @return list of files
	 
	private File[] getDirectoryHandler(String testDirectory, ITraceTypeReader traceReader){
		
		File traces=new File(testDirectory);
		String kernelCTF=TraceTypeFactory.getInstance().getCTFKernelorUserReader(true).getName();
		String userCTF=TraceTypeFactory.getInstance().getCTFKernelorUserReader(false).getName();
		File []fileList;
	
		if (traces.isDirectory())// Returns the list of files in a directory
            fileList=traces.listFiles();
		else{
            fileList= new File[1];// if there is only one file then assigns it
            fileList[0]=traces;
		}
		// CTF readers read directories only. If it is a file, CTF reader will throw an error.
		//Adding checks for this process
		if ( traceReader.getName().equals(kernelCTF) || traceReader.getName().equals(userCTF)){
			
			if (!fileList[0].isDirectory()){ // if the inner files are not directory;i.e., only one folder--it means return a directory 
					fileList= new File[1];
					fileList[0]=traces; //Return the directory;
			}
			 //else return the directory list
			 // if the list is a combination of files and directories then this will result in an exception in the testTheModel function
		}
		
		
		
		return fileList;
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
					   		+ " directories. Please put only trace files in it.");
			   
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

	
// End of BackgroundTesting class	
}

//End of ModelLoader class	


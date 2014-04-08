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
import org.eclipse.linuxtools.tmf.totalads.ui.modeling.BackgroundModeling;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;

/**
 * This class evaluates an already created model by running in background as thread. it is instantiated and executed 
 * from the {@link ModelLoader} class.
 * @author <p> Syed Shariyar Murtaza justssahry@hotmail.com </p>
 */

public class BackgroundTesting extends Thread{
	private String testDirectory;
	private ITraceTypeReader traceReader;
	private IDetectionAlgorithm model;
	private String database;
	private Label lblProgress;
	private Button btnDelete;
	private Button btnSettings;
	private Button btnAnalysisEvaluateModels;
	private ResultsAndFeedback resultsAndFeedback;
	String []modelOptions;
	/**
	 * Constructor to create an object of BackgroundTesting
	 * @param testDirectory Test directory
	 * @param traceReader Trace reader
	 * @param algorithm Algorithm
	 * @param database Database
	 * @param lblProg Progress label
	 * @param btnDelete Delete button
	 * @param btnSettings Settings button
	 * @param btnEvaluate Evaluate button
	 * @param resultsAndFeedback Results and Feedback
	 * @param algorithmSettings Algorithm settings
	 */
	public BackgroundTesting(String testDirectory, ITraceTypeReader traceReader, IDetectionAlgorithm algorithm, String database,
				Label lblProg, Button btnDelete, Button btnSettings, Button btnEvaluate, ResultsAndFeedback resultsAndFeedback
				, String []algorithmSettings){
		this.testDirectory=testDirectory;
		this.traceReader=traceReader;
		this.model=algorithm;
		this.database=database;
		this.lblProgress=lblProg;
		this.btnDelete=btnDelete;
		this.btnSettings=btnSettings;
		this.btnAnalysisEvaluateModels=btnEvaluate;
		this.resultsAndFeedback=resultsAndFeedback;
		this.modelOptions=algorithmSettings;
	}
	
	/**
	 * Overridden function to run a thread
	 */
		
	@Override
	public void run(){
			String msg=null;
			
			try {
				
				testTheModel(testDirectory, traceReader, model, database);
							
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
						lblProgress.setVisible(false);
						
					}
				});
				
				
			}//End of finally
	}// end of function
			
	/**
	 * Tests the model agaisnt a set of traces
	 * @param testDirectory Test directory
	 * @param traceReader Trace reader
	 * @param algorithm Algorithm of the model
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
					
			//if (!checkItemSelection())
				//throw new TotalADSUIException("Please, first select a model!");
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
			
			for (int trcCnt=0; trcCnt<fileList.length; trcCnt++){
				final int counter=trcCnt+1;
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						lblProgress.setText("Processing trace #"+counter+"..."); 
						}
				});
				 
				ITraceIterator trace=traceReader.getTraceIterator(fileList[trcCnt]);// get the trace
		 					
		 		final Results results= algorithm.test(trace, database, connection, modelOptions);
		 		final String traceName=fileList[trcCnt].getName();
		 		
		 		Display.getDefault().syncExec(new Runnable() {
					
					@Override
					public void run() {
						
						resultsAndFeedback.addTraceResult(traceName, results);
						
						
					}
				});
		 		
			}
	       
	     // Third, print summary
			final String summary=algorithm.getSummaryOfTestResults();
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					resultsAndFeedback.setSummary(summary);
					
				}
			});
	
	}
	
	/**
	 *  Get a directory handle, if there is only one file it returns an array of size one
	 * @param testDirectory Test directory
	 * @param traceReader Trace reader
	 * @return list of files
	 */
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
	}
	
// End of BackgroundTesting class	
}

//End of ModelLoader class	


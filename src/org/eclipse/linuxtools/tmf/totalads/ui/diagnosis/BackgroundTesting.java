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

import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmOutStream;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.algorithms.Results;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMSFactory;
import org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.ctfreaders.CTFLTTngSysCallTraceReader;
import org.eclipse.linuxtools.tmf.totalads.ui.io.ProgressConsole;
import org.eclipse.linuxtools.tmf.totalads.ui.modeling.BackgroundModeling;
import org.eclipse.linuxtools.tmf.totalads.ui.results.ResultsAndFeedback;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

/**
 * This class evaluates an already created algorithm by running in background as thread.
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 */

public class BackgroundTesting implements Runnable{
    private String fTestDirectory;
    private ITraceTypeReader fTraceReader;
    private IDetectionAlgorithm[] fAlgorithm;
    private String[] fDatabase;
    private Button fBtnAnalysisEvaluateModels;
    private ResultsAndFeedback fResultsAndFeedback;


	/**
	 * Constructor to create an object of BackgroundTesting
	 * @param testDirectory Test directory
	 * @param traceReader Trace reader
	 * @param algorithm Algorithm
	 * @param database Database
	 * @param btnEvaluate Evaluate button
	 * @param resultsAndFeedback An object to display results
	 */
	public BackgroundTesting(String testDirectory, ITraceTypeReader traceReader, IDetectionAlgorithm []algorithm, String []database,
				  Button btnEvaluate, ResultsAndFeedback resultsAndFeedback	){
		this.fTestDirectory=testDirectory;
		this.fTraceReader=traceReader;
		this.fAlgorithm=algorithm;
		this.fDatabase=database;
		this.fBtnAnalysisEvaluateModels=btnEvaluate;
		this.fResultsAndFeedback=resultsAndFeedback;
		this.fResultsAndFeedback.registerAllModelNames(database);

	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run(){
			String msg=null;

			try {

				testTheModel(fTestDirectory, fTraceReader, fAlgorithm, fDatabase);

			}
			catch(TotalADSGeneralException ex){// handle UI exceptions here
										 //UI exceptions are simply notifications--no need to log them
				if (ex.getMessage()==null) {
                    msg=Messages.BackgroundTesting_GeneralException;
                } else {
                    msg=ex.getMessage();
                }
			}
			catch(TotalADSDBMSException ex){// handle IDataAccessObject exceptions here
				if (ex.getMessage()==null) {
                    msg=Messages.BackgroundTesting_CommonException;
                }
                else {
                    msg=Messages.BackgroundTesting_DBMSException+ex.getMessage();
                }
				Logger.getLogger(BackgroundModeling.class.getName()).log(Level.WARNING,msg,ex);
			}
			catch(TotalADSReaderException ex){// handle Reader exceptions here
				if (ex.getMessage()==null) {
                    msg=Messages.BackgroundTesting_CommonException;
                } else {
                    msg=Messages.BackgroundTesting_ReaderException+ex.getMessage();
                }
				Logger.getLogger(BackgroundModeling.class.getName()).log(Level.WARNING,msg,ex);
			}
			catch (Exception ex) { // handle all other exceptions here and log them too
				if (ex.getMessage()==null) {
                    msg=Messages.BackgroundTesting_CommonException;
                } else {
                    msg=ex.getMessage();
                }
				Logger.getLogger(BackgroundTesting.class.getName()).log(Level.SEVERE, msg, ex);
				// An exception could be thrown due to unavailability of the db,
				// make sure that the connection is not lost
				DBMSFactory.INSTANCE.verifyConnection();
				// We don't have to worry about exceptions here as the above function handles all the exceptions
				// and just returns a message. This function also initializes connection info  to a correct value
				// We cannot write above function under ConnectinException block because such exception is never thrown
				// and Eclipse starts throwing errors
			}
			finally{

				final String exception=msg;

				 Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {

						if (exception!=null){ // if there has been any exception then show its message
							MessageBox msgBox=new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,SWT.ICON_ERROR);
							msgBox.setMessage(exception);
							msgBox.open();
						}else{
							MessageBox msgBox=new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,SWT.ICON_WORKING);
							msgBox.setMessage(Messages.BackgroundTesting_CompletionMessage);
							msgBox.open();
						}

						fBtnAnalysisEvaluateModels.setEnabled(true);
					}
				});


			}//End of finally
	}// end of function

	/**
	 * Tests the algorithm against a set of traces
	 * @param testDirectory Test directory
	 * @param traceReader Trace reader
	 * @param algorithm Algorithm of the algorithm
	 * @param database Database
	 * @throws TotalADSGeneralException General exception (usually validation errors)
	 * @throws TotalADSReaderException Exception related to trace reading
	 * @throws TotalADSDBMSException Exception related to database
	 *
	 */
    public void testTheModel(String testDirectory, ITraceTypeReader traceReader, IDetectionAlgorithm[] algorithm,
            String[] database) throws TotalADSGeneralException, TotalADSReaderException, TotalADSDBMSException {

			// First verify selections
			//Boolean isLastTrace=false;
			Integer totalFiles;

	       //if (fTestDirectory.isEmpty())
	    	//   throw new TotalADSGeneralException("Please, first select a trace!");

			File fileList[]=getDirectoryHandler(testDirectory,traceReader);// Get a file and a db handler

			if (fileList.length >5000) {
                throw new TotalADSGeneralException(Messages.BackgroundTesting_TraceLimit);
            }

			IDataAccessObject connection=DBMSFactory.INSTANCE.getDataAccessObject();

			 //Check for valid trace type reader and traces before creating a fDatabase
			try(ITraceIterator it=	traceReader.getTraceIterator(fileList[0]) ) {

			}catch (TotalADSReaderException ex){
			    // this is just a validation error, cast it to UI exception
				String message= NLS.bind(Messages.BackgroundTesting_InvalidTrace,ex.getMessage());
				throw new TotalADSGeneralException(message);
			}

			ProgressConsole console= new ProgressConsole(Messages.BackgroundTesting_ConsoleTitle);
			console.println(Messages.BackgroundTesting_ConsoleStartMessage);
			AlgorithmOutStream outStreamAlg=new AlgorithmOutStream();
			outStreamAlg.addObserver(console);
			// Second, start testing
			totalFiles=fileList.length;
			HashMap <String, Double> modelsAndAnomalyCount=new HashMap<>();
			// for each trace
			for (int trcCnt=0; trcCnt<totalFiles; trcCnt++){

				console.println(NLS.bind(Messages.BackgroundTesting_TraceCountMessage, trcCnt,fileList[trcCnt]));
				// for each selected model
				HashMap<String,Results> modelResults=new HashMap<>();
				final String traceName=fileList[trcCnt].getName();

				for (int modelCnt=0; modelCnt<database.length; modelCnt++){

					    console.println(NLS.bind(Messages.BackgroundTesting_ModelEval,database[modelCnt]));

						try (ITraceIterator trace=
						        traceReader.getTraceIterator(fileList[trcCnt]) ) {// get the trace

						        Results results= algorithm[modelCnt].test(trace, database[modelCnt], connection,outStreamAlg);
        				 		modelResults.put(database[modelCnt],results);
						}
				 		 // Third, print summary
						Double totalAnoms=algorithm[modelCnt].getTotalAnomalyPercentage();
						modelsAndAnomalyCount.put(database[modelCnt],totalAnoms);
						fResultsAndFeedback.setTotalAnomalyCount(modelsAndAnomalyCount);
						fResultsAndFeedback.addTraceResult(traceName, modelResults);
				}

		  }

	    fResultsAndFeedback.setTotalTraceCount(totalFiles.toString());


	}



	/**
	 *
	 * @param directory The name of the directory
	 * @param traceReader An object of the trace reader
	 * @return An array list of traces suited for the appropriate type
	 * @throws TotalADSGeneralException
	 */
	private static File[] getDirectoryHandler(String directory, ITraceTypeReader traceReader) throws TotalADSGeneralException{
		File traces=new File(directory);

		CTFLTTngSysCallTraceReader kernelReader=new CTFLTTngSysCallTraceReader();
		if (traceReader.getAcronym().equals(kernelReader.getAcronym())) {
            return getDirectoryHandlerforLTTngTraces(traces);
        }
        return getDirectoryHandlerforTextTraces(traces);
	}


	/**
	 * Get an array of trace list for a directory or just one file handler if there is only one file
	 * @param traces File object representing traces
	 * @return the file handler to the correct path
	 * @throws TotalADSGeneralException
	 */
	private static File[] getDirectoryHandlerforTextTraces(File traces) throws TotalADSGeneralException{

		File []fileList;

		if (traces.isDirectory()){ // if it is a directory return the list of all files
			    Boolean isAllFiles=false, isAllFolders=false;
			    fileList=traces.listFiles();
			    for (File file: fileList){

				   if (file.isDirectory()) {
                    isAllFolders=true;
                } else if (file.isFile()) {
                    isAllFiles=true;
                }

				   if (isAllFolders) {
                    throw new TotalADSGeneralException(NLS.bind(Messages.BackgroundTesting_FolderContainsDir, traces.getName()));
                }


			    }

			    if (!isAllFiles && !isAllFolders) {
                    throw new TotalADSGeneralException(NLS.bind(Messages.BackgroundTesting_EmptyDirectory, traces.getName()));
                }

		}
	    else{// if it is a single file return the single file; however, this code will never be reached
	    	// as in GUI we are only using a directory handle, but if in future we decide to change
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
	 * @throws TotalADSGeneralException
	 */
	private static File[] getDirectoryHandlerforLTTngTraces(File traces) throws TotalADSGeneralException{


		if (traces.isDirectory()){
				File []fileList=traces.listFiles();
				File []fileHandler;
			    Boolean isAllFiles=false, isAllFolders=false;

			    for (File file: fileList){

				   if (file.isDirectory()) {
                    isAllFolders=true;
                } else if (file.isFile()) {
                    isAllFiles=true;
                }

				   if (isAllFiles && isAllFolders) {
                    throw new TotalADSGeneralException(NLS.bind(Messages.BackgroundTesting_LTTngFolderContainsFilesandDir,traces.getName()));
                }

			    }
			    // if it has reached this far
			    if (!isAllFiles && !isAllFolders) {
                    throw new TotalADSGeneralException( NLS.bind(Messages.BackgroundTesting_EmptyDirectory, traces.getName()));
                } else if (isAllFiles){ // return the name of folder as a trace
			    	fileHandler =new File[1];
			    	fileHandler[0]=traces;
   		      } else {
                    fileHandler=fileList;
                }

			     return fileHandler;


	    }
        //loader in the main view
        throw new TotalADSGeneralException(NLS.bind(Messages.BackgroundTesting_SelectFolder,traces.getName()));
	}


// End of BackgroundTesting class
}



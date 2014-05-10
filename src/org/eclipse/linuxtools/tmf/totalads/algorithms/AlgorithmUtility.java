/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.algorithms;

import java.io.File;

import org.eclipse.linuxtools.tmf.totalads.dbms.DBMSFactory;
import org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.ctfreaders.CTFLTTngSysCallTraceReader;
import org.eclipse.linuxtools.tmf.totalads.ui.io.ProgressConsole;

/**
 * Utility class to execute algorithms by implementing common recurring tasks required for algorithms.
 * 
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public  class AlgorithmUtility {

	private AlgorithmUtility() {
		
	}

	/**
	 * Creates a model in the database with training settings
	 * @param modelName Model name
	 * @param algorithm Algorithm type
	 * @param connection IDataAccessObject object
	 * @param trainingSettings Training SettingsForm object
	 * @throws TotalADSDBMSException
	 * @throws TotalADSGeneralException
	 */
	public static void createModel(String modelName, IDetectionAlgorithm algorithm, IDataAccessObject connection, String []trainingSettings ) throws TotalADSDBMSException, TotalADSGeneralException{
		modelName+="_"+algorithm.getAcronym();
		modelName=modelName.toUpperCase();		
		algorithm.initializeModelAndSettings(modelName, DBMSFactory.INSTANCE.getDataAccessObject(), trainingSettings);
	}
	
	/**
	 * Returns the algorithm for a given model name
	 * @param modelName Name of the model
	 * @param connection IDataAccessObject connection object
	 * @return An object of type IDetectionAlgorithm
	 * @throws TotalADSGeneralException 
	 */
	public static IDetectionAlgorithm getAlgorithmFromModelName(String modelName, IDataAccessObject connection) throws TotalADSGeneralException{
		
		String []modelParts=modelName.split("_");
		if (modelParts==null  || modelParts.length <2)
			throw new TotalADSGeneralException("Not a valid model created by TotalADS");
		
		String algorithmAcronym=modelParts[1];
		IDetectionAlgorithm algorithm=AlgorithmFactory.getInstance().getAlgorithmByAcronym(algorithmAcronym);
		if (algorithm==null)
			throw new TotalADSGeneralException("Not a valid model created by TotalADS");
		
		return algorithm;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	////////// Training and Validation
	/////////////////////////////////////////////////////////////////////////////////////
	/**
	 * This function trains and validate models
	 * @param trainDirectory
	 * @param validationDirectory
	 * @param traceReader
	 * @throws TotalADSGeneralException
	 * @throws TotalADSDBMSException
	 * @throws TotalADSReaderException
	 */
	public static void trainAndValidateModels(String trainDirectory, String validationDirectory, ITraceTypeReader traceReader,
					IDetectionAlgorithm selectedAlgorithm, String modelName )
							throws TotalADSGeneralException, TotalADSDBMSException, TotalADSReaderException {

		ProgressConsole console=new ProgressConsole("Modeling");
		AlgorithmOutStream outStream=new AlgorithmOutStream();
		outStream.addObserver(console);
		       
		Boolean isLastTrace=false;
		IDetectionAlgorithm algorithm=selectedAlgorithm.createInstance();
				
		////////////////////
		///File verifications of traces
		///////////////////
		File fileList[]=getDirectoryHandler(trainDirectory,traceReader);// Get a file handler
				
		try{ //Check for valid trace type reader and training traces before creating a database
			traceReader.getTraceIterator(fileList[0]);
		}catch (TotalADSReaderException ex){
			String message="Invalid training traces and the trace reader.\n"+ex.getMessage();
			throw new TotalADSGeneralException(message);
		}
		
	
		File validationFileList[]=getDirectoryHandler(validationDirectory,traceReader);
		
		try{ //Check for valid trace type reader and validation traces before creating a database
			traceReader.getTraceIterator(validationFileList[0]);
		}catch (TotalADSReaderException ex){
			String message="Invalid validation traces and the trace reader.\n"+ex.getMessage();
			throw new TotalADSGeneralException(message);
		}
	   
		IDataAccessObject dataAcessObject=DBMSFactory.INSTANCE.getDataAccessObject();
				
		///////////					
		//Second, start training
		////
		console.clearConsole();
		console.println("Training the model....");
		
		for (int trcCnt=0; trcCnt<fileList.length; trcCnt++){
	
			if (trcCnt==fileList.length-1)
						 isLastTrace=true;
			 
			ITraceIterator trace=traceReader.getTraceIterator(fileList[trcCnt]);// get the trace
	 		
			console.println("Processing  training trace #"+(trcCnt+1)+": "+fileList[trcCnt].getName());
	 		algorithm.train(trace, isLastTrace, modelName,dataAcessObject, outStream);
		
		}
		//Fourth, start validation
		validateModels(validationFileList, traceReader,algorithm, modelName, outStream);
		
	}

	/**
	 * This functions validates a model for a given database of that model 
	 * @param fileList Array of files
	 * @param traceReader trace reader
	 * @param algorithm Algorithm object
	 * @param database Database name
	 * @param outStream console object
	 * @throws TotalADSGeneralException 
	 * @throws TotalADSReaderException
	 * @throws TotalADSDBMSException
	 */
	private static void validateModels(File []fileList, ITraceTypeReader traceReader, IDetectionAlgorithm algorithm,
			String database,AlgorithmOutStream outStream) throws TotalADSGeneralException, TotalADSReaderException, 
			TotalADSDBMSException {
		
				
		// process now
		outStream.addOutputEvent("Starting validation....");
		
		Boolean isLastTrace=false;
		
		for (int trcCnt=0; trcCnt<fileList.length; trcCnt++){
			 // get the trace
				if (trcCnt==fileList.length-1)
						isLastTrace=true;
				
	 			ITraceIterator trace=traceReader.getTraceIterator(fileList[trcCnt]);
	 		
	 			outStream.addOutputEvent("Processing  validation trace #"+(trcCnt+1)+": "+fileList[trcCnt].getName());
	 			
		 		algorithm.validate(trace, database, DBMSFactory.INSTANCE.getDataAccessObject(), isLastTrace, outStream );

		}
		
		
		
	}
	
	
	/**
	 * 
	 * @param directory The name of the directory
	 * @param traceReader An object of the trace reader
	 * @return An array list of traces sutied for the appropriate type
	 * @throws TotalADSGeneralException
	 */
	
	private static File[] getDirectoryHandler(String directory, ITraceTypeReader traceReader) throws TotalADSGeneralException{
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
	 * @throws TotalADSGeneralException 
	 */
	private static File[] getDirectoryHandlerforTextTraces(File traces) throws TotalADSGeneralException{
		
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
				  	   throw new TotalADSGeneralException("The folder "+traces.getName()+" contains"
					   		+ "	directories. Please put only trace files in it.");
			   
			    }
			    
			    if (!isAllFiles && !isAllFolders)
			    	 throw new TotalADSGeneralException("Empty directory: "+traces.getName());
				 
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
	 * @throws TotalADSGeneralException
	 */
	private static File[] getDirectoryHandlerforLTTngTraces(File traces) throws TotalADSGeneralException{
		
				
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
				  	   throw new TotalADSGeneralException("The folder "+traces.getName()+" contains a mix of"+
					   		 " files and directories. Please put only LTTng traces' directories in it.");
			   
			    }
			    // if it has reached this far 
			    if (!isAllFiles && !isAllFolders)
			    	 throw new TotalADSGeneralException("Empty directory: "+traces.getName());
			    else if (isAllFiles){ // return the name of folder as a trace
			    	fileHandler =new File[1];
			    	fileHandler[0]=traces;
   		      } 
			    else // if all folders then return the list of all folders
			    	fileHandler=fileList;
			   
			     return fileHandler;
			   
			    
	    } else// this will not happen currently as we are only using the directory 
	    	  //loader in the main view
	    	throw new TotalADSGeneralException("You have selected a file"+traces.getName()+", select a folder");
	
	}
}
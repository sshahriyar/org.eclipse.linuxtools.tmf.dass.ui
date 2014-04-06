package org.eclipse.linuxtools.tmf.totalads.ui.diagnosis;

import java.io.File;

import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.algorithms.Results;
import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
import org.eclipse.swt.widgets.Display;

/**
 * This class tests a model by launching a thread
 * @author <p> Syed Shariyar Murtaza justssahry@hotmail.com </p>
 */

public class BackgroundTesting extends Thread{
	String testDirectory;
	ITraceTypeReader traceReader;
	IDetectionAlgorithm model;
	String database;
	/**
	 * Constructor
	 * @param testDirectory
	 * @param traceReader
	 * @param model
	 * @param database
	 */
	public BackgroundTesting(String testDirectory, ITraceTypeReader traceReader, IDetectionAlgorithm model, String database){
		this.testDirectory=testDirectory;
		this.traceReader=traceReader;
		this.model=model;
		this.database=database;
	}
	
	
		
	@Override
	public void run(){
			String msg=null;
			
			try {
				
				testTheModel(testDirectory, traceReader, model, database);
							
			} 
			catch(TotalADSUIException ex){// handle UI exceptions here
				if (ex.getMessage()==null)
					msg="Severe error: see log.";	
				else
					msg=ex.getMessage();
			}
			catch (Exception ex) { // handle all other exceptions here and log them too.
									//UI exceptions are simply notifications--no need to log them
									
				if (ex.getMessage()==null)
					msg="Severe error: see log.";	
				else
					msg=ex.getMessage();
				ex.printStackTrace();
			}
			finally{
				
				final String exception=msg;
						
				 Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						if (exception!=null){ // if there has been any exception then show its message
							msgBox.setMessage(exception);
							msgBox.open();
						}
						btnAnalysisEvaluateModels.setEnabled(true);
						btnSettings.setEnabled(true);
						btnDelete.setEnabled(true);
						lblProgress.setVisible(false);
						
					}
				});
				
				
			}//End finally
	}
			
	/**
	 * Tests the model
	 * @param testDirectory
	 * @param traceReader
	 * @param model
	 * @param database
	 * @throws TotalADSUIException
	 * @throws Exception
	 */
	public void testTheModel(String testDirectory, ITraceTypeReader traceReader, IDetectionAlgorithm model, String database ) throws TotalADSUIException,Exception {
				
				
			// First verify selections
			Boolean isLastTrace=false;
					
			if (!checkItemSelection())
				throw new TotalADSUIException("Please, first select a model!");
	       if (testDirectory.isEmpty())
	    	   throw new TotalADSUIException("Please, first select a trace!");
			
			File fileList[]=getDirectoryHandler(testDirectory,traceReader);// Get a file and a db handler
			
			if (fileList.length >5000)
				throw new TotalADSUIException("More than 5000 traces can not be tested simultaneously.");
			
			DBMS connection=Configuration.connection;
			
			
			try{ //Check for valid trace type reader and traces before creating a database
				traceReader.getTraceIterator(fileList[0]);
			}catch (Exception ex){
				String message="Invalid trace reader and traces: "+ex.getMessage();
				throw new TotalADSUIException(message);
			}
			
			
			// Second, start testing
			
			for (int trcCnt=0; trcCnt<fileList.length; trcCnt++){
				final int counter=trcCnt+1;
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {lblProgress.setText("Processing trace #"+counter+"..."); }
				});
				 
				ITraceIterator trace=traceReader.getTraceIterator(fileList[trcCnt]);// get the trace
		 					
		 		final Results results= model.test(trace, database, connection, modelOptions);
		 		final String traceName=fileList[trcCnt].getName();
		 		
		 		Display.getDefault().syncExec(new Runnable() {
					
					@Override
					public void run() {
						
						resultsAndFeedback.addTraceResult(traceName, results);
						
						
					}
				});
		 		
			}
	       
	     // print summary
			final String summary=model.getSummaryOfTestResults();
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					resultsAndFeedback.setSummary(summary);
					
				}
			});
	
	}
	
	/**
	 * Get a directory handle, if there is only one file it returns an array of size one
	 * @param trainDirectory
	 * @return File[]
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


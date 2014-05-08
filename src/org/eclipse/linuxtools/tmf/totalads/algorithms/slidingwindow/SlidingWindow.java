/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.algorithms.slidingwindow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.linuxtools.tmf.totalads.algorithms.IAlgorithmOutStream;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.algorithms.Results;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmTypes;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmOutStream;
import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.swtchart.Chart;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


/**
 * This class implements the Sliding Window algorithm for the host-based anomaly detection.
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p> 
 * 
 */
public class SlidingWindow implements IDetectionAlgorithm {
	 
	//private String TRACE_COLLECTION="trace_data";//Configuration.traceCollection;
	//private String SETTINGS_COLLECTION="settings";//Configuration.settingsCollection;
	private Integer maxWin=5;
	private Integer maxHamDis=0;
	private String warningMessage="";
	private HashMap<Integer, Event[]> sysCallSequences;
	private Boolean treeExists;
	private String []trainingOptions={"Max Win","5", "Max Hamming Distance","0"};
	private String []testingOptions={"Max Hamming Distance","0"};
	private Integer validationTraceCount=0;
	private Integer validationAnomalies=0;
	private Integer testTraceCount=0;
	private Integer testAnomalies=0;
	private Boolean intialize=false;
	private Boolean isTestStarted=false;
	private SlidingWindowTree treeTransformer;
	private int maxWinLimit=25;
	private NameToIDMapper nameToID;
	private int testNameToIDSize; 
	
	/**
	 * Constructor
	 **/
	public SlidingWindow() {
		treeExists=false;
		sysCallSequences= new HashMap<Integer, Event[]>();
		treeTransformer=new SlidingWindowTree();
		nameToID=new NameToIDMapper();
		
	}
	/**
	 * Initializes the model if already exists in the database
	 * @param connection DBMS object
	 * @param database	Database name
	 */
	private void initialize(DBMS connection,String database) {
		
			DBCursor cursor=connection.selectAll(database, TraceCollection.COLLECTION_NAME.toString());
			if (cursor !=null){
				while (cursor.hasNext()){
					DBObject dbObject=cursor.next();
 					Gson gson =new Gson();
 					Integer key=(Integer) dbObject.get(TraceCollection.KEY.toString());
 					Object obj=dbObject.get(TraceCollection.TREE.toString());
 					if (obj!=null){
 						Event []event = gson.fromJson(obj.toString(), Event[].class);
 						sysCallSequences.put(key, event);
 					} 
				 treeExists=true;
				}
				
				cursor.close();
			}else
				treeExists=false;
			
			// Get the maxWin and maxHam
			cursor=connection.selectAll(database, SettingsCollection.COLLECTION_NAME.toString());
			if (cursor !=null){
				while (cursor.hasNext()){
					DBObject dbObject=cursor.next();
					maxWin=Integer.parseInt(dbObject.get(SettingsCollection.MAX_WIN.toString()).toString());
					maxHamDis=Integer.parseInt(dbObject.get(SettingsCollection.MAX_HAM_DIS.toString()).toString());
				}
				cursor.close();
			}
	
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#getTrainingOptions()
	 */
    @Override
    public String[] getTrainingOptions(){
    	   
    	 return trainingOptions;
    	     	
    }
  

   /*
    * (non-Javadoc)
    * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#saveTestingOptions(java.lang.String[], java.lang.String, org.eclipse.linuxtools.tmf.totalads.dbms.DBMS)
    */
    @Override
    public void saveTestingOptions(String [] options, String database, DBMS connection) throws TotalADSUIException, TotalADSDBMSException{
    	 Integer theMaxHamDis=0;
    	if (options!=null && options[0].equals(this.testingOptions[0]) ){
  		  	try {
  		  		theMaxHamDis=Integer.parseInt(options[1]);
  		  	}catch (NumberFormatException ex){
  		  		throw new TotalADSUIException("Please, enter an integer value.");
  		  	}
  		   
  		  	/// Get previous max window first
  		    loadSetings(database, connection);
  		    maxHamDis=theMaxHamDis;// change the maxHam
  	        saveSettings(database, connection); // save maxHamm
  	     }
    	
    	  
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#getTestingOptions(java.lang.String, org.eclipse.linuxtools.tmf.totalads.dbms.DBMS)
     */
    @Override
    public String[] getTestingOptions(String database, DBMS connection){
    	loadSetings(database, connection);
    	testingOptions[1]=maxHamDis.toString(); 		
    	return testingOptions;
    	
    	
    }
    
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#initializeModelAndSettings(java.lang.String, org.eclipse.linuxtools.tmf.totalads.dbms.DBMS, java.lang.String[])
	 */
	@Override
	public void initializeModelAndSettings(String modelName, DBMS connection, String[] trainingSettings) throws TotalADSDBMSException, TotalADSUIException{
		 
		if (trainingSettings!=null){
	   		  try{
		    		  for (int i=0; i<trainingSettings.length;i++){
		    			   if (trainingSettings[i].equals(this.trainingOptions[0]))
				    		  	maxWin=Integer.parseInt(trainingSettings[i+1]);// on error exception will be thrown automatically
		    			   else	 if (trainingSettings[i].equals(this.trainingOptions[2]) )
				    		  	maxHamDis=Integer.parseInt(trainingSettings[i+1]);// on error exception will be thrown automatically
		    		  }
	   		  }catch (Exception ex){// Capturing exception to send a UI error
	   			  throw new TotalADSUIException("Please, enter integer numbers only in settings' fileds.");
	   		  }
	   		  
	   		  if (maxWin > maxWinLimit)
	   			   throw new TotalADSUIException ("Sequence size too large; select "+maxWinLimit+" or lesser.");
   	    }
		
		
		String []collectionNames={TraceCollection.COLLECTION_NAME.toString(), SettingsCollection.COLLECTION_NAME.toString(),
								  NameToIDCollection.COLLECTION_NAME.toString()};
		connection.createDatabase(modelName, collectionNames);
		saveSettings(modelName, connection);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#train(org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator, java.lang.Boolean, java.lang.String, org.eclipse.linuxtools.tmf.totalads.dbms.DBMS, org.eclipse.linuxtools.tmf.totalads.algorithms.IAlgorithmOutStream)
	 */
	@Override
	public void train (ITraceIterator trace, Boolean isLastTrace, String database, DBMS connection, IAlgorithmOutStream outStream)  throws TotalADSUIException, TotalADSDBMSException,TotalADSReaderException {
	    
		 if (!intialize){
			  validationTraceCount=0;
			  validationAnomalies=0;
     	      initialize(connection,database);
		      intialize=true;
		      nameToID.loadMap(connection, database);
	    		    	  
	      }
	    	  
	    	  
		  int  winWidth=0;
		  int seqCount=1;
	      LinkedList<Integer> newSequence=new LinkedList<Integer>();
	     String event=null;
	      while (trace.advance()) {
	    	  event=trace.getCurrentEvent();
	    	  
	    	  newSequence.add(nameToID.getId(event));
	    	 
	    	  winWidth++;
	    	      	  
	    	  if(winWidth >= maxWin){
	    		  		
	    		  winWidth--;
	    		  Integer[] seq=new Integer[maxWin];
	    		  seq=newSequence.toArray(seq);
	    		  // searching and adding to db
	    		 // System.out.println(Arrays.toString(seq));
	    		 // console.printTextLn("Adding sequence "+seqCount+ " starting with "+seq[0]);
	    		  treeTransformer.searchAndAddSequence(seq,sysCallSequences);
	    		  //treeTransformer.searchAndAddSequence(seq,database,connection);
	    		  newSequence.remove(0);
	    		  seqCount++;
	    	  }
	    		  
	     }
	     if (isLastTrace){ 
	    	 // Saving events tree in database
	    	 treeTransformer.printSequence(outStream, database, sysCallSequences);
	    	 treeTransformer.saveinDatabase(outStream, database, connection, sysCallSequences);
	    	 intialize=false;
	    	 nameToID.saveMap(connection, database);
	     }
	     
	
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#validate(org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator, java.lang.String, org.eclipse.linuxtools.tmf.totalads.dbms.DBMS, java.lang.Boolean, org.eclipse.linuxtools.tmf.totalads.algorithms.IAlgorithmOutStream)
	 */
	@Override
	public  void validate (ITraceIterator trace, String database, DBMS connection, Boolean isLastTrace, IAlgorithmOutStream outStream) 
			throws TotalADSUIException, TotalADSDBMSException, TotalADSReaderException {
	  
		 validationTraceCount++;// count the number of traces
		 
		 //Integer []hammAnomalies=new Integer[maxWin];
	     Results result= evaluateTrace(trace, database, connection);
	   
	     if (result.getAnomaly()){
	    	 String details=result.getDetails().toString();
	    	 outStream.addOutputEvent(details);
	    	 validationAnomalies++;
	    	 
	     }
	    
	     if (isLastTrace){
	    	 
	    	outStream.addOutputEvent("Total traces in validation folder: "+validationTraceCount); 
	    	Double anomalyPrcentage=(validationAnomalies.doubleValue()/validationTraceCount.doubleValue())*100;
	    	outStream.addOutputEvent("Total anomalies at max hamming distance "+maxHamDis+ " are "+anomalyPrcentage);
	    	Double normalPercentage=(100-anomalyPrcentage);
	    	outStream.addOutputEvent("Total normal at max hamming distance "+maxHamDis+ " are "+normalPercentage);
	    	
	    	// Update the settings collection for maxwin and maxhamm
	    	 saveSettings(database, connection);
	    	 outStream.addOutputEvent("Database updated..");
	    	 
	    	 if (!warningMessage.isEmpty())
	    		 outStream.addOutputEvent(warningMessage);
	     }

	}
	
    
    
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#test(org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator, java.lang.String, org.eclipse.linuxtools.tmf.totalads.dbms.DBMS, org.eclipse.linuxtools.tmf.totalads.algorithms.IAlgorithmOutStream)
	 */
	@Override
	public Results test (ITraceIterator trace,  String database, DBMS connection, IAlgorithmOutStream outputStream) 
			throws TotalADSUIException, TotalADSDBMSException, TotalADSReaderException {
		  
	       if (!isTestStarted){
			  testTraceCount=0;
			  testAnomalies=0;
	    	  initialize(connection, database); // get the trees from db
			
			  isTestStarted=true;
			  nameToID.loadMap(connection, database);
			  testNameToIDSize=nameToID.getSize();
		  }
		  
		  return evaluateTrace(trace, database, connection);
		  
	}

	/**
	 * Evaluates a trace
	 * @param trace
	 * @param database
	 * @param connection
	 * @return
	 * @throws TotalADSReaderException 
	 */
	 private Results evaluateTrace(ITraceIterator trace,  String database, DBMS connection) throws TotalADSReaderException{
		     
		     int winWidth=0, anomalousSequencesToReturn=0, maxAnomalousSequencesToReturn=10;
		     int displaySeqCount=0, totalAnomalousSequences=0, largestHam=0;
		     Results results= new Results();
			 results.setAnomalyType("");
			 String headerMsg="First "+maxAnomalousSequencesToReturn+" or less anomalous sequences with non-overlapping  events at Ham:"+maxHamDis+"\n\n";
			 Integer []largestHamSeq=null;
			 testTraceCount++;
			  
		      LinkedList<Integer> newSequence=new LinkedList<Integer>();
		      //System.out.println(maxWin);
		      String event=null;
		      while (trace.advance()) {
		    	 
		    	  event=trace.getCurrentEvent();
		    	  newSequence.add(nameToID.getId(event));
		    	 
		    	  winWidth++;
		    	      	  
		    	  if(winWidth >= maxWin){
		    		  		
		    		  winWidth--;
		    		  
		    		  Integer[] seq=new Integer[maxWin];
		    		  seq=newSequence.toArray(seq);
		    		 // System.out.println(Arrays.toString(seq));
		    		 
		    		  int counter=0;
		    		 //Calculate the minimum hamming distance 
		    		 Integer hammDisForSequence=seq.length; // we assign hamming distance 
		    		 for (Map.Entry<Integer, Event[]> tree: sysCallSequences.entrySet()){
		    			 
		    			 Event[] nodes=tree.getValue();
		    			// just get the hamming and search with a full sequence
		    			 Integer hammDisForTree=treeTransformer.getHammingAndSearch(nodes, seq); 
			    	     if (hammDisForTree < hammDisForSequence)    
			    	    	   hammDisForSequence=hammDisForTree;
			    	     
		    		 }
		    		// If hamming distance is greater than the set threshold then it is an anomaly
			   		  if (hammDisForSequence > maxHamDis) {
			   			  totalAnomalousSequences++;
			   			 
			   			  if (headerMsg.length()>=1){
			   				  results.setAnomaly(true);
			   				  results.setDetails(headerMsg);
			   				  headerMsg="";
			   			  }
			   			 //Add a new sequence for display, when  all of previous events are gone
			   			  if (displaySeqCount <= maxAnomalousSequencesToReturn)
			   			  		if (anomalousSequencesToReturn % maxWin==0){ 
			   	    				  //Convert sequence in integer ids to name
			   	    				 StringBuilder seqName=new StringBuilder();
			   	    				 for (int i=0;i<seq.length;i++){
			   	    					 if (i==seq.length-1)
			   	    						 seqName.append(nameToID.getKey(seq[i]));
			   	    					 else
			   	    						 seqName.append(nameToID.getKey(seq[i])).append(" ");
			   	    				 }
			   	    				 seqName.append(":: Ham=").append(hammDisForSequence).append("\n");
			   	    				 //Add sequence to results
			   	    				 results.setDetails(seqName.toString());
			   	    			     displaySeqCount++;
			   	    			    //Get the sequence with the largest hamming distance 
			   	    			    
			   	    			 }
			   			  if (hammDisForSequence > largestHam){
			   			    	largestHam=hammDisForSequence;
			   			    	largestHamSeq=seq;
			   			    }
			   			  anomalousSequencesToReturn++;
			   		  }// End of ham comparison
		    		 
		    		
		    		  newSequence.remove(0);// remove the top event and slide a window
		    	  }
		    		  
		     }
		     
		    additionalInforForResults(largestHam, largestHamSeq, results, totalAnomalousSequences);
		  		
		    return results;  

		}
	 
	 
	 /**
	  * Adds additional information to the reuslts
	  * @param largestHam
	  * @param largestHamSeq
	  * @param results
	  * @param totalAnomalousSequences
	  */
	 private void additionalInforForResults(int largestHam, Integer [] largestHamSeq, Results results, int totalAnomalousSequences){
		  
		     if (results.getAnomaly()){
		    	 testAnomalies++;
		    	 results.setDetails("\n\nLargest Hamming distance: "+ largestHam+"\n");
		    	 results.setDetails("Last sequence with the largest Hamming distance:\n ");
		    	 for (int i=0;i<largestHamSeq.length;i++)
		    		 results.setDetails(nameToID.getKey(largestHamSeq[i])+" ");
		    	 results.setDetails("\n\nTotal anomalous sequences "+ totalAnomalousSequences);
		      }
		     
		     ////  get unknown events
		     if (nameToID.getSize() > testNameToIDSize){
				 Integer diff=nameToID.getSize()-testNameToIDSize;
				 int eventCount=0;
				 if (diff >10)
					 eventCount=testNameToIDSize+10;
				 else
					 eventCount=testNameToIDSize+diff;
				
				 results.setDetails("\n\nTen or less unknown events: \n");
				 int count=0;
				 for (int i=testNameToIDSize; i<eventCount;i++){// All these events are unknown
					   results.setDetails(nameToID.getKey(i)+" ");
					   count++;
					   if ((count)%10==0)
						   results.setDetails("\n");   
				 }
				 testNameToIDSize+=diff;//don't display this for the second trace unless or untill there are additional events

			 }
	 }
	
	/**
	 * Updates settings collection
	 * @param datatbase
	 * @param connection
	 * @throws TotalADSDBMSException 
	 */
	private void saveSettings(String database,DBMS connection) throws TotalADSDBMSException {
		  
		  String settingsKey ="SWN_SETTINGS";
		 
		  JsonObject jsonKey=new JsonObject();
		  jsonKey.addProperty("_id",settingsKey);
				  
		  JsonObject jsonObjToUpdate= new JsonObject();
		  jsonObjToUpdate.addProperty(SettingsCollection.KEY.toString(), settingsKey);
		  jsonObjToUpdate.addProperty(SettingsCollection.MAX_WIN.toString(), maxWin);
		  jsonObjToUpdate.addProperty(SettingsCollection.MAX_HAM_DIS.toString(), maxHamDis);
		
		  connection.insertOrUpdateUsingJSON(database, jsonKey, jsonObjToUpdate, SettingsCollection.COLLECTION_NAME.toString());
	 
		 
	}
	/**
	 * Loads settings into the class variables maxWin and maxHamDis
	 * @param database
	 * @param connection
	 */
	private void loadSetings(String database, DBMS connection){
		DBCursor cursor=connection.selectAll(database, SettingsCollection.COLLECTION_NAME.toString());
		if (cursor !=null){
			while (cursor.hasNext()){
				DBObject dbObject=cursor.next();
				maxWin=Integer.parseInt(dbObject.get(SettingsCollection.MAX_WIN.toString()).toString());
				maxHamDis=Integer.parseInt(dbObject.get(SettingsCollection.MAX_HAM_DIS.toString()).toString());
			}
			cursor.close();
		}
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#getTotalAnomalyPercentage()
	 */
	@Override
	public Double getTotalAnomalyPercentage() {
		Double anomalousPercentage=(testAnomalies.doubleValue()/testTraceCount.doubleValue())*100;
		return anomalousPercentage;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#graphicalResults(org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator)
	 */
	@Override
	public Chart graphicalResults(ITraceIterator traceIterator) {
		
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#createInstance()
	 */
	@Override
	public IDetectionAlgorithm createInstance() {
		
		return new SlidingWindow();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#getName()
	 */
	@Override
	public String getName() {

		return "Sliding Window (SWN)";
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#getAcronym()
	 */
	@Override
    public String getAcronym(){
    	
    	return "SWN";
    }
    
	/**
	 *  Self registration of the model with the modelFactory 
	 */
	public static void registerModel() throws TotalADSUIException{
		AlgorithmFactory modelFactory= AlgorithmFactory.getInstance();
		SlidingWindow sldWin=new SlidingWindow();
		modelFactory.registerModelWithFactory( AlgorithmTypes.ANOMALY,  sldWin);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#getDescription()
	 */
	@Override 
	public String getDescription(){
		return "SWN works by extracting sequences of length ‘n’ from a trace by sliding a window one event "
				+ "(e.g., system call) at a time. For example, for a trace having system calls “3, 6, 195, 195”, "
				+ "two sequences “3, 6, 195” and “6, 195, 195” of length 3 can be extracted. SWN extracts sequences "
				+ "from normal traces and then compares them against the sequences in an unknown trace. If a new "
				+ "sequence is found in an unknown trace then it is considers it as anomalous. The Hamming distance "
				+ "between sequences can be used to adjust the decision threshold to reduce false alarms; e.g., a "
				+ "sequence “3, 5, 195” is anomalous for above sequences but the mismatch occurs only at one "
				+ "position—i.e., a hamming distance difference of only one. If the minimum Hamming distance matching "
				+ "criterion is set to more than one, then it is a normal sequence. ";
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#isOnlineLearningSupported()
	 */
	@Override
	public boolean isOnlineLearningSupported() {
		
		return true;
	}
	
	
}

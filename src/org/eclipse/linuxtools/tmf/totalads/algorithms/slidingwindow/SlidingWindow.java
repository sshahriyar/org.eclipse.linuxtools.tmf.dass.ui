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

import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.algorithms.Results;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmTypes;
import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.ui.modeling.ProgressConsole;
import org.swtchart.Chart;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


/**
 * This class implements the Sliding Window algorithm
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p> 
 * 
 */
public class SlidingWindow implements IDetectionAlgorithm {
	 
	private String TRACE_COLLECTION="trace_data";//Configuration.traceCollection;
	private String SETTINGS_COLLECTION="settings";//Configuration.settingsCollection;
	private Integer maxWin=5;
	private Integer maxHamDis=0;
	private String warningMessage="";
	private HashMap<String, Event[]> sysCallSequences;
	private String []trainingOptions={"Max Win","5", "Max Hamming Distance","0"};
	private String []testingOptions={"Max Hamming Distance","0"};
	private Integer validationTraceCount=0;
	private Integer validationAnomalies=0;
	private Integer testTraceCount=0;
	private Integer testAnomalies=0;
	private Boolean intialize=false;
	private Boolean isTestStarted=false;
	private SlidingWindowTree treeTransformer;
	
	/**
	 * Constructor
	 **/
	public SlidingWindow() {
		sysCallSequences= new HashMap<String, Event[]>();
		treeTransformer=new SlidingWindowTree();
	}
	/**
	 * Initializes the model if already exists in the database
	 * @param connection DBMS object
	 * @param database	Database name
	 */
	private void initialize(DBMS connection,String database) {
		
			DBCursor cursor=connection.selectAll(database, this.TRACE_COLLECTION);
			if (cursor !=null){
				while (cursor.hasNext()){
					DBObject dbObject=cursor.next();
					Gson gson =new Gson();
					String key=dbObject.get("_id").toString();
					
					Event []event = gson.fromJson(dbObject.get("tree").toString(), Event[].class);
					sysCallSequences.put(key, event);
				}
			cursor.close();
			}
			// get the maxwin
			cursor=connection.selectAll(database, this.SETTINGS_COLLECTION);
			if (cursor !=null){
				while (cursor.hasNext()){
					DBObject dbObject=cursor.next();
					maxWin=Integer.parseInt(dbObject.get(SettingsCollection.MAX_WIN.toString()).toString());
					maxHamDis=Integer.parseInt(dbObject.get(SettingsCollection.MAX_HAM_DIS.toString()).toString());
				}
				cursor.close();
			}
	
	}
	
	/**
     * Returns the settings of an algorithm as option name at index i and value at index i+1
     * @return String[] Array of String
     */
    @Override
    public String[] getTrainingOptions(){
    		return trainingOptions;
    	
    }
    
    /**
     * 
     * Set the settings of an algorithm as option name at index i and value ate index i+1
     * @return String[] Array of String
     *
     */
    @Override
    public String[] getTestingOptions(String database, DBMS connection){
    	DBCursor cursor=connection.selectAll(database, this.SETTINGS_COLLECTION);
		if (cursor !=null){
			while (cursor.hasNext()){
				DBObject dbObject=cursor.next();
				//maxWin=Integer.parseInt(dbObject.get(SETTINGS_COLL_FIELDS.MAX_WIN).toString());
				maxHamDis=Integer.parseInt(dbObject.get(SettingsCollection.MAX_HAM_DIS.toString()).toString());
			}
			cursor.close();
		}
    	testingOptions[1]=maxHamDis.toString(); 		
    	return testingOptions;
    	
    	
    }
	/**
	 * Creates a database to store models
	 */
	@Override
	public void createDatabase(String databaseName, DBMS connection) throws TotalADSDBMSException{
		String []collectionNames={TRACE_COLLECTION, SETTINGS_COLLECTION};
		connection.createDatabase(databaseName, collectionNames);
	}
	
	
	/**
	 * 
	 * Trains the model by overriding a method from the interface {@link IDetectionAlgorithm}
	 * 
	 */
	@Override
	public void train (ITraceIterator trace, Boolean isLastTrace, String database, DBMS connection, ProgressConsole console, 
			String[] options, Boolean isNewDB)  throws TotalADSUIException, TotalADSDBMSException,TotalADSReaderException {
	    
		 if (!intialize){
			  validationTraceCount=0;
			  validationAnomalies=0;
			  int maxWinLimit=15;
	    	
	    	  // If the option name is the same and database has no model then take the maxwin from user
	    	  // else maxwin aleady exists in the database. We cannot change it
	    	  if ( options!=null){
	    		  try{
		    		  if (sysCallSequences.size()!=0)
		    			  warningMessage="Warning: window size was not changed because the model (database) already exists.";
		    		  else if (options[0].equals(this.trainingOptions[0]))
			    		  	maxWin=Integer.parseInt(options[1]);// on error exception will be thrown automatically
		    		  
		    		  //check for hamming distance
		    		  if (options[2].equals(this.trainingOptions[2]) )
			    		  	maxHamDis=Integer.parseInt(options[3]);// on error exception will be thrown automatically
	    		  
	    		  }catch (Exception ex){// Capturing exception to send a UI error
	    			  throw new TotalADSUIException("Please, enter integer numbers only in options.");
	    		  }
	    		  
	    		  if (maxWin > maxWinLimit)
	    			   throw new TotalADSUIException ("Sequence size too large; select "+maxWinLimit+" or lesser.");
	    	  }
	    		
	    	  intialize=true;
		    	 // Initializing 
		       	  	 if (isNewDB)
		    	  		  createDatabase(database, connection);
		    		 initialize(connection,database);
		    
	    		    	  
	      }
	    	  
	    	  
		  int  winWidth=0;
	      LinkedList<String> newSequence=new LinkedList<String>();
	     
	      while (trace.advance()) {
	    	  	    	
	    	  newSequence.add(trace.getCurrentEvent());
	    	 
	    	  winWidth++;
	    	      	  
	    	  if(winWidth >= maxWin){
	    		  		
	    		  winWidth--;
	    		  String[] seq=new String[maxWin];
	    		  seq=newSequence.toArray(seq);
	    		  // searching and adding to db
	    		  treeTransformer.searchAndAddSequence(seq,sysCallSequences);
	    		  
	    		  newSequence.remove(0);
	    	  }
	    		  
	     }
	     if (isLastTrace){ 
	    	 // Saving events tree in database
	    	 
	    	 treeTransformer.saveinDatabase(console, database, connection, sysCallSequences, TRACE_COLLECTION);
	    	 intialize=false;
	     }
	     
	
	}

	/**
	 * 
	 * Validates the model by overriding a method from the interface {@link IDetectionAlgorithm}
	 *
	 */
	@Override
	public  void validate (ITraceIterator trace, String database, DBMS connection, Boolean isLastTrace, ProgressConsole console) 
			throws TotalADSUIException, TotalADSDBMSException, TotalADSReaderException {
	  
		 validationTraceCount++;// count the number of traces
		 
		 //Integer []hammAnomalies=new Integer[maxWin];
	     Results result= evaluateTrace(trace, database, connection);
	   
	     if (result.getAnomaly()){
	    	 String details=result.getDetails().toString();
	    	 console.printTextLn(details);
	    	// Integer hamming=Integer.parseInt(details.split("::")[1]);
	    	 //hammAnomalies[hamming]++;
	    	 validationAnomalies++;
	    	 
	     }
	    
	     if (isLastTrace){
	    	 
	    	 //for (int hamCount=1; hamCount < hammAnomalies.length; hamCount++){
	    	//	    console.printTextLn("Anomalies at hamming "+ hamCount + ":" +hammAnomalies[hamCount]);
	    	//	    totalAnomalies+=hammAnomalies[hamCount];
	    	// }
	    	console.printTextLn("Total traces in validation folder: "+validationTraceCount); 
	    	Double anomalyPrcentage=(validationAnomalies.doubleValue()/validationTraceCount.doubleValue())*100;
	    	console.printTextLn("Total anomalies at max hamming distance "+maxHamDis+ " are "+anomalyPrcentage);
	    	Double normalPercentage=(100-anomalyPrcentage);
	    	console.printTextLn("Total normal at max hamming distance "+maxHamDis+ " are "+normalPercentage);
	    	
	    	// Update the settings collection for maxwin and maxhamm
	    	 saveSettings(database, connection);
	    	 console.printTextLn("Database updated..");
	    	 
	    	 if (!warningMessage.isEmpty())
	    		 console.printTextLn(warningMessage);
	     }

	}
	
    
    
	/**
	 * Tests the model by overriding a method from the interface {@link IDetectionAlgorithm}
	 */
	@Override
	public Results test (ITraceIterator trace,  String database, DBMS connection, String[] options) 
			throws TotalADSUIException, TotalADSDBMSException, TotalADSReaderException {
		  
	       if (!isTestStarted){
			  testTraceCount=0;
			  testAnomalies=0;
	    	  initialize(connection, database); // get the trees from db
			  if (options!=null && options[0].equals(this.testingOptions[0]) ){
	    		  	try {
	    		  		maxHamDis=Integer.parseInt(options[1]);
	    		  	}catch (NumberFormatException ex){
	    		  		throw new TotalADSUIException("Please, enter an integer value.");
	    		  	}
	    	        saveSettings(database, connection); // save maxHamm
	    	   }
			  isTestStarted=true;
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
	     int displaySeqCount=0;
	     Results results= new Results();
		 results.setAnomalyType("");
		 results.setAnomaly(false);
		 testTraceCount++;
		  
	      LinkedList<String> newSequence=new LinkedList<String>();
	      //System.out.println(maxWin);
	      while (trace.advance()) {
	    	 
	    	
	    	  newSequence.add(trace.getCurrentEvent());
	    	 
	    	  winWidth++;
	    	      	  
	    	  if(winWidth >= maxWin){
	    		  		
	    		  winWidth--;
	    		  
	    		  String[] seq=new String[maxWin];
	    		  seq=newSequence.toArray(seq);
	    		  
	    		  Event[] nodes=null;
	    		  int counter=0;
	    		 
	    		  do {// This loop searches for the tree that starts with the first event of a new sequences, 
	    			  // if not found it starts with a second event, then the third event and so on. Each time
	    			  //it keeps on increasing the hamming distnace
	    			   nodes=sysCallSequences.get(seq[counter]);
	    			   counter++;
	    		  }while (nodes==null && counter <seq.length);
	    		  
	    		  //Boolean isNormal=false;
	    		  Integer hammDis=counter-1; // we assign hamming distance 
	    		 
	    		  if (nodes!=null){ // if a tree has been found then we go inside this condition
	    			   
	    			  String []tmp;
	    			   
	    			    if (hammDis >= 1){// if we have found a tree that does not start with the 
	    			    				 // first event of a new sequence in the above loop then we pass the sequence 
	    			    				// from where it matched with the root of the tree in the above loop
	    			    	if (seq.length==counter){
	    			    		int a=0;
	    			    		a++;
	    			    	}
	    			    		
	    			    	tmp=new String[seq.length-(counter-1)];
	    			    	for (int i=counter-1; i<tmp.length;i++)
	    			    			tmp[i]=seq[i+1];
	    			    	hammDis=hammDis+treeTransformer.getHammingAndSearch(nodes, tmp)-1;// calculate hamming distance and subtract 1
	    			    	                                      // because one extra distance is reported from the function
	    			    										  // getHammingAndSearch
	    			    }
	    			    else	
	    			  	  hammDis=treeTransformer.getHammingAndSearch(nodes, seq); // just get the hamming and search with a full sequence
	    			     
	    		  }
	    		  //isNormal=searchMatchingSequenceInTree(nodes, seq);
	    		 // System.out.println(hammDis+ " "+maxHamDis);
	    		  if (hammDis > maxHamDis) {// It is not normal, it is actually an anomaly
	    			  results.setAnomaly(true);
	    			  
	    			  if (anomalousSequencesToReturn % maxWin==0){ // add a new sequence when the previous events are gone
	    				  results.setDetails(Arrays.toString(seq)+"::"+hammDis+"\n");
	    			      displaySeqCount++;
	    			  }
	    				   
	    			  anomalousSequencesToReturn++;
	    		  }
	    		  if (displaySeqCount >= maxAnomalousSequencesToReturn){
	    			  results.setDetails(".....\n....."+maxAnomalousSequencesToReturn+" or less"
	    			  		+ " randomly selected anomalies"+"\n");
	    			  break;// No need to extract all the anomalous sequences in a trace;
	    		                     // just return and break
	    		  }
	    		  newSequence.remove(0);// remove the top event and slide a window
	    	  }
	    		  
	     }
	     if (results.getAnomaly())
	    	 testAnomalies++;
	  		
	    return results;  

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
		
		  connection.insertOrUpdateUsingJSON(database, jsonKey, jsonObjToUpdate, this.SETTINGS_COLLECTION);
	 
		 
	}
	
	
	/** 
	 * Text Results, an overriden method
	 */
	@Override
	public String getSummaryOfTestResults() {
		Double anomalousPercentage=(testAnomalies.doubleValue()/testTraceCount.doubleValue())*100;
		return anomalousPercentage.toString()+"%";
	}

	/* 
	 * graphicalResults, an overriden method
	 */
	@Override
	public Chart graphicalResults() {
		
		return null;
	}

	/* 
	 * Creates Instance, an overriden method
	 */
	@Override
	public IDetectionAlgorithm createInstance() {
		
		return new SlidingWindow();
	}

	/** 
	 * Returns the name
	 */
	@Override
	public String getName() {

		return "Sliding Window (SWN)";
	}
	
	 /**
     * Returns the acronym of the model
     */
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
	
	
	/**
	 * Performs cross validation
	 */
	@Override
	public void crossValidate(Integer folds, String database, DBMS connection, ProgressConsole console, ITraceIterator trace) throws TotalADSUIException, TotalADSDBMSException{
		
	}
}

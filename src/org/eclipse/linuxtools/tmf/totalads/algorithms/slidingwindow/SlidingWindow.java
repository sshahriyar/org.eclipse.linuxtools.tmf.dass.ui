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
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.ui.diagnosis.ProgressConsole;
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
	/**Fields of settings collection (table in a traditional database)
	 */ 
	
	private static final class SETTINGS_COLL_FIELDS{
		static final String KEY="_id";
		static final String MAX_WIN="maxWIN";
		static final String MAX_HAM_DIS="maxHamDis";
	}
	
	HashMap<String, Event[]> sysCallSequences;
	
	String []trainingOptions={"Max Win","5", "Max Hamming Distance","0"};
	String []testingOptions={"Max Hamming Distance","0"};
		
    Integer validationTraceCount=0;
    Integer validationAnomalies=0;
    Integer testTraceCount=0;
    Integer testAnomalies=0;
	Boolean intialize=false;
	Boolean isTestStarted=false;
	/**
	 * Constructor
	 * 	 */
	public SlidingWindow() {
		sysCallSequences= new HashMap<String, Event[]>();
	}
	/**
	 * Initializes the model if already exists in the database
	 * @param connection
	 * @param database
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
					maxWin=Integer.parseInt(dbObject.get(SETTINGS_COLL_FIELDS.MAX_WIN).toString());
					maxHamDis=Integer.parseInt(dbObject.get(SETTINGS_COLL_FIELDS.MAX_HAM_DIS).toString());
				}
				cursor.close();
			}
	
	}
	
	/**
     * Returns the settings of an algorithm as option name at index i and value at index i+1
     * @return String[]
     */
    @Override
    public String[] getTrainingOptions(){
    		return trainingOptions;
    	
    }
    /**
     * Set the settings of an algorithm as option name at index i and value ate index i+1
     */
    @Override
    public String[] getTestingOptions(String database, DBMS connection){
    	DBCursor cursor=connection.selectAll(database, this.SETTINGS_COLLECTION);
		if (cursor !=null){
			while (cursor.hasNext()){
				DBObject dbObject=cursor.next();
				//maxWin=Integer.parseInt(dbObject.get(SETTINGS_COLL_FIELDS.MAX_WIN).toString());
				maxHamDis=Integer.parseInt(dbObject.get(SETTINGS_COLL_FIELDS.MAX_HAM_DIS).toString());
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
	
	/* 
	 * Trains the model
	 */
	
	@Override
	public void train (ITraceIterator trace, Boolean isLastTrace, String database, DBMS connection, ProgressConsole console, String[] options)  throws TotalADSUIException, TotalADSDBMSException {
	    
		 if (!intialize){
			  validationTraceCount=0;
			  validationAnomalies=0;
			  int maxWinLimit=15;
	    	  intialize=true;
	    	 // initializing 
	    	  try{
	    		  initialize(connection,database);
	    	  } catch (Exception ex){
	    		  throw new TotalADSDBMSException(ex.getMessage());
	    	  }
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
	    		  
	    		  }catch (Exception ex){
	    			  throw new TotalADSUIException("Please, enter integer numbers only in options.");
	    		  }
	    		  
	    		  if (maxWin > maxWinLimit)
	    			   throw new TotalADSUIException ("Sequence size too large; select "+maxWinLimit+" or lesser.");
	    	  }
	    		
	    		    	  
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
	    		  searchAndAddSequence(seq);
	    		  newSequence.remove(0);
	    	  }
	    		  
	     }
	     if (isLastTrace){ 
	    	 // Saving events tree in database
	    	 try{
	    		 saveinDatabase(console, database, connection);
	    	 } catch (Exception ex){
	    		 throw new TotalADSDBMSException(ex.getMessage());
	    	 }
	    	 intialize=false;
	     }
	     
	
	}

	/* 
	 * Validates the model
	 */
	@Override
	public  void validate (ITraceIterator trace, String database, DBMS connection, Boolean isLastTrace, ProgressConsole console) 
			throws TotalADSUIException, TotalADSDBMSException {
	  
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
	 * Tests the model
	 */
	@Override
	public Results test (ITraceIterator trace,  String database, DBMS connection, String[] options) throws TotalADSUIException, TotalADSDBMSException {
		  
	      
		
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
 * 
 * @param trace
 * @param database
 * @param connection
 * @return
 */
 private Results evaluateTrace(ITraceIterator trace,  String database, DBMS connection){
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
	    			    	hammDis=hammDis+getHammingAndSearch(nodes, tmp)-1;// calculate hamming distance and subtract 1
	    			    	                                      // because one extra distance is reported from the function
	    			    										  // getHammingAndSearch
	    			    }
	    			    else	
	    			  	  hammDis=getHammingAndSearch(nodes, seq); // just get the hamming and search with a full sequence
	    			     
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
		  jsonObjToUpdate.addProperty(SETTINGS_COLL_FIELDS.KEY, settingsKey);
		  jsonObjToUpdate.addProperty(SETTINGS_COLL_FIELDS.MAX_WIN, maxWin);
		  jsonObjToUpdate.addProperty(SETTINGS_COLL_FIELDS.MAX_HAM_DIS, maxHamDis);
			
	
		  connection.insertOrUpdateUsingJSON(database, jsonKey, jsonObjToUpdate, this.SETTINGS_COLLECTION);
	 
		 
	}
	
	
	/* 
	 * Text Results
	 */
	@Override
	public String getSummaryOfTestResults() {
		Double anomalousPercentage=(testAnomalies.doubleValue()/testTraceCount.doubleValue())*100;
		return anomalousPercentage.toString()+"%";
	}

	/* 
	 * graphicalResults
	 */
	@Override
	public Chart graphicalResults() {
		
		return null;
	}

	/* 
	 * Creates Instance
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
	 * Searches and adds a sequence in the training database. 
	 * If a sequence already exists, it updates the counter
	 * @param sequence sequence to add
	 */
	private void searchAndAddSequence(String []newSequence){
		Integer seqSize=newSequence.length;
		Event[] eventSequence= sysCallSequences.get(newSequence[0]);
		if (eventSequence==null){ // if there is no such starting event then add sequence to such an event
			
			eventSequence=new Event[seqSize+1];
			for (int j=0;j<seqSize;j++){
				eventSequence[j]=new Event();
				eventSequence[j].event=newSequence[j];
			}
			eventSequence[seqSize]=new Event();
			eventSequence[seqSize].event="1";
			
		}else{
			
			Boolean isFound=searchAndAppendSequence(eventSequence, newSequence);// search in graph from this node/sequence
			
			if (isFound==false){// if not found then add on 0 branch 
				eventSequence[0].branches=new ArrayList<Event[]>();
				Event[] newBranchSeq=new Event[seqSize+1];
				for (int j=0;j<seqSize-1;j++){
					newBranchSeq[j]=new Event();
					newBranchSeq[j].event=newSequence[j];
				}
				newBranchSeq[seqSize]=new Event();
				newBranchSeq[seqSize].event="1";
				eventSequence[0].branches.add(newBranchSeq);
			}
		}
		// putting the sequence (graph actually) to the starting event (node) as a key
		sysCallSequences.put(newSequence[0],eventSequence);
	}
	
	/**
	 * Searches and appends a sequence(set of events) to an already existing tree of events. 
	 * If a sequence already exists, it updates the counter 
	 * Use the following tree example to understand the code
	 * 
	 * 108-106-5-55-45
	 * 90
	 * |
	 * 3---9-6
	 * | |-3-10
	 * | |-3-6
	 * |
	 * 106
	 * |
	 * 5
	 * @param eventSeq an already existing tree of events 
	 * @param newSeq   a sequence of events that requires to be appended
	 * @return
	 */
	private Boolean searchAndAppendSequence(Event[] eventSeq, String []newSeq){
		
	Integer seqSize=newSeq.length;
	Integer j;
	for (j=0; j < seqSize;j++){
	 	
		if (!eventSeq[j].event.equals(newSeq[j]))
				break;
	}
	 
	Integer matchIdx=j-1;
	 
	if (matchIdx>=seqSize-1){
	 	Integer counter= Integer.parseInt(eventSeq[seqSize].event)+1;
	 	eventSeq[seqSize].event=counter.toString();
		return true;	
	}
	else if (matchIdx <0){
			return false; // return null if mismatch on the first idx
	}
	
	else {
		 Event []newEventSeq= new Event[seqSize-matchIdx];//+1 for the count
		 String [] newTmpSeq= new String [seqSize-matchIdx-1];
	     Boolean isFound=false;      
	     Integer i;
		 for ( i=0; i <newEventSeq.length-1; i++){
			 		newEventSeq[i]=new Event();
					newEventSeq[i].event=newSeq[matchIdx+i+1];// that is copy from the next index of matched index
					newTmpSeq[i]=newSeq[matchIdx+i+1];
		 }
		 newEventSeq[i]=new Event();
		 newEventSeq[i].event="1";// add 1 as a counter at the leaf
			
		ArrayList<Event[]> branches= eventSeq[matchIdx].branches;
	//      When there are no more branches then we shall automatically 
	//       add a new branch by skipping the if block
		if (branches!=null){
	          //if the branches exist then we need to recursively go through the remaining branches to find 
				// a possible location to append the new sequence
				
				for (int bCount=0; bCount<branches.size(); bCount++){
					Event []branchEventSeq=branches.get(bCount);
					/// ****** recursive call
					isFound=searchAndAppendSequence(branchEventSeq,newTmpSeq);
					/// ****** recursive call
					if (isFound==true){//{// there is no need to iterate more branches, we have found a match
						//branches.set(bCount, branchEventSeq);
						//branches.add(returnSeq);
						break;
					}
		        }
		}else{
			branches= new ArrayList<Event[]>();
		}
	    //We have just found out where to append a branch in the graph
	 	  //add a new branch to the event and return the eventSeq.   
		
		if (isFound==false)
			branches.add(newEventSeq);
	
		eventSeq[matchIdx].branches=branches;
		return true;
		   
	}
	// End of function searchAndAddSequence
   }
	/**
	 * This function saves the model in the database by converting HashMap to JSON and serializing in MongoDB
	 * @param console
	 * @param database
	 * @param connection
	 * @throws Exception
	 */
	private void saveinDatabase(ProgressConsole console, String database, DBMS connection) throws TotalADSDBMSException{
		console.printTextLn("Saving in database.....");
		for(Map.Entry<String, Event[]>nodes:  sysCallSequences.entrySet()){
			
					
			Event []events=nodes.getValue(); 

			
			com.google.gson.Gson gson = new com.google.gson.Gson();
			
			JsonElement jsonArray= gson.toJsonTree(events);
			JsonObject jsonObject= new JsonObject();
			jsonObject.addProperty("_id", nodes.getKey());
			jsonObject.add("tree", jsonArray);
			
			JsonObject jsonKey=new JsonObject();
			jsonKey.addProperty("_id", nodes.getKey());
			
			//console.printTextLn(jsonObject.toString());
			connection.insertOrUpdateUsingJSON(database, jsonKey, jsonObject, this.TRACE_COLLECTION);
			
			
		
		
			
		}
	}
	/**
	 * Prints the graph of sequence
	 * @param console
	 */
	private void printSequence(ProgressConsole console, String database) {
		for(Map.Entry<String, Event[]>nodes:  sysCallSequences.entrySet()){
			// create root: nodes.getKey
			
			//console.printTextLn(JSONserialize(events[0]));
			printRecursive(nodes.getValue(),"", console);
		/*
		 Event []events=nodes.getValue(); 
		 DBMS db=Configuration.connection;
		
		com.google.gson.Gson gson = new com.google.gson.Gson();
		
		JsonElement jsonArray= gson.toJsonTree(events);
		JsonObject jsonObject= new JsonObject();
		jsonObject.addProperty("_id", nodes.getKey());
		jsonObject.add("tree", jsonArray);
		
		JsonObject jsonKey=new JsonObject();
		jsonKey.addProperty("_id", nodes.getKey());//_id
		
		console.printTextLn(jsonObject.toString());
		DBMS connection=Configuration.connection;
		
		//connection.insertUsingJSON(database, jsonObject, this.TRACE_COLLECTION);
		try {
			connection.insertOrUpdateUsingJSON(database, jsonKey, jsonObject, this.TRACE_COLLECTION);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		System.out.println(jsonObject.toString());
		
		*/
			
		}
		
	}
	/**
	 * This function goes through the tree of Events and print the sequences in a human readable
	 * form
	 * @param nodes
	 * @param prefix
	 * @param console
	 */
	private void printRecursive(Event[] nodes,String prefix, ProgressConsole console){
	      
		Boolean isPrefixPrinted=false;
 		   for (int nodeCount=0; nodeCount<nodes.length; nodeCount++){
			      
				  ArrayList<Event[]> branches=nodes[nodeCount].branches;
				  if (nodeCount==nodes.length-1)
						prefix=prefix+"-:"+nodes[nodeCount].event;// the last element is the count of the sequence
				  else	
				  		prefix=prefix+nodes[nodeCount].event+"-";// just append the events
				  		//create a two dimesnion key Tree as an array and node as event name 
				  
				  if (branches!= null){ // if there are branches on an event then keep
					 
					 for(int i=0;i<branches.size(); i++){
							printRecursive(branches.get(i),prefix,console);
					   }   
				  } else {
				       // create tee withnull
				    	// Print only when we reach a leaf of a branch
				    	if (nodeCount==nodes.length-1)
				    		   		console.printText(prefix);
				    	
				    		//console.printText(nodes[nodeCount].event+"-");
				}
		}
	  	console.printNewLine();	    
	}

	/**
	 * Searches a matching sequence in the tree
	 * @param eventSeq Tree
	 * @param newSeq New sequence
	 * @return
	 */
	private Boolean  searchMatchingSequenceInTree(Event[] eventSeq, String []newSeq){
		
		Integer seqSize=newSeq.length;
		Integer j;
		for (j=0; j < seqSize;j++){
		 	if (!eventSeq[j].event.equals(newSeq[j]))
					break;
		}
		 
		Integer matchIdx=j-1;
		 
		if (matchIdx>=seqSize-1)
		 			return true;	
		else if (matchIdx <0)
				return false; // return null if mismatch on the first idx 
		
		else {
			 //Event []newEventSeq= new Event[seqSize-matchIdx];//+1 for the count
			 String [] newTmpSeq= new String [seqSize-matchIdx-1];
		     Boolean isFound=false;      
		     Integer i;
			 for ( i=0; i <newTmpSeq.length-1; i++){
				 			// that is copy from the next index of matched index
						newTmpSeq[i]=newSeq[matchIdx+i+1];
			 }
			 	
			ArrayList<Event[]> branches= eventSeq[matchIdx].branches;
		//      When there are no more branches then we shall automatically 
		//       add a new branch by skipping the if block
			if (branches!=null){
		          //if the branches exist then we need to recursively go through the remaining branches to find 
					// a possible location to append the new sequence
					
					for (int bCount=0; bCount<branches.size(); bCount++){
						Event []branchEventSeq=branches.get(bCount);
						/// ****** recursive call
						isFound=searchMatchingSequenceInTree(branchEventSeq,newTmpSeq);
						/// ****** recursive call
						if (isFound==true)//{// there is no need to iterate more branches, we have found a match
							break;
						
			        }
			}
			
			if (isFound)
				return true;
			else
				return false;
			   
		}
		// End of function searchMatchingSequence
	   }
	
	/**
	 * Searches a new sequence in the tree and returns the Hamming distance.
	 * if Hamming distance is zero then a sequence matched  otherwise  Hamming
	 * distance is equal to the number of mismatches
	 * @param nodes Tree of events
	 * @param newSeq Sequence to search
	 */
	private Integer getHammingAndSearch(Event[] nodes,String []newSeq){
	      
		Boolean isPrefixPrinted=false;
		Integer hammDis=0, minHammDis=100000000;//initializing minimum hamming distance with a very large number
 		
		for (int nodeCount=0; nodeCount<newSeq.length; nodeCount++){
			      
				  ArrayList<Event[]> branches=nodes[nodeCount].branches;
				  if (!nodes[nodeCount].event.equals(newSeq[nodeCount]))	
				  		 hammDis++;
				  		
				  
				  if (branches!= null){ // if there are branches on an event then keep
					  	 String [] newTmpSeq= new String [newSeq.length-nodeCount-1];
					     for ( int i=0; i <newTmpSeq.length; i++)
					    	 newTmpSeq[i]=newSeq[nodeCount+i+1];// that is copy from the next index of matched index
									
						
					     for(int i=0;i<branches.size(); i++){
						    	Integer branchHamming= getHammingAndSearch(branches.get(i),newTmpSeq);
						    	if (branchHamming==0){ // there is no need to hceck further branches
						    		minHammDis=0;             // we have found a match, as hamming is 0
						    		break;
						    	}
						    	else{
						    		 if (branchHamming <minHammDis)
						    			 minHammDis=branchHamming;
						    	}
				 	      }   
				  } 
		}
		
		if (hammDis<minHammDis)
			minHammDis=hammDis;
	    
		return minHammDis;		    
	}

	/**
	 * Performs cross validation
	 */
	@Override
	public void crossValidate(Integer folds, String database, DBMS connection, ProgressConsole console, ITraceIterator trace) throws TotalADSUIException, TotalADSDBMSException{
		
	}
}

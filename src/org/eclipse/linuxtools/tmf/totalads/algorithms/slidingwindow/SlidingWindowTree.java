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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.ui.ProgressConsole;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * This class implements the tree transformation methods for the sliding window algorithm. it converts a trace of events
 * into a tree of patterns and serializes to database when needed.
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p> 
 * 
 */
public class SlidingWindowTree {
	/**
	 * Constructor
	 */
	public SlidingWindowTree() {
		
	}
	
	/**
	 * Searches and adds a sequence in the training database. 
	 * If a sequence already exists, it updates the counter
	 * @param newSequence
	 * @param database
	 * @param connection
	 * @throws TotalADSDBMSException
	 */
	public void searchAndAddSequence(String []newSequence, HashMap<String, Event[]> sysCallSequences){
  //  public void searchAndAddSequence(String []newSequence, String database, DBMS connection) throws TotalADSDBMSException{
		Integer seqSize=newSequence.length;
		Event[] eventSequence= sysCallSequences.get(newSequence[0]);//load from database
		//Event[] eventSequence=	loadTreeFromDatabase(newSequence[0], database, connection) ;//load from database
		
		if (eventSequence==null){ // if there is no such starting event then add sequence to such an event
			
			eventSequence=new Event[seqSize+1];
			for (int j=0;j<seqSize;j++){
				eventSequence[j]=new Event();
				eventSequence[j].setEvent(newSequence[j]);
			}
			eventSequence[seqSize]=new Event();
			eventSequence[seqSize].setEvent("1");
			
		}else{
			
			Boolean isFound=searchAndAppendSequence(eventSequence, newSequence);// search in tree from this node/sequence
			
			if (isFound==false){// if not found then add on 0 branch 
				eventSequence[0].setBranches(new ArrayList<Event[]>());
				Event[] newBranchSeq=new Event[seqSize+1];
				for (int j=0;j<seqSize-1;j++){
					newBranchSeq[j]=new Event();
					newBranchSeq[j].setEvent(newSequence[j]);
				}
				newBranchSeq[seqSize]=new Event();
				newBranchSeq[seqSize].setEvent("1");
				eventSequence[0].getBranches().add(newBranchSeq);
			}
		}
		// putting the sequence (graph actually) to the starting event (node) as a key
		sysCallSequences.put(newSequence[0],eventSequence);//add to database
		//saveTreeInDatabase( database, connection, eventSequence, TraceCollection.COLLECTION_NAME.toString());
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
	 	
		if (!eventSeq[j].getEvent().equals(newSeq[j]))
				break;
	}
	 
	Integer matchIdx=j-1;
	 
	if (matchIdx>=seqSize-1){
	 	Integer counter= Integer.parseInt(eventSeq[seqSize].getEvent())+1;
	 	eventSeq[seqSize].setEvent(counter.toString());
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
					newEventSeq[i].setEvent(newSeq[matchIdx+i+1]);// that is copy from the next index of matched index
					newTmpSeq[i]=newSeq[matchIdx+i+1];
		 }
		 newEventSeq[i]=new Event();
		 newEventSeq[i].setEvent("1");// add 1 as a counter at the leaf
			
		ArrayList<Event[]> branches= eventSeq[matchIdx].getBranches();
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
	
		eventSeq[matchIdx].setBranches(branches);
		return true;
		   
	}
	// End of function searchAndAddSequence
   }
	
	/**
	 * This function saves the model in the database by converting HashMap to JSON and serializing in MongoDB
	 * @param console ProgressConsole object
	 * @param database Database name
	 * @param connection DBMS object
	 * @param sysCallSequences  A map containing one tree of events for every key
	 * @param collectionName Collection name
	 * @throws TotalADSDBMSException
	 */
	public void saveinDatabase(ProgressConsole console, String database, DBMS connection,HashMap<String, 
			Event[]> sysCallSequences, String collectionName) throws TotalADSDBMSException{
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
			connection.insertOrUpdateUsingJSON(database, jsonKey, jsonObject, collectionName);
			
		}
	}
	
	/**
	 * Saves a tree in the database
	 * @param database
	 * @param connection
	 * @param sysCallSequences
	 * @param collectionName
	 * @throws TotalADSDBMSException
	 */
	public void saveTreeInDatabase( String database, DBMS connection, 
			Event[] tree, String collectionName) throws TotalADSDBMSException{
		
		 
		
		 	String key=tree[0].getEvent(); // top node is the key
			
			com.google.gson.Gson gson = new com.google.gson.Gson();
			
			JsonElement jsonArray= gson.toJsonTree(tree);
			JsonObject jsonObject= new JsonObject();
			jsonObject.addProperty("_id", key);
			jsonObject.add("tree", jsonArray);
			
			JsonObject jsonKey=new JsonObject();
			jsonKey.addProperty("_id", key);
			
			//console.printTextLn(jsonObject.toString());
			connection.insertOrUpdateUsingJSON(database, jsonKey, jsonObject, collectionName);
		
	}
	/**
	 * Loads a tree based on the root node from the database
	 * @param rootNode
	 * @param database
	 * @param connection
	 * @return
	 */
	public Event[] loadTreeFromDatabase(String rootNode ,String database, DBMS connection){
		DBCursor cursor=connection.select("_id", "", rootNode, database, TraceCollection.COLLECTION_NAME.toString());
		Event []event=null;
		 if (cursor !=null){
			
				DBObject dbObject=cursor.next();
				Gson gson =new Gson();
				event = gson.fromJson(dbObject.get("tree").toString(), Event[].class);
			
				cursor.close();
		}
		 return event;
	}
	/**
	 * Prints the graph of sequence; use for testing
	 * @param console ProgressConsole object
	 * @param sysCallSequences  A map containing one tree of events for every key
	 */
	public void printSequence(ProgressConsole console, String database, HashMap<String, Event[]> sysCallSequences) {
		for(Map.Entry<String, Event[]>nodes:  sysCallSequences.entrySet()){
			// create root: nodes.getKey
			
			//console.printTextLn(JSONserialize(events[0]));
			printRecursive(nodes.getValue(),"", console);
			}
		
	}
	/**
	 * This function goes through the tree of Events and print the sequences in a human readable
	 * form.
	 * @param nodes Root event
	 * @param prefix Prefix of the event sequence
	 * @param console ProgressConsole object
	 */
	private void printRecursive(Event[] nodes,String prefix, ProgressConsole console){
	      
		Boolean isPrefixPrinted=false;
 		   for (int nodeCount=0; nodeCount<nodes.length; nodeCount++){
			      
				  ArrayList<Event[]> branches=nodes[nodeCount].getBranches();
				  if (nodeCount==nodes.length-1)
						prefix=prefix+"-:"+nodes[nodeCount].getEvent();// the last element is the count of the sequence
				  else	
				  		prefix=prefix+nodes[nodeCount].getEvent()+"-";// just append the events
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
	 * @return True if a sequence matches, else false
	 */
	public Boolean  searchMatchingSequenceInTree(Event[] eventSeq, String []newSeq){
		
		Integer seqSize=newSeq.length;
		Integer j;
		for (j=0; j < seqSize;j++){
		 	if (!eventSeq[j].getEvent().equals(newSeq[j]))
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
			 	
			ArrayList<Event[]> branches= eventSeq[matchIdx].getBranches();
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
	public Integer getHammingAndSearch(Event[] nodes,String []newSeq){
	      
		
		Integer hammDis=0, minHammDis=100000000;//initializing minimum hamming distance with a very large number
 		
		for (int nodeCount=0; nodeCount<newSeq.length; nodeCount++){
			      
				  ArrayList<Event[]> branches=nodes[nodeCount].getBranches();
				  if (!nodes[nodeCount].getEvent().equals(newSeq[nodeCount]))	
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

// End of class
}

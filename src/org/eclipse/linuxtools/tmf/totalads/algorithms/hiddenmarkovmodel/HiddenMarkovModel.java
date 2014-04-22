/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.algorithms.hiddenmarkovmodel;

import java.util.Arrays;
import java.util.LinkedList;

import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmTypes;
import org.eclipse.linuxtools.tmf.totalads.algorithms.Results;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.ui.ProgressConsole;
import org.swtchart.Chart;
/**
 * This class implements a Hidden Markov Model
 * @author <p>Syed Shariyar Murtaza justsshary@hotmail.com</p>
 * 
 */
public class HiddenMarkovModel implements IDetectionAlgorithm {
	//private String []trainingSettings;
	private int seqLength;
	private  HmmMahout hmm;
	private NameToIDMapper nameToID;
	   private boolean isTrainIntialized=false;
	   private int numStates, numSymbols;
	/**
	 * Constructor
	 */
	public HiddenMarkovModel() {
		
		nameToID=new NameToIDMapper();		
		
	}

	/**
	 *  Self registration of the model with the modelFactory 
	 */
	public static void registerModel() throws TotalADSUIException{
		AlgorithmFactory modelFactory= AlgorithmFactory.getInstance();
		HiddenMarkovModel hmm=new HiddenMarkovModel();
		modelFactory.registerModelWithFactory( AlgorithmTypes.ANOMALY,  hmm);
	}
	/**
	 * Creates database
	 */
	@Override
	public void createDatabase(String databaseName, DBMS connection)throws TotalADSDBMSException {
		
		
		String []settings={ SettingsCollection.KEY.toString(),"hmm",
							SettingsCollection.NUM_STATES.toString(),"5",
						    SettingsCollection.NUM_SYMBOLS.toString(), "100"
						   ,SettingsCollection.SEQ_LENGTH.toString(),"20",
							SettingsCollection.PROBABILITY_THRESHOLD.toString(),"1.0"};
		
		try {
			
			hmm.verifySaveSettingsCreateDb(settings, databaseName, connection,true,true);
		} catch (TotalADSUIException e) {}

	}
	/**
	 * Saves Training options
	 * @param options
	 * @param database
	 * @param connection
	 * @throws TotalADSUIException
	 * @throws TotalADSDBMSException
	 */
	@Override
	public void saveTrainingOptions(String [] options, String database, DBMS connection) throws TotalADSUIException, TotalADSDBMSException
	{
		
		hmm.verifySaveSettingsCreateDb(options, database, connection,false,false);
		
	}
	/**
	 * Gets training settings
	 * @return
	 */
	@Override
	public String[] getTrainingOptions(DBMS connection, String database, Boolean isNewDatabase) {
		String [] trainingSettings=new String[6];
		trainingSettings[0]=SettingsCollection.NUM_STATES.toString();
		trainingSettings[1]="5";
		trainingSettings[2]= SettingsCollection.NUM_SYMBOLS.toString();
		trainingSettings[3]="350";
		trainingSettings[4]=SettingsCollection.SEQ_LENGTH.toString();
		trainingSettings[5]="20";
		return trainingSettings;
	}
	/**
	 * Gets Test settings
	 * @param database
	 * @param connection
	 * @return  An array of String 
	 */
	@Override
	public String[] getTestingOptions(String database, DBMS connection) {
		
		String []settings=hmm.loadSettings(database, connection);
		if (settings==null)
			return null;
		
		String []testingSettings=new String[4];
		testingSettings[0]=SettingsCollection.SEQ_LENGTH.toString();
		testingSettings[1]=settings[5];// seq length
		testingSettings[2]=SettingsCollection.PROBABILITY_THRESHOLD.toString();
		testingSettings[3]=settings[7]; // probaility
		return testingSettings;
	}
	/**
	 * Saves test settings
	 * @param options
	 * @param database
	 * @param connection
	 * @throws TotalADSUIException
	 * @throws TotalADSDBMSException
	 */
   @Override
	public void saveTestingOptions(String [] options, String database, DBMS connection) throws TotalADSUIException, TotalADSDBMSException
	{ 
	   
	   hmm.verifySaveSettingsCreateDb(options, database, connection,false,false);
	}
 
  /**
   * 
   * Trains an Hmm
   */

   @Override
	public void train(ITraceIterator trace, Boolean isLastTrace, String database, DBMS connection, ProgressConsole console,
		String[] options, Boolean isNewDB) throws TotalADSUIException, TotalADSDBMSException, TotalADSReaderException {
		
	    if (!isTrainIntialized){
				 hmm=new HmmMahout();
								 
				 if (options!=null){
					 if (isNewDB){// add all settings to the db if this is a new database
						 String []setting=new String[options.length+4];
						 setting[0]=SettingsCollection.KEY.toString(); 
						 setting[1]="hmm";
						 for (int i=0;i<options.length;i++)
							 setting[i+2]=options[i];
						 setting[options.length+2]=SettingsCollection.PROBABILITY_THRESHOLD.toString();
						 setting[options.length+3]="1.0";
						 
						 hmm.verifySaveSettingsCreateDb(setting, database, connection,true,true);
					 }else
						 hmm.verifySaveSettingsCreateDb(options, database, connection,false,false);
				 }					 
				 else{
					 if (isNewDB) 
						createDatabase(database, connection); // with default settings
					 options=hmm.loadSettings(database, connection);// get settings from db
				 } 
				 numStates=Integer.parseInt(options[1]);
				 numSymbols=Integer.parseInt(options[3]);
				 seqLength=Integer.parseInt(options[5]);
				 if (isNewDB)
					 hmm.initializeHMM(numSymbols, numStates);
				 else
					 hmm.loadHmm(connection, database);	
				 nameToID.loadMap(connection, database);
				 isTrainIntialized=true;
		 }
	   
		 int numIterations=10;
		 console.printTextLn("Extracting sequence");
		 
		 int winWidth=0,seqCount=0;
		 LinkedList<Integer> newSequence=new LinkedList<Integer>();
	   	 Boolean isTrained=true;
		 while (trace.advance()) {
			 trace.getCurrentEvent();	
	    	  newSequence.add(nameToID.getId(trace.getCurrentEvent()));
	    	 // newSequence.add(1);
	    	  winWidth++;
	    	  isTrained=false; 	  
	    	  if(winWidth >= seqLength){
	    		  isTrained=true;	
	    		  winWidth--;
	    		  Integer[] seq=new Integer[seqLength];
	    		  seq=newSequence.toArray(seq);
	    		 // console.printTextLn("Seq: "+Arrays.toString(seq));
	    		  // searching and adding to db
	    		  //if (seqCount==0) 
	    			//  hmm.generateSequences(seq, false);
	    		 // else
	    			//  hmm.generateSequences(seq, true);
	    		  if (seqCount==0){
	    		     console.printTextLn("Learning using BaumWelch algorithm");
	    		     console.printTextLn(Integer.toString(seqLength));
	    		  }
	    		  hmm.learnUsingBaumWelch(numIterations, seq);
	    		  newSequence.remove(0);
	    		  seqCount++;
	    		  if (seqCount%100==0)
	    			  console.printTextLn("Learning on "+seqCount+"th sequence");
	 	    	      
	    		  
	    	  }
	    	  
	    		  
	     }
		 if (isTrained==false){// train on the last missing sequences in the previous loop
			 Integer[] seq=new Integer[newSequence.size()];
   		     seq=newSequence.toArray(seq);
   		     console.printTextLn("Learning on the last sequence using BaumWelch algorithm");
   		     hmm.learnUsingBaumWelch(numIterations, seq);
		 }
		 
	     if (isLastTrace){ 
	    	     numSymbols=nameToID.getSize();
	    	     //hmm.initializeHMM(numSymbols, numStates);
	 		     console.printTextLn("Training finished..");
	    	     //hmm.learnUsingBaumWelch(numIterations);
	    	  	 console.printTextLn("Saving HMM");
	    	  	 console.printTextLn(hmm.printHMM());
	    	  	 hmm.saveHMM(database, connection);
	    	  	 nameToID.saveMap(connection, database);
	     }
		 
		
	}
	/**
	 * Validates HMM
	 * @param trace
	 * @param database
	 * @param connection
	 * @param isLastTrace
	 * @param console
	 * @throws TotalADSUIException
	 * @throws TotalADSDBMSException
	 * @throws TotalADSReaderException 
	 */
	@Override
	public void validate(ITraceIterator trace, String database,DBMS connection, 
			Boolean isLastTrace, ProgressConsole console) throws TotalADSUIException, TotalADSDBMSException, TotalADSReaderException {
		
		int winWidth=0;
		String []options=hmm.loadSettings(database, connection);
		Double probThreshold=Double.parseDouble(options[7]);
		LinkedList<Integer> newSequence=new LinkedList<Integer>();
	   	console.printTextLn("Starting validation"); 
		while (trace.advance()) {
	    	  	    	
	    	  newSequence.add(nameToID.getId(trace.getCurrentEvent()));
	    	 
	    	  winWidth++;
	    	      	  
	    	  if(winWidth >= seqLength){
	    		  		
	    		  winWidth--;
	    		  Integer[] seq=new Integer[seqLength];
	    		  seq=newSequence.toArray(seq);
	    		  // searching and adding to db
	    		   
	    		  double prob=1.0;
	    		  try{
	    			  prob=hmm.observationLikelihood(seq);
	    		  } catch (Exception ex){
	    			  console.printTextLn("Unknown events in seq: "+Arrays.toString(seq));
	    		  }
	    		  
	    		  if (prob<probThreshold){
	    			  probThreshold=prob;
	    			  //console.printTextLn(Arrays.toString(seq));
	    			  console.printTextLn("Min Threshold: "+probThreshold.toString());
	    			 
	    		  }
	    		  newSequence.remove(0);
	    	
	    	  }
	    		  
	     }
		options[7]=probThreshold.toString();
		console.printTextLn("Final Min Threshold: "+probThreshold.toString());
		 
		hmm.verifySaveSettingsCreateDb(options, database, connection,false,false); 
		if (isLastTrace)
			nameToID.saveMap(connection, database);
	}
	/**
	 * Tests an HMM
	 */
	@Override
	public Results test(ITraceIterator trace, String database, DBMS connection,
			String[] options) throws TotalADSUIException, TotalADSDBMSException {
		throw new TotalADSUIException("HmmJahmm is not implemented yet");
		
	}
	/**
	 * Cross Validation
	 */
	@Override
	public void crossValidate(Integer folds, String database, DBMS connection,
			ProgressConsole console, ITraceIterator trace, Boolean isLastTrace) throws TotalADSUIException, TotalADSDBMSException {
	

	}
	/**
	 * Returns the total anomalies found during testing
	 */
	@Override
	public Double getTotalAnomalyPercentage() {
	
		return null;
	}

	@Override
	public Chart graphicalResults(ITraceIterator traceIterator) {
	
		return null;
	}

	@Override
	public IDetectionAlgorithm createInstance() {
	
		return new HiddenMarkovModel();
	}

	@Override
	public String getName() {
		
		return "Hidden Markov Model (HMM)";
	}

	@Override
	public String getAcronym() {
		
		return "HMM";
	}

}

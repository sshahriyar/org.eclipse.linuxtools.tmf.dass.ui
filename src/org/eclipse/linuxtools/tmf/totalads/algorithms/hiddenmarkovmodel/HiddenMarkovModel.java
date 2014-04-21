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

import java.util.LinkedList;

import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmTypes;
import org.eclipse.linuxtools.tmf.totalads.algorithms.Results;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
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
	private String []trainingSettings;

	/**
	 * Constructor
	 */
	public HiddenMarkovModel() {
		trainingSettings=new String[6];
		trainingSettings[0]=SettingsCollection.NUM_STATES.toString();
		trainingSettings[1]="5";
		trainingSettings[2]= SettingsCollection.NUM_SYMBOLS.toString();
		trainingSettings[3]="350";
		trainingSettings[4]=SettingsCollection.SEQ_LENGTH.toString();
		trainingSettings[5]="50";
				
		
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
		String []collectionNames={HmmModelCollection.COLLECTION_NAME.toString(), SettingsCollection.COLLECTION_NAME.toString()
				                  , NameToIDCollection.COLLECTION_NAME.toString() };
		
		connection.createDatabase(databaseName, collectionNames);
		String []settings={ SettingsCollection.KEY.toString(),"_id",
							SettingsCollection.NUM_STATES.toString(),"0",
						    SettingsCollection.NUM_SYMBOLS.toString(), "0"
						   ,SettingsCollection.SEQ_LENGTH.toString(),"0",
							SettingsCollection.PROBABILITY_THRESHOLD.toString(),"0.0"};
		
		try {
			HMM hmm=new HMM();
			hmm.saveSettings(settings, databaseName, connection);
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
		HMM hmm=new HMM();
		hmm.saveSettings(options, database, connection);
		
	}
	/**
	 * Gets training settings
	 * @return
	 */
	@Override
	public String[] getTrainingOptions() {
		
		return trainingSettings;
	}
	/**
	 * Gets Test settings
	 * @param database
	 * @param connection
	 * @return
	 */
	@Override
	public String[] getTestingOptions(String database, DBMS connection) {
		HMM hmm =new HMM();
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
	   HMM hmm=new HMM();
	   hmm.saveSettings(options, database, connection);
	}
   /**
    * tRains an HMM
    * @param trace
    * @param isLastTrace
    * @param database
    * @param connection
    * @param console
    * @param options
    * @param isNewDB
    * @throws TotalADSUIException
    */
   private int seqLength;
   private  HMM hmm;
	@Override
	public void train(ITraceIterator trace, Boolean isLastTrace,
			String database, DBMS connection, ProgressConsole console,
			String[] options, Boolean isNewDB) throws TotalADSUIException, TotalADSDBMSException {
		
		 hmm=new HMM();
		 if (isNewDB)
			 createDatabase(database, connection);
		 
		 if (options!=null)
			 hmm.saveSettings(options, database, connection);
		 else{
			 if (isNewDB) options=trainingSettings;
			 else options=hmm.loadSettings(database, connection);
		 } 
		 int numStates=Integer.parseInt(options[1]);
		 int numSymbols=Integer.parseInt(options[3]);
		 seqLength=Integer.parseInt(options[5]);
		 int numIterations=10;
		 hmm.initializeHMM(numSymbols, numStates);
		
		 int winWidth=0,seqCount=0;
		 LinkedList<String> newSequence=new LinkedList<String>();
	   	 
		 while (trace.advance()) {
	    	  	    	
	    	  newSequence.add(trace.getCurrentEvent());
	    	 
	    	  winWidth++;
	    	      	  
	    	  if(winWidth >= seqLength){
	    		  		
	    		  winWidth--;
	    		  String[] seq=new String[seqLength];
	    		  seq=newSequence.toArray(seq);
	    		  // searching and adding to db
	    		  if (seqCount==0) 
	    			  hmm.generateSequences(seq, false);
	    		  else
	    			  hmm.generateSequences(seq, true);
	    	
	    		  newSequence.remove(0);
	    		  seqCount++;
	    	  }
	    		  
	     }
	     if (isLastTrace){ 
	    	  	 hmm.learnUsingBaumWelch(numIterations);
	    	  	 hmm.saveHMM(database, connection);
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
	 */
	@Override
	
	public void validate(ITraceIterator trace, String database,
			DBMS connection, Boolean isLastTrace, ProgressConsole console)
			throws TotalADSUIException, TotalADSDBMSException {
		
		int winWidth=0;
		String []options=hmm.loadSettings(database, connection);
		Double probThreshold=Double.parseDouble(options[7]);
				
		LinkedList<String> newSequence=new LinkedList<String>();
	   	 
		 while (trace.advance()) {
	    	  	    	
	    	  newSequence.add(trace.getCurrentEvent());
	    	 
	    	  winWidth++;
	    	      	  
	    	  if(winWidth >= seqLength){
	    		  		
	    		  winWidth--;
	    		  String[] seq=new String[seqLength];
	    		  seq=newSequence.toArray(seq);
	    		  // searching and adding to db
	    		   
	    		  hmm.generateSequences(seq, false);
	    		  
	    		  double prob=hmm.observationProbability(seq);
	    		  if (prob<probThreshold)
	    			  probThreshold=prob;
	    		  newSequence.remove(0);
	    	
	    	  }
	    		  
	     }
		options[7]=probThreshold.toString();
		hmm.saveSettings(options, database, connection); 
	}
	/**
	 * Tests an HMM
	 */
	@Override
	public Results test(ITraceIterator trace, String database, DBMS connection,
			String[] options) throws TotalADSUIException, TotalADSDBMSException {
		throw new TotalADSUIException("HMM is not implemented yet");
		
	}

	@Override
	public void crossValidate(Integer folds, String database, DBMS connection,
			ProgressConsole console, ITraceIterator trace, Boolean isLastTrace) throws TotalADSUIException, TotalADSDBMSException {
		// TODO Auto-generated method stub

	}

	@Override
	public Double getTotalAnomalyPercentage() {
		// TODO Auto-generated method stub
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

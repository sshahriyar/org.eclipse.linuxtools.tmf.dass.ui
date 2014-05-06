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

import org.eclipse.linuxtools.tmf.totalads.algorithms.IAlgorithmOutStream;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmTypes;
import org.eclipse.linuxtools.tmf.totalads.algorithms.Results;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmOutStream;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.swtchart.Chart;
/**
 * This class implements a Hidden Markov DataModel
 * @author <p>Syed Shariyar Murtaza justsshary@hotmail.com</p>
 * 
 */
public class HiddenMarkovModel implements IDetectionAlgorithm {
	//private String []trainingSettings;
	private Integer seqLength;
	private  HmmMahout hmm;
	private NameToIDMapper nameToID;
	private boolean isTrainIntialized=false, isValidationInitialized=false, isTestInitialized=false;
	private int numStates, numSymbols, numIterations, testNameToIDSize;
	private Double totalTestAnomalies=0.0, totalTestTraces=0.0, logThresholdTest=0.0;;
	
	/**
	 * Constructor
	 */
	public HiddenMarkovModel() {
		
		nameToID=new NameToIDMapper();	
		seqLength=10000000;
		
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
						    SettingsCollection.NUM_SYMBOLS.toString(), "300",
						    SettingsCollection.NUMBER_OF_ITERATIONS.toString(),"10",
							SettingsCollection.LOG_LIKELIHOOD.toString(),"0.0",
						    SettingsCollection.SEQ_LENGTH.toString(),seqLength.toString()};
		
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
		HmmMahout hmm=new HmmMahout();
		hmm.verifySaveSettingsCreateDb(options, database, connection,false,false);
		
	}
	/**
	 * Gets training settings
	 * @return
	 */
	@Override
	public String[] getTrainingOptions(DBMS connection, String database, Boolean isNewDatabase) {
		if (isNewDatabase){
			String [] trainingSettings=new String[6];
			trainingSettings[0]=SettingsCollection.NUM_STATES.toString();
			trainingSettings[1]="5";
			trainingSettings[2]= SettingsCollection.NUM_SYMBOLS.toString();
			trainingSettings[3]="300";
			trainingSettings[4]=SettingsCollection.NUMBER_OF_ITERATIONS.toString();
			trainingSettings[5]="10";
			return trainingSettings;
		}else{
			String [] trainingSettings=new String[2];
			trainingSettings[0]=SettingsCollection.NUMBER_OF_ITERATIONS.toString();
			trainingSettings[1]="10";
			return trainingSettings;
		}
			
	}
	/**
	 * Gets Test settings
	 * @param database
	 * @param connection
	 * @return  An array of String 
	 */
	@Override
	public String[] getTestingOptions(String database, DBMS connection) {
		 HmmMahout hmm=new HmmMahout();
		String []settings=hmm.loadSettings(database, connection);
		if (settings==null)
			return null;
		
		String []testingSettings=new String[2];
		//testingSettings[0]=SettingsCollection.SEQ_LENGTH.toString();
		//testingSettings[1]=settings[5];// seq length
		testingSettings[0]=SettingsCollection.LOG_LIKELIHOOD.toString();
		testingSettings[1]=settings[7]; // probaility
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
	   HmmMahout hmm=new HmmMahout();
	   hmm.verifySaveSettingsCreateDb(options, database, connection,false,false);
	}
 
  /**
   * 
   * Trains an HMM
   */
      @Override
	public void train(ITraceIterator trace, Boolean isLastTrace, String database, DBMS connection, IAlgorithmOutStream outStream,
		String[] options, Boolean isNewDB) throws TotalADSUIException, TotalADSDBMSException, TotalADSReaderException {
	   
	    if (!isTrainIntialized){
				 hmm=new HmmMahout();
								 
				 if (options!=null){
					 if (isNewDB){// add all settings to the db if this is a new database
						 String []setting=new String[options.length+6];
						 setting[0]=SettingsCollection.KEY.toString(); 
						 setting[1]="hmm";
						 for (int i=0;i<options.length;i++)
							 setting[i+2]=options[i];
						 setting[options.length+2]=SettingsCollection.LOG_LIKELIHOOD.toString();
						 setting[options.length+3]="0.0";
						 setting[options.length+4]=SettingsCollection.SEQ_LENGTH.toString();
						 setting[options.length+5]=seqLength.toString();
						 hmm.verifySaveSettingsCreateDb(setting, database, connection,true,true);
					 }else
						 hmm.verifySaveSettingsCreateDb(options, database, connection,false,false);
				 }					 
				 else{
					 if (isNewDB) 
						createDatabase(database, connection); // with default settings if no settings selected
				 } 
				 options=hmm.loadSettings(database, connection);// get settings from db
				 numStates=Integer.parseInt(options[1]);
				 numSymbols=Integer.parseInt(options[3]);
				 numIterations=Integer.parseInt(options[5]);
				// if (isNewDB)
				//	 hmm.initializeHMM(numSymbols, numStates);
				// else
				//	 hmm.loadHmm(connection, database);	
				 nameToID.loadMap(connection, database);
				 isTrainIntialized=true;
		 }	
	   
		 
		 outStream.addOutputEvent("Extracting sequences, please wait....");
		 
		 int winWidth=0,seqCount=0;
		 LinkedList<Integer> newSequence=new LinkedList<Integer>();
	   	 Boolean isTrained=true;
	   	 String event=null;
		
	   	 while (trace.advance()  ) {
			 event=trace.getCurrentEvent();
	    	  newSequence.add(nameToID.getId(event));
	    	 // newSequence.add(1);
	    	  winWidth++;
	    	  isTrained=false; 	  
	    	
	    	  if(winWidth >= seqLength){
	    		 
	    		  isTrained=true;	
	    		  winWidth--;
	    		  Integer[] seq=new Integer[seqLength];
	    		  seq=newSequence.toArray(seq);
	    		  outStream.addOutputEvent("Learning using the BaumWelch algorithm");
	    		  trainBaumWelch(seq, connection, database);
	    		  newSequence.remove(0);
	    		  seqCount++;
	    	  }
	    	  
	    		  
	     }
		 if (isTrained==false){// train on the last missing sequences in the previous loop
			 Integer[] seq=new Integer[newSequence.size()];
   		     seq=newSequence.toArray(seq);
   		     outStream.addOutputEvent("Learning on sequences using BaumWelch algorithm");
   		     trainBaumWelch(seq, connection, database);
		 }
		 
	     if (isLastTrace){ 
	    	     numSymbols=nameToID.getSize();
	    	     //hmm.initializeHMM(numSymbols, numStates);
	 		     outStream.addOutputEvent("Training finished..");
	    	     //hmm.learnUsingBaumWelch(numIterations);
	    	  	 outStream.addOutputEvent("Saving HMM");
	    	  	 outStream.addOutputEvent(hmm.toString());
	    	  	 //hmm.saveHMM(database, connection);
	    	  	 nameToID.saveMap(connection, database);
	     }
		 
		
	}
   /**
    *  Trains Using BaumWelch
    * @param seq
    * @throws TotalADSUIException
    */
   private void trainBaumWelch(Integer []seq, DBMS connection, String database) throws TotalADSUIException{
	   try{
		         hmm.initializeHMM(numSymbols, numStates);
		    	 hmm.learnUsingBaumWelch(numIterations, seq);
		    	 hmm.updatePreviousModel(seq, connection, database);
		    	 
		     } catch (Exception ex){
		    	 if (nameToID.getSize()>numSymbols)
		    		 throw new  TotalADSUIException("More events were found in the trace than you mentioned."
		    		 		+ " Please, select larger number of unique events and start fresh with a new model");
		    	 else
		    		 throw new TotalADSUIException(ex);
		     }
   }
	/**
	 * Validates HMM
	 * @param trace
	 * @param database
	 * @param connection
	 * @param isLastTrace
	 * @param outStream
	 * @throws TotalADSUIException
	 * @throws TotalADSDBMSException
	 * @throws TotalADSReaderException 
	 */
	@Override
	public void validate(ITraceIterator trace, String database,DBMS connection, 
			Boolean isLastTrace, IAlgorithmOutStream outStream) throws TotalADSUIException, TotalADSDBMSException, TotalADSReaderException {
		
		int winWidth=0,validationSeqLength=seqLength;
		Double logThreshold;
		String []options=hmm.loadSettings(database, connection);
		logThreshold=Double.parseDouble(options[7]);
		
		LinkedList<Integer> newSequence=new LinkedList<Integer>();
	   	outStream.addOutputEvent("Starting validation");
	   	Boolean isValidated=true;
	   	outStream.addOutputEvent("Extracting sequences, please wait...");
	   	
		 String event=null;
		 while (trace.advance()  ) {
			 event=trace.getCurrentEvent();
	    	  newSequence.add(nameToID.getId(event));
	    	 
	    	  winWidth++;
	    	  isValidated=false;    	  
	    	  if(winWidth >= validationSeqLength){
	    		  isValidated=true;		
	    		  winWidth--;
	    		  Integer[] seq=new Integer[validationSeqLength];
	    		  seq=newSequence.toArray(seq);
	    		  // searching and adding to db
	    		   logThreshold= validationEvaluation(outStream, logThreshold, seq);
	    		
	    		  newSequence.remove(0);
	    	
	    	  }
	    		  
	     }
		if (!isValidated){
			 Integer[] seq=new Integer[newSequence.size()];
   		  	 seq=newSequence.toArray(seq);
			 logThreshold=validationEvaluation(outStream, logThreshold, seq);
		}
		
		options[7]=logThreshold.toString();
		
		outStream.addOutputEvent("Minimum Log Likelihood Threshold: "+logThreshold.toString());
		 
		hmm.verifySaveSettingsCreateDb(options, database, connection,false,false); 
		//if (isLastTrace)
			//nameToID.saveMap(connection, database);
	}
	
	/**
	 * Performs the evaluation for a likelihood of a sequence during validation
	 * @param outStream
	 * @param probThreshold
	 * @param seq
	 */
	private Double validationEvaluation(IAlgorithmOutStream outStream, Double probThreshold, Integer []seq){
		  Double prob=1.0;
		  try{
			  prob=hmm.observationLikelihood(seq);
			 // console.printTextLn("Sequence likelhood: "+prob.toString());
		  } catch (Exception ex){
			  outStream.addOutputEvent("Unknown events in your sequences. Retrain using larger number of unique events");
			 // ex.printStackTrace();
		  }
		  
		  if (prob<probThreshold){
			  probThreshold=prob;
			  //console.printTextLn(Arrays.toString(seq));
			  outStream.addOutputEvent("Min Log Likelihood Threshold: "+probThreshold.toString());
			 
		  }
		  return probThreshold;
	}
	/**
	 * Tests an HMM
	 * @throws TotalADSReaderException 
	 */
	@Override
	public Results test(ITraceIterator trace, String database, DBMS connection,	String[] options,
			IAlgorithmOutStream outputStream) throws TotalADSUIException, TotalADSDBMSException, TotalADSReaderException {
		
		int winWidth=0,testSeqLength=seqLength;
		
		
		if (!isTestInitialized){
			hmm=new HmmMahout();
			if (options!=null){
				hmm.verifySaveSettingsCreateDb(options, database, connection, false, false);
				logThresholdTest=Double.parseDouble(options[1]);
			}else{
				options=hmm.loadSettings(database, connection);
				logThresholdTest=Double.parseDouble(options[7]);
			}
			hmm.loadHmm(connection, database);
			nameToID.loadMap(connection, database);
			testNameToIDSize=nameToID.getSize();
			isTestInitialized=true;
		}
		
		Results results=new Results();
		LinkedList<Integer> newSequence=new LinkedList<Integer>();
	   	Boolean isTested=true;
	    totalTestTraces++;
	    String event=null;
	   	while (trace.advance() ) {
	   		event=trace.getCurrentEvent();
	   		 newSequence.add(nameToID.getId(event));
	    	  winWidth++;
	    	  isTested=false;    	  
	    	  if(winWidth >= testSeqLength){
	    		  isTested=true;		
	    		  winWidth--;
	    		  Integer[] seq=new Integer[testSeqLength];
	    		  seq=newSequence.toArray(seq);
	    		  // searching and adding to db
	    		  if (testEvaluation(results, logThresholdTest, seq)==true)
	 				break;
	    		  //testEvaluation(results, logThreshold, seq);
	    		  newSequence.remove(0);
	    		  
	    	  }
	    		  
	     }
		if (!isTested){
			 Integer[] seq=new Integer[newSequence.size()];
   		  	 seq=newSequence.toArray(seq);
			 testEvaluation(results, logThresholdTest, seq);
		}
		
		
		return results; 
		
	}
	/**
	 * 
	 * @param result
	 * @param probThreshold
	 * @param seq
	 * @return Return true if anomaly else false
	 */
	private boolean testEvaluation(Results result, Double probThreshold, Integer []seq){
		  Double prob=1.0;
		  try{
			  prob=hmm.observationLikelihood(seq);
			
		  } catch (Exception ex){
				 result.setAnomaly(true);
				 if (nameToID.getSize() > testNameToIDSize){
					 Integer diff=nameToID.getSize()-testNameToIDSize;
					 
					 if (diff >100)
						 result.setDetails("\nMore than 100 unknown events, only 100 are shown here: \n");
					 else
						 result.setDetails("\nUnknown events: \n");
					 int eventCount=0;
					 for (int i=testNameToIDSize; i<testNameToIDSize+diff;i++){// All these events are unknown
						   result.setDetails(nameToID.getKey(i)+", ");
						   eventCount++;
						   if ((eventCount)%10==0)
							   result.setDetails("\n");   
					 }
					 testNameToIDSize+=diff;//don't display this for the second trace unless or untill there are additional events
				 }
			totalTestAnomalies++; 
			return true;	 
		  }
		  
		  if (prob<probThreshold){
			  probThreshold=prob;
			  result.setDetails("Anomalous pattern of events found in the sequence: \n");
			  
			  int firstRange=10;
			   if (seq.length <10)
				  firstRange=seq.length;
			  for (int id=0; id<firstRange;id++)
			    	result.setDetails(nameToID.getKey(seq[id])+", ");
			  
			  int secondRange=seq.length/2;
			  if (secondRange+10<seq.length){
				  result.setDetails("\n.........................................................\n");
				  for (int id=secondRange; id<secondRange+10;id++)
					  result.setDetails(nameToID.getKey(seq[id])+", ");
			  }
			
			  int thirdRange=seq.length;  
			  if (thirdRange-10>secondRange+10){
				  result.setDetails("\n.........................................................\n");	
				  for (int id=secondRange; id<secondRange+10;id++)
					  result.setDetails(nameToID.getKey(seq[id])+", ");
			  }
			  result.setAnomaly(true);
			  totalTestAnomalies++;
			  return true;
		  }else{
			  result.setAnomaly(false);
			  return false;
		  }
		  
	}
	
	/**
	 * Returns the total anomalies found during testing
	 */
	@Override
	public Double getTotalAnomalyPercentage() {
	
		 Double anomalyPercentage= (totalTestAnomalies/totalTestTraces) *100;
		 return anomalyPercentage;
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
		
		return "Hidden Markov DataModel (HMM)";
	}

	@Override
	public String getAcronym() {
		
		return "HMM";
	}
	
	

}

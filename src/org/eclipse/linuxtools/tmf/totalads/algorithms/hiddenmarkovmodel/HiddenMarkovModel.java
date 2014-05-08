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
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.swtchart.Chart;
/**
 * This class implements a Hidden Markov Model
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
	public static void registerModel() throws TotalADSGeneralException{
		AlgorithmFactory modelFactory= AlgorithmFactory.getInstance();
		HiddenMarkovModel hmm=new HiddenMarkovModel();
		modelFactory.registerModelWithFactory( AlgorithmTypes.ANOMALY,  hmm);
		
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#initializeModelAndSettings(java.lang.String, org.eclipse.linuxtools.tmf.totalads.dbms.DBMS, java.lang.String[])
	 */
	@Override
	public void initializeModelAndSettings(String modelName, DBMS connection, String[] trainingSettings)throws TotalADSDBMSException, TotalADSGeneralException {
		String []setting=null;
		
		if (trainingSettings!=null){
			 setting=new String[trainingSettings.length+6];
			 setting[0]=SettingsCollection.KEY.toString(); 
			 setting[1]="hmm";
			 for (int i=0;i<trainingSettings.length;i++)
				 setting[i+2]=trainingSettings[i];
			 setting[trainingSettings.length+2]=SettingsCollection.LOG_LIKELIHOOD.toString();
			 setting[trainingSettings.length+3]="0.0";
			 setting[trainingSettings.length+4]=SettingsCollection.SEQ_LENGTH.toString();
			 setting[trainingSettings.length+5]=seqLength.toString();
		}else{
		 
		    String []settings={ SettingsCollection.KEY.toString(),"hmm",
							SettingsCollection.NUM_STATES.toString(),"5",
						    SettingsCollection.NUM_SYMBOLS.toString(), "300",
						    SettingsCollection.NUMBER_OF_ITERATIONS.toString(),"10",
							SettingsCollection.LOG_LIKELIHOOD.toString(),"0.0",
						    SettingsCollection.SEQ_LENGTH.toString(),seqLength.toString()};
		    setting=settings;
		    
		}
		HmmMahout hmm=new HmmMahout();
		hmm.verifySaveSettingsCreateDb(setting, modelName, connection,true,true);
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#getTrainingOptions()
	 */
	@Override
	public String[] getTrainingOptions() {
		//if (isNewDatabase){
			String [] trainingSettings=new String[6];
			trainingSettings[0]=SettingsCollection.NUM_STATES.toString();
			trainingSettings[1]="5";
			trainingSettings[2]= SettingsCollection.NUM_SYMBOLS.toString();
			trainingSettings[3]="300";
			trainingSettings[4]=SettingsCollection.NUMBER_OF_ITERATIONS.toString();
			trainingSettings[5]="10";
			return trainingSettings;
		/*}else{
			String [] trainingSettings=new String[2];
			trainingSettings[0]=SettingsCollection.NUMBER_OF_ITERATIONS.toString();
			trainingSettings[1]="10";
			return trainingSettings;
		}*/
			
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#getTestingOptions(java.lang.String, org.eclipse.linuxtools.tmf.totalads.dbms.DBMS)
	 */
	@Override
	public String[] getTestingOptions(String database, DBMS connection) {
		 HmmMahout hmm=new HmmMahout();
		String []settings=hmm.loadSettings(database, connection);
		if (settings==null)
			return null;
		
		String []testingSettings=new String[2];
		testingSettings[0]=SettingsCollection.LOG_LIKELIHOOD.toString();
		testingSettings[1]=settings[7]; // probability
		return testingSettings;
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#saveTestingOptions(java.lang.String[], java.lang.String, org.eclipse.linuxtools.tmf.totalads.dbms.DBMS)
	 */
   @Override
	public void saveTestingOptions(String [] options, String database, DBMS connection) throws TotalADSGeneralException, TotalADSDBMSException
	{ 
	   HmmMahout hmm=new HmmMahout();
	   hmm.verifySaveSettingsCreateDb(options, database, connection,false,false);
	}
 
   /*
    * (non-Javadoc)
    * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#train(org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator, java.lang.Boolean, java.lang.String, org.eclipse.linuxtools.tmf.totalads.dbms.DBMS, org.eclipse.linuxtools.tmf.totalads.algorithms.IAlgorithmOutStream)
    */
    @Override
	public void train(ITraceIterator trace, Boolean isLastTrace, String database, DBMS connection, IAlgorithmOutStream outStream) throws TotalADSGeneralException, TotalADSDBMSException, TotalADSReaderException {
	   
	    if (!isTrainIntialized){
				 hmm=new HmmMahout();
								 
				/* if (options!=null){
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
						initializeModelAndSettings(database, connection, null); // with default settings if no settings selected
				 } */
				 String []options=hmm.loadSettings(database, connection);// get settings from db
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
    * Trains Using BaumWelch
    * @param seq
    * @throws TotalADSGeneralException
    */
   private void trainBaumWelch(Integer []seq, DBMS connection, String database) throws TotalADSGeneralException{
	   try{
		         hmm.initializeHMM(numSymbols, numStates);
		    	 hmm.learnUsingBaumWelch(numIterations, seq);
		    	 hmm.updatePreviousModel(seq, connection, database);
		    	 
		     } catch (Exception ex){
		    	 if (nameToID.getSize()>numSymbols)
		    		 throw new  TotalADSGeneralException("More events were found in the trace than you mentioned."
		    		 		+ " Please, select larger number of unique events and start fresh with a new model");
		    	 else
		    		 throw new TotalADSGeneralException(ex);
		     }
   }
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#validate(org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator, java.lang.String, org.eclipse.linuxtools.tmf.totalads.dbms.DBMS, java.lang.Boolean, org.eclipse.linuxtools.tmf.totalads.algorithms.IAlgorithmOutStream)
	 */
	@Override
	public void validate(ITraceIterator trace, String database,DBMS connection, 
			Boolean isLastTrace, IAlgorithmOutStream outStream) throws TotalADSGeneralException, TotalADSDBMSException, TotalADSReaderException {
		
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
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#test(org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator, java.lang.String, org.eclipse.linuxtools.tmf.totalads.dbms.DBMS, org.eclipse.linuxtools.tmf.totalads.algorithms.IAlgorithmOutStream)
	 */
	@Override
	public Results test(ITraceIterator trace, String database, DBMS connection,	IAlgorithmOutStream outputStream) throws TotalADSGeneralException, TotalADSDBMSException, TotalADSReaderException {
		
		int winWidth=0,testSeqLength=seqLength;
		String []options;
		
		if (!isTestInitialized){
			hmm=new HmmMahout();
			/*if (options!=null){
				hmm.verifySaveSettingsCreateDb(options, database, connection, false, false);
				logThresholdTest=Double.parseDouble(options[1]);
			}else{*/
				options=hmm.loadSettings(database, connection);
				logThresholdTest=Double.parseDouble(options[7]);
			//}
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
	 * Helper function for testing
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
	
	/*
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
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#createInstance()
	 */
	@Override
	public IDetectionAlgorithm createInstance() {
	
		return new HiddenMarkovModel();
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#getName()
	 */
	@Override
	public String getName() {
		
		return "Hidden Markov DataModel (HMM)";
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#getAcronym()
	 */
	@Override
	public String getAcronym() {
		
		return "HMM";
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#getDescription()
	 */
	@Override
	public String getDescription(){
		return "HMM is a stochastic model for sequential data and hence it is naturally suitable for modeling temporal order of "
				+ "system call sequences. The process is determined by a latent Markov chain having a finite number of "
				+ "states, N, and a set of output observation probability distributions, B, associated with each state. "
				+ "Starting from an initial state No, the process transits from one state to another according to the matrix "
				+ "of transition probability distribution, A, and then emits an observation symbol Ok from a finite alphabet "
				+ "(i.e., M distinct observable events) according to the output probability distribution, Bj(Ok), of the current"
				+ " state Nj. HMM is typically parameterized by the initial state distribution probabilities (Π), "
				+ "output (emission) probabilities (B), and state transition probabilities (A). Baulm-Welch algorithm is used "
				+ "to train the model parameters to fit the sequences of observations, T.  During the validation phase,"
				+ " HMM adjusts the decision threshold (log likelihood) of prediction of anomalous alarms on T sequences "
				+ "from traces. In the testing phase, if the probability value of any sequence in a trace is below the "
				+ "selected threshold, then we consider the trace as anomalous otherwise we consider it as normal";
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#isOnlineLearningSupported()
	 */
	@Override
	public boolean isOnlineLearningSupported() {
		
		return false;
	}

	
}

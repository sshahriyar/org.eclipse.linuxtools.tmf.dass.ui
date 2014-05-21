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

import org.eclipse.linuxtools.tmf.totalads.algorithms.IAlgorithmOutStream;
import org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmFactory;
import org.eclipse.linuxtools.tmf.totalads.algorithms.AlgorithmTypes;
import org.eclipse.linuxtools.tmf.totalads.algorithms.Results;
import org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject;
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
	private Integer fSeqLength;
	private  HmmMahout fHmm;
	private NameToIDMapper fNameToID;
	private boolean fIsTrainIntialized=false,  fIsTestInitialized=false;
	private int fNumStates, fNumSymbols, fNumIterations, fTestNameToIDSize;
	private Double fTotalTestAnomalies=0.0, fTotalTestTraces=0.0, fLogThresholdTest=0.0;
	private LinkedList<Integer> fBatchLargeTrainingSeq;
	/**
	 * Constructor
	 */
	public HiddenMarkovModel() {

		fNameToID=new NameToIDMapper();
		fSeqLength=100;

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
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#initializeModelAndSettings(java.lang.String, org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject, java.lang.String[])
	 */
	@Override
	public void initializeModelAndSettings(String modelName, IDataAccessObject dataAccessObject, String[] trainingSettings)throws TotalADSDBMSException, TotalADSGeneralException {
		String []setting=null;

		if (trainingSettings!=null){
			 setting=new String[trainingSettings.length+6];
			 setting[0]=SettingsCollection.KEY.toString();
			 setting[1]="HMM"; //$NON-NLS-1$
			 for (int i=0;i<trainingSettings.length;i++) {
                setting[i+2]=trainingSettings[i];
            }
			 setting[trainingSettings.length+2]=SettingsCollection.LOG_LIKELIHOOD.toString();
			 setting[trainingSettings.length+3]="0.0"; //$NON-NLS-1$
			 setting[trainingSettings.length+4]=SettingsCollection.SEQ_LENGTH.toString();
			 setting[trainingSettings.length+5]=fSeqLength.toString();
		}else{

		    String []settings={ SettingsCollection.KEY.toString(),"Hmm", //$NON-NLS-1$
							SettingsCollection.NUM_STATES.toString(),"5", //$NON-NLS-1$
						    SettingsCollection.NUM_SYMBOLS.toString(), "300", //$NON-NLS-1$
						    SettingsCollection.NUMBER_OF_ITERATIONS.toString(),"10", //$NON-NLS-1$
							SettingsCollection.LOG_LIKELIHOOD.toString(),"0.0", //$NON-NLS-1$
						    SettingsCollection.SEQ_LENGTH.toString(),fSeqLength.toString()};
		    setting=settings;

		}
		HmmMahout hmm=new HmmMahout();
		hmm.verifySaveSettingsCreateDb(setting, modelName, dataAccessObject,true,true);

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#getTrainingOptions()
	 */
	@Override
	public String[] getTrainingSettings() {

			String [] trainingSettings=new String[6];
			trainingSettings[0]=SettingsCollection.NUM_STATES.toString();
			trainingSettings[1]="5"; //$NON-NLS-1$
			trainingSettings[2]= SettingsCollection.NUM_SYMBOLS.toString();
			trainingSettings[3]="300"; //$NON-NLS-1$
			trainingSettings[4]=SettingsCollection.NUMBER_OF_ITERATIONS.toString();
			trainingSettings[5]="10"; //$NON-NLS-1$
			return trainingSettings;

	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#getTestingOptions(java.lang.String, org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject)
	 */
	@Override
	public String[] getTestSettings(String database, IDataAccessObject dataAccessObject) throws TotalADSDBMSException {
		 HmmMahout hmm=new HmmMahout();
		String []settings=hmm.loadSettings(database, dataAccessObject);
		if (settings==null) {
            return null;
        }

		String []testingSettings=new String[2];
		testingSettings[0]=SettingsCollection.LOG_LIKELIHOOD.toString();
		testingSettings[1]=settings[7]; // probability
		return testingSettings;
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#saveTestingOptions(java.lang.String[], java.lang.String, org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject)
	 */
   @Override
	public void saveTestSettings(String [] options, String database, IDataAccessObject dataAccessObject) throws TotalADSGeneralException, TotalADSDBMSException
	{
	   HmmMahout hmm=new HmmMahout();
	   hmm.verifySaveSettingsCreateDb(options, database, dataAccessObject,false,false);
	}

   /*
    * (non-Javadoc)
    * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#getSettingsToDisplay()
    */
   @Override
   public String[] getSettingsToDisplay(String database, IDataAccessObject dataAccessObject) throws TotalADSDBMSException{
	    HmmMahout hmm=new HmmMahout();
		String []settings=hmm.loadSettings(database, dataAccessObject);
		return settings;
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#train(org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator, java.lang.Boolean, java.lang.String, org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject, org.eclipse.linuxtools.tmf.totalads.algorithms.IAlgorithmOutStream)
    */
    @Override
	public void train(ITraceIterator trace, Boolean isLastTrace, String database, IDataAccessObject connection, IAlgorithmOutStream outStream) throws TotalADSGeneralException, TotalADSDBMSException, TotalADSReaderException {

	  batchTraining(trace, isLastTrace, database, connection, outStream);

	}

    /**
     * Trains an HMM on a collection of traces at once; i.e., in a batch
     * @param trace
     * @param isLastTrace
     * @param database
     * @param connection
     * @param outStream
     * @throws TotalADSGeneralException
     * @throws TotalADSDBMSException
     * @throws TotalADSReaderException
     */
    private void batchTraining(ITraceIterator trace, Boolean isLastTrace, String database, IDataAccessObject connection, IAlgorithmOutStream outStream) throws TotalADSGeneralException, TotalADSDBMSException, TotalADSReaderException {

	    if (!fIsTrainIntialized){
				 fHmm=new HmmMahout();

				 String []options=fHmm.loadSettings(database, connection);// get settings from db
				 fNumStates=Integer.parseInt(options[1]);
				 fNumSymbols=Integer.parseInt(options[3]);
				 fNumIterations=Integer.parseInt(options[5]);
				 fNameToID.loadMap(connection, database);
				 fIsTrainIntialized=true;
				 fBatchLargeTrainingSeq=new LinkedList<>();
		 }


		 outStream.addOutputEvent("Extracting sequences, please wait....");
		 outStream.addNewLine();

	   	 String event=null;

	   	 while (trace.advance()  ) {
			  event=trace.getCurrentEvent();
			  fBatchLargeTrainingSeq.add(fNameToID.getId(event));

	     }

	     if (isLastTrace){
	    	     fNumSymbols=fNameToID.getSize();

	    	     outStream.addOutputEvent("Training using BaumWelch..can take really long depending on the size and number of traces");
	 		     outStream.addNewLine();
	    	        Integer[] seq=new Integer[fBatchLargeTrainingSeq.size()];
	    		 seq=fBatchLargeTrainingSeq.toArray(seq);
	    	     trainBaumWelch(seq, connection, database);

	    	  	 outStream.addOutputEvent("Saving HMM to the database");
	    	  	 outStream.addNewLine();
	    	  	 fHmm.saveHMM(database, connection);

	    	  	 outStream.addOutputEvent(fHmm.toString());
	    	  	 outStream.addNewLine();
	    	  	 //fHmm.saveHMM(database, connection);
	    	  	 fNameToID.saveMap(connection, database);
	     }


	}
   /**
    * Trains Using BaumWelch
    * @param seq
    * @throws TotalADSGeneralException
    */
   private void trainBaumWelch(Integer []seq, IDataAccessObject dao, String database) throws TotalADSGeneralException{
	   try{
		         fHmm.initializeHMM(fNumSymbols, fNumStates);
		    	 fHmm.learnUsingBaumWelch(fNumIterations, seq);
		    	// fHmm.updatePreviousModel(seq, connection, database);
		    	 fHmm.saveHMM(database, dao);

		     } catch (Exception ex){
		    	 if (fNameToID.getSize()>fNumSymbols) {
                    throw new  TotalADSGeneralException("More events were found in the trace than you mentioned."
		    		 		+ " Please, select larger number of unique events and start fresh with a new model. HMM model cannot be built!");
                }
                throw new TotalADSGeneralException(ex);
		     }
   }
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#validate(org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator, java.lang.String, org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject, java.lang.Boolean, org.eclipse.linuxtools.tmf.totalads.algorithms.IAlgorithmOutStream)
	 */
	@Override
	public void validate(ITraceIterator trace, String database,IDataAccessObject dataAccessObject,
			Boolean isLastTrace, IAlgorithmOutStream outStream) throws TotalADSGeneralException, TotalADSDBMSException, TotalADSReaderException {

		int winWidth=0,validationSeqLength=fSeqLength;
		Double logThreshold;
		String []options=fHmm.loadSettings(database, dataAccessObject);
		logThreshold=Double.parseDouble(options[7]);

		LinkedList<Integer> newSequence=new LinkedList<>();
	   	outStream.addOutputEvent("Starting validation");
	   	outStream.addNewLine();

	   	Boolean isValidated=false;
	   	outStream.addOutputEvent("Extracting sequences, please wait...");
	   	outStream.addNewLine();

		 String event=null;
		 while (trace.advance()  ) {
			 event=trace.getCurrentEvent();
	    	  newSequence.add(fNameToID.getId(event));

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
		outStream.addNewLine();
		outStream.addOutputEvent("Finished validation");
		outStream.addNewLine();
		fHmm.verifySaveSettingsCreateDb(options, database, dataAccessObject,false,false);

	}

	/**
	 * Performs the evaluation for a likelihood of a sequence during validation
	 * @param outStream
	 * @param logThreshold
	 * @param seq
	 */
	private Double validationEvaluation(IAlgorithmOutStream outStream, Double logThreshold, Integer []seq){
		  Double prob=1.0;
		  Double logLikelihood=logThreshold;
		  try{
			  prob=fHmm.observationLikelihood(seq);
			 // console.printTextLn("Sequence likelhood: "+prob.toString());
		  } catch (Exception ex){
			  outStream.addOutputEvent("Unknown events in your validation traces: Though HMM model is succefully built, consider retraining using larger number of unique events");
			  outStream.addNewLine();
			  // ex.printStackTrace();
		  }

		  if (prob<logLikelihood){
			  logLikelihood=prob;
			  //console.printTextLn(Arrays.toString(seq));
			  outStream.addOutputEvent("Min Log Likelihood Threshold: "+logThreshold.toString());
			  outStream.addNewLine();
		  }
		  return logLikelihood;
	}


	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#test(org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator, java.lang.String, org.eclipse.linuxtools.tmf.totalads.dbms.IDataAccessObject, org.eclipse.linuxtools.tmf.totalads.algorithms.IAlgorithmOutStream)
	 */
	@Override
	public Results test(ITraceIterator trace, String database, IDataAccessObject dataAccessObject,	IAlgorithmOutStream outputStream) throws TotalADSGeneralException, TotalADSDBMSException, TotalADSReaderException {

		int winWidth=0,testSeqLength=fSeqLength;
		String []options;

		if (!fIsTestInitialized){
			fHmm=new HmmMahout();
			options=fHmm.loadSettings(database, dataAccessObject);
			fLogThresholdTest=Double.parseDouble(options[7]);

			fHmm.loadHmm(dataAccessObject, database);
			fNameToID.loadMap(dataAccessObject, database);
			fTestNameToIDSize=fNameToID.getSize();
			fIsTestInitialized=true;
		}

		Results results=new Results();
		LinkedList<Integer> newSequence=new LinkedList<>();
	   	Boolean isTested=false;
	    fTotalTestTraces++;
	    String event=null;

	    outputStream.addOutputEvent("Extracting Sequences");
	    outputStream.addNewLine();
	    int seqCount=1;
	    while (trace.advance() ) {

	    	  event=trace.getCurrentEvent();
	   		  newSequence.add(fNameToID.getId(event));
	    	  winWidth++;
	    	  isTested=false;

	    	  if(winWidth >= testSeqLength){

	    		  isTested=true;
	    		  winWidth--;
	    		  Integer[] seq=new Integer[testSeqLength];
	    		  seq=newSequence.toArray(seq);

	    		  if (seqCount%10000==0){
	    			  outputStream.addOutputEvent("Executing "+seqCount+"th sequence");
	    			  outputStream.addNewLine();
	    		  }

	    		  if (testEvaluation(results, fLogThresholdTest, seq)==true) {
                    break;
                }
	    		  //testEvaluation(results, logThreshold, seq);
	    		  newSequence.remove(0);
	    		  seqCount++;
	    	  }

	     }
		if (!isTested){
			 Integer[] seq=new Integer[newSequence.size()];
   		  	 seq=newSequence.toArray(seq);
			 testEvaluation(results, fLogThresholdTest, seq);
		}
		outputStream.addOutputEvent("Finished evaluating the trace");
		outputStream.addNewLine();
		if (results.getAnomaly()==true) {
            fTotalTestAnomalies++;
        }
		return results;

	}
	/**
	 * Helper function for testing
	 * @param result
	 * @param logThreshold
	 * @param seq
	 * @return Return true if anomaly, else returns false
	 */
	private boolean testEvaluation(Results result, Double logThreshold, Integer []seq){
		  Double loglikelihood=1.0;
		  Double logThresholdValue=logThreshold;
		  try{
			  loglikelihood=fHmm.observationLikelihood(seq);

		  } catch (Exception ex){
				 result.setAnomaly(true);
				 if (fNameToID.getSize() > fTestNameToIDSize){
					 Integer diff=fNameToID.getSize()-fTestNameToIDSize;

					 if (diff >100) {
                        result.setDetails("\nMore than 100 unknown events, only 100 are shown here: \n");
                    } else {
                        result.setDetails("\nUnknown events: \n");
                    }
					 int eventCount=0;
					 for (int i=fTestNameToIDSize; i<fTestNameToIDSize+diff;i++){// All these events are unknown
						   result.setDetails(fNameToID.getKey(i)+", ");
						   eventCount++;
						   if ((eventCount)%10==0) {
                            result.setDetails("\n");
                        }
					 }
					 fTestNameToIDSize+=diff;//don't display this for the second trace unless or untill there are additional events
				 }
			//fTotalTestAnomalies++;
			return true;
		  }

		  if (loglikelihood<logThresholdValue){
			  logThresholdValue=loglikelihood;
			  result.setDetails("Anomalous pattern of events found in the sequence: \n");

			  int firstRange=10;
			   if (seq.length <10) {
                firstRange=seq.length;
            }
			  for (int id=0; id<firstRange;id++) {
                result.setDetails(fNameToID.getKey(seq[id])+", ");
            }

			  int secondRange=seq.length/2;
			  if (secondRange+10<seq.length){
				  result.setDetails("\n.........................................................\n");
				  for (int id=secondRange; id<secondRange+10;id++) {
                    result.setDetails(fNameToID.getKey(seq[id])+", ");
                }
			  }

			  int thirdRange=seq.length;
			  if (thirdRange-10>secondRange+10){
				  result.setDetails("\n.........................................................\n");
				  for (int id=secondRange; id<secondRange+10;id++) {
                    result.setDetails(fNameToID.getKey(seq[id])+", ");
                }
			  }
			  result.setAnomaly(true);
			  //fTotalTestAnomalies++;
			  return true;
		  }
          result.setAnomaly(false);
          return false;

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.linuxtools.tmf.totalads.algorithms.IDetectionAlgorithm#getTotalAnomalyPercentage()
	 */
	@Override
	public Double getTotalAnomalyPercentage() {

		 Double anomalyPercentage= (fTotalTestAnomalies/fTotalTestTraces) *100;
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

		return "Hidden Markov Model (HMM)";
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
	//////////////////////////////////////////////////
	//Test code for incremental HMM
	/////////////////////////////////////////////////
	/*
	private void incrementalHMM(ITraceIterator trace, Boolean isLastTrace, String database, IDataAccessObject connection, IAlgorithmOutStream outStream) throws TotalADSGeneralException, TotalADSDBMSException, TotalADSReaderException {

	    if (!fIsTrainIntialized){
				 fHmm=new HmmMahout();

				 String []options=fHmm.loadSettings(database, connection);// get settings from db
				 fNumStates=Integer.parseInt(options[1]);
				 fNumSymbols=Integer.parseInt(options[3]);
				 fNumIterations=Integer.parseInt(options[5]);
				 fNameToID.loadMap(connection, database);
				 fIsTrainIntialized=true;
		 }


		 outStream.addOutputEvent("Extracting sequences, please wait....");
		 outStream.addNewLine();

		 int winWidth=0;
		 LinkedList<Integer> newSequence=new LinkedList<Integer>();
	   	 Boolean isTrained=true;
	   	 String event=null;

	   	 while (trace.advance()  ) {
			  event=trace.getCurrentEvent();
	    	  newSequence.add(fNameToID.getId(event));
	    	 // newSequence.add(1);
	    	  winWidth++;
	    	  isTrained=false;

	    	  if(winWidth >= fSeqLength){

	    		  isTrained=true;
	    		  winWidth--;
	    		  Integer[] seq=new Integer[fSeqLength];
	    		  seq=newSequence.toArray(seq);
	    		  outStream.addOutputEvent("Learning using the BaumWelch algorithm");
	    		  outStream.addNewLine();
	    		  trainBaumWelch(seq, connection, database);
	    		  newSequence.remove(0);

	    	  }


	     }
		 if (isTrained==false){// train on the last missing sequences in the previous loop
			 Integer[] seq=new Integer[newSequence.size()];
   		     seq=newSequence.toArray(seq);
   		     outStream.addOutputEvent("Learning on sequences using BaumWelch algorithm");
   		     outStream.addNewLine();
   		     trainBaumWelch(seq, connection, database);
		 }

	     if (isLastTrace){
	    	     fNumSymbols=fNameToID.getSize();
	    	     //fHmm.initializeHMM(fNumSymbols, fNumStates);
	 		     outStream.addOutputEvent("Training finished..");
	 		     outStream.addNewLine();

	    	  	 outStream.addOutputEvent("Saving HMM");
	    	  	 outStream.addNewLine();

	    	  	 outStream.addOutputEvent(fHmm.toString());
	    	  	 outStream.addNewLine();
	    	  	 //fHmm.saveHMM(database, connection);
	    	  	 fNameToID.saveMap(connection, database);
	     }


	}*/

}

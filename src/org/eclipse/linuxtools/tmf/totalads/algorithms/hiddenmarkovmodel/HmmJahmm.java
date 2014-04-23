package org.eclipse.linuxtools.tmf.totalads.algorithms.hiddenmarkovmodel;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import be.ac.ulg.montefiore.run.jahmm.*;
import be.ac.ulg.montefiore.run.jahmm.draw.GenericHmmDrawerDot;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchScaledLearner;
import be.ac.ulg.montefiore.run.jahmm.toolbox.KullbackLeiblerDistanceCalculator;
import be.ac.ulg.montefiore.run.jahmm.toolbox.MarkovGenerator;
/**
 * 
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class HmmJahmm {
	private List <List <ObservationInteger>> sequences;
	private Hmm<ObservationInteger> hmm;
	private BaumWelchLearner bwl = new BaumWelchLearner();
	private int numStates;
	private int numSymbols;
	
	/**
	 * Initializes Hidden Markov Model with random initial probabilities
	 * @param numSymbols
	 * @param numStates
	 * 
	 */
	public void initializeHMM(int numSymbols, int numStates){
		
		OpdfIntegerFactory factory = new OpdfIntegerFactory(numSymbols);
		hmm = new Hmm<ObservationInteger>(numStates,factory);
		this.numSymbols=numSymbols;
		this.numStates=numStates;
		
		/// Generating transition probabilities with random numbers
		Random random = new Random();
		double start=0.0001;
		double end=1.0000;
		double tansitionProbabilities[][]=new double[numStates][numStates];
		double []rowSums=new double[numStates];	
		Arrays.fill(rowSums,0.0);
		
		for (int row=0;row<numStates; row++)
			for (int col = 0; col < numStates; col++){
				tansitionProbabilities[row][col]=getRandomRealNumber(start, end, random);
				rowSums[row]+=tansitionProbabilities[row][col];
			}
		
		for (int row=0;row<numStates; row++)
			for (int col = 0; col < numStates; col++)
				tansitionProbabilities[row][col]=tansitionProbabilities[row][col]/rowSums[row];

		
		// Assigning initial state probabilities Pi; i.e. probabilities at time 1
		for (int idx=0; idx<numStates; idx++ )
			   hmm.setPi(idx, tansitionProbabilities[0][idx]);
		
		//Assigning transition probabilities
		for (int row=0;row<numStates; row++)
			for (int col = 0; col < numStates; col++)
				hmm.setAij(row, col,tansitionProbabilities[row][col]);
		
		// Measuring emission probabilities of each symbol
		double emissionProbabilities[][]=new double[numStates][numSymbols];
		Arrays.fill(rowSums,0.0);// Utilizing the same rowSums variable 
		random = new Random();
		
		for (int row=0;row<numStates; row++)
			for (int col = 0; col < numSymbols; col++){
				emissionProbabilities[row][col]=getRandomRealNumber(start, end, random);
				rowSums[row]+=emissionProbabilities[row][col];
			}
		
		for (int row=0;row<numStates; row++)
			for (int col = 0; col < numSymbols; col++)
				emissionProbabilities[row][col]=emissionProbabilities[row][col]/rowSums[row];
		
		//Assigning emission probabilities of symbols  to states
		for (int row=0;row<numStates; row++)
			hmm.setOpdf(row, new OpdfInteger(emissionProbabilities[row]));
				
		
	}
	
	/**
	 * Validates settings and saves them into the database after creating a new databse if requried
	 * @param settings Settings array
	 * @param database Database name
	 * @param connection DBMS object
	 * @param isNewSettings True if settings are inserted first time, else false if existing fields are updated
	 * @param isNewDBTrue if new database has to be created 
	 * @throws TotalADSUIException
	 * @throws TotalADSDBMSException
	 */
	public void verifySaveSettingsCreateDb(String []settings, String database, DBMS connection, Boolean isNewSettings, Boolean isNewDB) throws TotalADSUIException, TotalADSDBMSException{
	
		JsonObject settingObject=new JsonObject();
		for (int i=0; i<settings.length;i+=2){ 
			
		     if (SettingsCollection.NUM_STATES.toString().equalsIgnoreCase(settings[i])){
					  try {
						  Integer num_states=Integer.parseInt(settings[i+1]);
						  settingObject.add(SettingsCollection.NUM_STATES.toString(), new JsonPrimitive(num_states));
					  }catch (Exception ex){
						  throw new TotalADSUIException("Select an integer for number of states");
					  }
					  
				}else if (SettingsCollection.NUM_SYMBOLS.toString().equalsIgnoreCase(settings[i])){
					  try {
						  Integer num_symbols=Integer.parseInt(settings[i+1]);
						  settingObject.add(SettingsCollection.NUM_SYMBOLS.toString(), new JsonPrimitive(num_symbols));
					  }catch (Exception ex){
						  throw new TotalADSUIException("Select an integer for number of symbols");
					  }
			   	}else if (SettingsCollection.SEQ_LENGTH.toString().equalsIgnoreCase(settings[i])){
					  try {
						  Integer seqLength=Integer.parseInt(settings[i+1]);
						  settingObject.add(SettingsCollection.SEQ_LENGTH.toString(), new JsonPrimitive(seqLength));
					  }catch (Exception ex){
						  throw new TotalADSUIException("Select an integer for sequence length");
					  }
			   	}else if (SettingsCollection.LOG_LIKELIHOOD.toString().equalsIgnoreCase(settings[i])){
					  try {
						   Double prob=Double.parseDouble(settings[i+1]);
						   if (prob >1.0) throw new TotalADSUIException("Probability can't be > 1");
						   settingObject.add(SettingsCollection.LOG_LIKELIHOOD.toString().toString(), new JsonPrimitive(prob));
					  }catch (Exception ex){
						  throw new TotalADSUIException("Select a decimal number for the probability threshold");
					  }
			   	} else if (SettingsCollection.KEY.toString().equalsIgnoreCase(settings[i])){
			   		settingObject.add(SettingsCollection.KEY.toString(), new JsonPrimitive("hmm"));
			   	}
		}
		
		// creating id for query searching
		JsonObject jsonKey=new JsonObject();
		jsonKey.addProperty(SettingsCollection.KEY.toString(),"hmm");
		
		if (isNewDB){
			String []collectionNames={HmmModelCollection.COLLECTION_NAME.toString(), SettingsCollection.COLLECTION_NAME.toString()
	                  , NameToIDCollection.COLLECTION_NAME.toString() };
			connection.createDatabase(database, collectionNames);
		}
		
		if (isNewSettings)
			connection.insertOrUpdateUsingJSON(database, jsonKey, settingObject, SettingsCollection.COLLECTION_NAME.toString());
		else
			connection.updateFieldsInExistingDocUsingJSON(database, jsonKey, settingObject, SettingsCollection.COLLECTION_NAME.toString());
				
	    
		
	}
	
	/**
	 * Loads settings from the database
	 * @param database
	 * @param connection
	 * @return Settings as an array of String 
	 */
	public String[] loadSettings(String database, DBMS connection){
		String [] settings=null;
		DBCursor cursor=connection.selectAll(database, SettingsCollection.COLLECTION_NAME.toString());
		if (cursor!=null){
				 settings=new String[8];
				 Gson gson =new Gson();
				 DBObject dbObject=cursor.next();
				 settings[0]=SettingsCollection.NUM_STATES.toString();
				 settings[1]=dbObject.get(SettingsCollection.NUM_STATES.toString()).toString();
				 settings[2]=SettingsCollection.NUM_SYMBOLS.toString();
				 settings[3]=dbObject.get(SettingsCollection.NUM_SYMBOLS.toString()).toString();
				 settings[4]=SettingsCollection.SEQ_LENGTH.toString();
				 settings[5]=dbObject.get(SettingsCollection.SEQ_LENGTH.toString()).toString();
				 settings[6]=SettingsCollection.LOG_LIKELIHOOD.toString();
				 settings[7]=dbObject.get(SettingsCollection.LOG_LIKELIHOOD.toString()).toString();
		}
		return settings;
	}
	/**
	 * Returns a decimal random number within a decimal range
	 * @param start
	 * @param end
	 * @param random
	 * @return
	 */
	private double getRandomRealNumber(double start, double end, Random random){
		   
		    //get the range, casting to long to avoid overflow problems
		    double range = end - start ;
		    // compute a fraction of the range, 0 <= frac < range
		    double fraction = (range * random.nextDouble());
		    double randomNumber =  fraction + start;    
		    return randomNumber;
	  }
		  
	/**
	 * Trains an HmmJahmm using the BaumWelch algorithm
	 * @param numIterations
	 * @param initialHMM
	 * @param sequences
	 */
	public  void learnUsingBaumWelch(Integer numIterations){

		 //BaumWelchLearner bwl = new BaumWelchLearner();
			for (int i = 0; i < numIterations; i++) 
					hmm = bwl.iterate(hmm, sequences);
	
	}
	
	/**
	 * Converts an int array sequence into a List sequence on which an HmmJahmm can be trained
	 * @param seq
	 * @return
	 */
	private List<ObservationInteger> generateASequence(Integer []seq){
		
		List<ObservationInteger> sequence = new ArrayList<ObservationInteger>(); 
		
		for (int j=0; j<seq.length;j++)
			sequence.add(new ObservationInteger(seq[j].intValue()));
		
		return sequence;
	}
	
	/**
	 * 
	 * @param seq
	 * @param isAppend if false then a new sequence is created, else previous one is appended
	 */
	public void  generateSequences(Integer []seq, Boolean isAppend){
		if (!isAppend) //isAppend==false
			sequences=new ArrayList<List<ObservationInteger>>();
		sequences.add(generateASequence(seq));
	}
	/**
	 * Returns the Observation probability of a sequences based on a model
	 * @param seq Integer array of sequences
	 * @return
	 */
	public double observationProbability(Integer [] seq){
		return hmm.probability(generateASequence(seq));
	}
	/**
	 * Loads the model directly from the database
	 * @param connection
	 * @param database
	 */
	public void loadHmm(DBMS connection, String database){
	   
		DBCursor cursor=connection.selectAll(database, HmmModelCollection.COLLECTION_NAME.toString());
		if (cursor!=null){
				 
				 Gson gson =new Gson();
				 int stateCounter=0;
				
				 while (cursor.hasNext()){	
			
					DBObject dbObject=cursor.next();
					Object obj=dbObject.get(HmmModelCollection.MODEL.toString());
					
					if (obj!=null){
					
						State stateJ = gson.fromJson(obj.toString(), State.class);
						numStates= stateJ.getTransitionSize();
					    numSymbols=stateJ.getEmissionSize();
						
						if (stateCounter==0){ // create an object in the first iteration only
							OpdfIntegerFactory factory = new OpdfIntegerFactory(numSymbols);
							hmm = new Hmm<ObservationInteger>(numStates,factory);
						}
											
						// Assigning initial state probabilities
						hmm.setPi(stateCounter, stateJ.getInitialProb());
						
						//Assigning transition probabilities
						double []aij=stateJ.getTransition();
						for (int col = 0; col < numStates; col++)
								hmm.setAij(stateCounter, col,aij[col]);
						// Assigning emission probabilities	
						hmm.setOpdf(stateCounter, new OpdfInteger(stateJ.getEmission()));
				
					} 
					
				}// end while
		}
	}
	
	/**
	 * This functions saves the HmmJahmm model into the database
	 * @param database
	 * @param connection
	 * @throws TotalADSDBMSException
	 */
	public void saveHMM(String database, DBMS connection) throws TotalADSDBMSException{
		
		int states= hmm.nbStates();
		
		/// Inserting the states and probabilities
		for (int stateCount=0; stateCount <states; stateCount++){
			
			JsonObject stateProperties=new JsonObject();
			//Saving initial state probabilities
			JsonPrimitive pi=new JsonPrimitive(hmm.getPi(stateCount));
			//pi.addProperty(HmmModelCollection.INTITIALPROB.toString(), hmm.getPi(stateCount));
			
			//Saving transition probabilities
			JsonArray transitionMatrix =new JsonArray();
			for (int transCount=0;transCount <states;transCount++){
				JsonPrimitive aij=new JsonPrimitive(hmm.getAij(stateCount, transCount));
				transitionMatrix.add(aij);
			}
			
			// Saving emission probabilities of output symbols
			JsonArray emissionMatrix =new JsonArray();
			Opdf<ObservationInteger> opdf= hmm.getOpdf(stateCount);
		
			for (int emissionCount=0;emissionCount <numSymbols;emissionCount++){
				//since symbols are integer numbers in an order, we can use emissionCount as a symbol
				double stateOi=opdf.probability(new ObservationInteger(emissionCount));
				JsonPrimitive stateOuti=new JsonPrimitive(stateOi);
				emissionMatrix.add(stateOuti);
			}
			
			// Adding them in an array
			stateProperties.add(HmmModelCollection.INTITIALPROB.toString(),pi);
			stateProperties.add(HmmModelCollection.TRANSITIONPROB.toString(), transitionMatrix);
			stateProperties.add(HmmModelCollection.EMISSIONPROB.toString(), emissionMatrix);
			String key="state_"+stateCount;
			
			// Creating states ids
			JsonObject state=new JsonObject();
			state.add(HmmModelCollection.KEY.toString(),new JsonPrimitive(key));
			state.add("state", stateProperties);
			
			// Creating id for query searching
			JsonObject jsonTheKey=new JsonObject();
			jsonTheKey.addProperty(HmmModelCollection.KEY.toString(),key);
			
			System.out.println(state.toString());
			connection.insertOrUpdateUsingJSON(database, jsonTheKey, state,	HmmModelCollection.COLLECTION_NAME.toString());
		}
		
	}
	
	/**
	 * Used for verification/testing of the model
	 * @return
	 */
	public String printHMM(){
		
		return hmm.toString(new DecimalFormat("####.###################"));
		
	}
	/**
	 * 
	 * @param args
	 */
	public static void main (String args[]){
	/*	HmmJahmm myHMM=new HmmJahmm();
		myHMM.initializeHMM(5, 5);
	
		Gson gson =new Gson();
		System.out.println(gson.toJson(myHMM));
		
		myHMM.generateSequences(new Integer[]{1,2,3,4,0,1,2,3,4,0},false );
		myHMM.generateSequences(new Integer[]{1,1,1,1,0,1,2,3,4,0},true );
		myHMM.generateSequences(new Integer[]{1,4,2,1,0,1,2,3,4,0},true );
		myHMM.generateSequences(new Integer[]{1,3,2,1,0,1,2,3,4,0},true );
		myHMM.generateSequences(new Integer[]{1,2,2,2,0,1,2,3,4,0},true );
		myHMM.generateSequences(new Integer[]{1,4,2,4,0,1,2,3,4,0},true );
		myHMM.generateSequences(new Integer[]{1,2,3,2,0,1,2,3,4,0},true );
		myHMM.generateSequences(new Integer[]{1,2,4,4,0,1,2,3,4,0},true );
		myHMM.generateSequences(new Integer[]{1,2,3,4,0,1,2,3,4,0},true );
		myHMM.generateSequences(new Integer[]{1,1,1,1,0,1,1,1,1,0},true );
		myHMM.generateSequences(new Integer[]{1,4,2,1,0,1,1,1,1,0},true );
		myHMM.generateSequences(new Integer[]{1,3,2,1,0,1,1,1,1,0},true );
		myHMM.generateSequences(new Integer[]{1,2,2,2,0,1,1,1,1,0},true );
		myHMM.generateSequences(new Integer[]{1,4,2,4,0,1,1,1,1,0},true );
		myHMM.generateSequences(new Integer[]{1,2,3,2,0,1,1,1,1,0},true );
		myHMM.generateSequences(new Integer[]{1,2,4,4,0,1,1,1,1,0},true );
		myHMM.generateSequences(new Integer[]{1,2,3,4,0,1,1,1,1,0},true );
		myHMM.generateSequences(new Integer[]{1,1,1,1,0,1,1,1,1,0},true );
		
	
		
		myHMM.learnUsingBaumWelch(100);
		myHMM.learnUsingBaumWelch(100);
		myHMM.learnUsingBaumWelch(100);
		System.out.println(myHMM.printHMM());
		System.out.println(myHMM.observationProbability(new Integer[]{1,2,4,4,0,1,1,1,1,0}));
		
		/*myHMM.generateSequences(new int[]{1,4,2,1,0,1,1,1,1,0,1,4,2,1,0,1,1,1,1,0},false );
		myHMM.generateSequences(new int[]{1,4,2,1,0,1,1,1,1,0,1,4,2,1,0,1,1,1,1,0},true );
		myHMM.generateSequences(new int[]{1,3,2,2,0,1,1,1,1,0,1,4,2,1,0,1,1,1,1,0},true );
		myHMM.generateSequences(new int[]{1,2,2,4,0,1,1,1,1,0,1,4,2,1,0,1,1,1,1,0},true );
		myHMM.generateSequences(new int[]{1,0,3,2,0,1,1,1,1,0,1,4,2,1,0,1,1,1,1,0},true );
		myHMM.generateSequences(new int[]{1,0,4,4,0,1,1,1,1,0,1,4,2,1,0,1,1,1,1,0},true );
		myHMM.generateSequences(new int[]{1,1,1,1,0,1,1,1,1,0,1,4,2,1,0,1,1,1,1,0},true );
		myHMM.generateSequences(new int[]{1,4,2,1,0,1,1,1,1,0,1,4,2,1,0,1,1,1,1,0},true );
		myHMM.generateSequences(new int[]{1,3,2,1,0,1,1,1,1,0,1,4,2,1,0,1,1,1,1,0},true );
		myHMM.generateSequences(new int[]{1,2,2,2,0,1,1,1,1,0,1,4,2,1,0,1,1,1,1,0},true );
		myHMM.generateSequences(new int[]{1,4,2,4,0,1,1,1,1,0,1,4,2,1,0,1,1,1,1,0},true );
		myHMM.generateSequences(new int[]{1,2,3,2,0,1,1,1,1,0,1,4,2,1,0,1,1,1,1,0},true );
		myHMM.generateSequences(new int[]{1,2,4,4,0,1,1,1,1,0,1,4,2,1,0,1,1,1,1,0},true );
		myHMM.generateSequences(new int[]{1,2,3,4,0,1,1,1,1,0,1,4,2,1,0,1,1,1,1,0},true );*/
		//myHMM.learnUsingBaumWelch(100);
		
		//System.out.println(myHMM.printHMM());
		/*System.out.println(myHMM.observationProbability(new Integer[]{1,2,4,4,0}));
		
	
		
		DBMS conn=new DBMS();
		conn.connect(Configuration.host, Configuration.port);
		try {
			myHMM.saveHMM("TATA_HMM",conn);
		} catch (TotalADSDBMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		DBMS conn=new DBMS();
		conn.connect(Configuration.host, Configuration.port);
		HmmJahmm myHMM=new HmmJahmm();
		myHMM.loadHmm(conn, "TATA_HMM");
		//myHMM.saveHMM();
		//sequences.add
		//hmm.learnUsingBaumWelch(numIterations, initialHMM, sequences); 
		
		
				
		
		
	}
}

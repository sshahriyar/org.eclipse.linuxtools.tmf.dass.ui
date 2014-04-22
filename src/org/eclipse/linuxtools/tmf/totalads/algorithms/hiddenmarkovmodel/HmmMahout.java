package org.eclipse.linuxtools.tmf.totalads.algorithms.hiddenmarkovmodel;

import java.util.*;

import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.dbms.DBMS;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;






import org.apache.mahout.classifier.sequencelearning.hmm.*;
import org.apache.mahout.math.DenseMatrix;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
/**
 * 
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class HmmMahout {
	
	private HmmModel hmm;
	private int numStates;
	private int numSymbols;
	
	/**
	 * Initializes Hidden Markov Model with random initial probabilities
	 * @param numSymbols
	 * @param numStates
	 * 
	 */
	public void initializeHMM(int numSymbols, int numStates){
		
		this.numSymbols=numSymbols;
		this.numStates=numStates;
		hmm=new HmmModel(numStates, numSymbols);
		
	}
	
	/**
	 * Validates settings and saves them into the database after creating a new database if required
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
			   	}else if (SettingsCollection.PROBABILITY_THRESHOLD.toString().equalsIgnoreCase(settings[i])){
					  try {
						   Double prob=Double.parseDouble(settings[i+1]);
						   if (prob >1.0) throw new TotalADSUIException("Probability can't be > 1");
						   settingObject.add(SettingsCollection.PROBABILITY_THRESHOLD.toString().toString(), new JsonPrimitive(prob));
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
				 settings[6]=SettingsCollection.PROBABILITY_THRESHOLD.toString();
				 settings[7]=dbObject.get(SettingsCollection.PROBABILITY_THRESHOLD.toString()).toString();
				 cursor.close();
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
	 * Trains an HMM on a sequence using the BaumWelch algorithm
	 * @param numIterations
	 * @param observedSequence
	 */
	public  void learnUsingBaumWelch(Integer numIterations, Integer []observedSequence){
		
		int []seq=new int[observedSequence.length];
		for (int i=0;i<seq.length;i++)
			seq[i]=observedSequence[i];
		HmmTrainer.trainBaumWelch(hmm,seq , 0.0001, numIterations,false);
					
	}
	/**
	 * Trains an HMM on a sequence using the BaumWelch algorithm
	 * @param numIterations
	 * @param observedSequence
	 */
	public  void learnUsingBaumWelch(Integer numIterations, int []observedSequence){
		
		HmmTrainer.trainBaumWelch(hmm,observedSequence , 0.000000001, numIterations,false);
					
	}
	
	
	/**
	 * Returns the Observation probability of a sequences based on a model
	 * @param sequence Integer array of sequences
	 * @return
	 */
	public double observationLikelihood(int[] sequence){
		return HmmEvaluator.modelLikelihood(hmm, sequence, false);
	}
	/**
	 * Returns the Observation probability of a sequences based on a model
	 * @param sequence Integer array of sequences
	 * @return
	 */
	public double observationLikelihood(Integer[] sequence){
		int []seq=new int[sequence.length];
		for (int i=0; i<sequence.length;i++)
			seq[i]=sequence[i];
		return HmmEvaluator.modelLikelihood(hmm, seq, false);
	}
	/**
	 * Loads the model directly from a database
	 * @param connection
	 * @param database
	 */
	public void loadHmm(DBMS connection, String database){
	   
		DBCursor cursor=connection.selectAll(database, HmmModelCollection.COLLECTION_NAME.toString());
		if (cursor!=null){
				 Gson gson =new Gson();
				 if (cursor.hasNext()){	
					DBObject dbObject=cursor.next();
					Object emissionProb=dbObject.get(HmmModelCollection.EMISSIONPROB.toString());
					Object transsitionProb=dbObject.get(HmmModelCollection.EMISSIONPROB.toString());
					Object initialProb=dbObject.get(HmmModelCollection.EMISSIONPROB.toString());
					
					DenseMatrix emissionMatrix = gson.fromJson(emissionProb.toString(), DenseMatrix.class);
					DenseMatrix transitionMatrix = gson.fromJson(transsitionProb.toString(), DenseMatrix.class);
					DenseVector initialProbVector=gson.fromJson(initialProb.toString(), DenseVector.class);
					
					hmm=new HmmModel(transitionMatrix, emissionMatrix, initialProbVector);
				}
				cursor.close(); 
		}
	}
	
	/**
	 * This functions saves the HmmJahmm model into the database
	 * @param database
	 * @param connection
	 * @throws TotalADSDBMSException
	 */
	public void saveHMM(String database, DBMS connection) throws TotalADSDBMSException{
		
		
		   /// Inserting the states and probabilities
			// Creating states ids
			String key="hmm";
			Gson gson=new Gson();
			
			DenseMatrix emissionMatrix=(DenseMatrix) hmm.getEmissionMatrix();
			DenseMatrix transitionMatrix=(DenseMatrix) hmm.getTransitionMatrix();
			Vector initialProb=  hmm.getInitialProbabilities();
			
			
			JsonObject hmmDoc=new JsonObject();
			hmmDoc.add(HmmModelCollection.KEY.toString(),new JsonPrimitive(key));
			hmmDoc.add(HmmModelCollection.EMISSIONPROB.toString(), gson.toJsonTree(emissionMatrix));
			hmmDoc.add(HmmModelCollection.TRANSITIONPROB.toString(), gson.toJsonTree(transitionMatrix));
			hmmDoc.add(HmmModelCollection.INTITIALPROB.toString(), gson.toJsonTree(initialProb));
			
			// Creating id for query searching
			JsonObject jsonTheKey=new JsonObject();
			jsonTheKey.addProperty(HmmModelCollection.KEY.toString(),key);
			
			System.out.println(hmmDoc.toString());
			
			
			connection.insertOrUpdateUsingJSON(database, jsonTheKey, hmmDoc,HmmModelCollection.COLLECTION_NAME.toString());
		
	}
	
	/**
	 * Used for verification/testing of the model
	 * @return
	 */
	public String printHMM(){
		
		return hmm.toString();
		
	}
	/**
	 * 
	 * @param args
	 */
	public static void main (String args[]){
	
		/*HmmModel hmmModel=new HmmModel(5, 20);
		int a[]={1,2,10,8,7,6,7,8,9,0,19,18};
		
		System.out.println(hmmModel.getEmissionMatrix());
		System.out.println(hmmModel.getTransitionMatrix());
		HmmTrainer.trainBaumWelch(hmmModel,a , 0.000000001, 10,true);
		System.out.println(hmmModel.getEmissionMatrix());
		System.out.println(hmmModel.getTransitionMatrix());
		*/
		HmmMahout hmm=new HmmMahout();
		hmm.initializeHMM(100, 20);
		HmmModel hmmModel=new HmmModel(3, 5);
		DenseMatrix emission=(DenseMatrix) hmmModel.getEmissionMatrix();
		DenseMatrix transition=(DenseMatrix) hmmModel.getTransitionMatrix();
		DenseVector initialProb= (DenseVector)hmmModel.getInitialProbabilities();
		System.out.println(emission);
		//System.out.println(hmmModel.getTransitionMatrix());
		
		Gson gson=new Gson();
		JsonElement em=gson.toJsonTree(initialProb);
		System.out.println("eee");
		System.out.println(em.toString());
		DenseVector e=  gson.fromJson(em, DenseVector.class);
		System.out.println("jjj");
		System.out.println(e);
		
		DBMS conn=new DBMS();
		conn.connect(Configuration.host, Configuration.port);
		try{
			hmm.saveHMM("HIJ_HMM", conn);
			hmm.loadHmm(conn, "HIJ_HMM");
		} catch (Exception ex){
			ex.printStackTrace();
		}
		
		//System.out.println(gson.toJson(hmm));
	}
}

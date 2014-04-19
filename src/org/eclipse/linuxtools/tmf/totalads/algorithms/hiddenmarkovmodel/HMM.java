package org.eclipse.linuxtools.tmf.totalads.algorithms.hiddenmarkovmodel;
import java.util.*;

import org.omg.CORBA.TRANSIENT;

import be.ac.ulg.montefiore.run.jahmm.*;
import be.ac.ulg.montefiore.run.jahmm.draw.GenericHmmDrawerDot;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.toolbox.KullbackLeiblerDistanceCalculator;
import be.ac.ulg.montefiore.run.jahmm.toolbox.MarkovGenerator;

public class HMM {

	public enum Packet {
		OK, LOSS;
		
		public ObservationDiscrete<Packet> observation() {
			return new ObservationDiscrete<Packet>(this);
		}
	};
	
	public Hmm<ObservationInteger> initializeHMM(int numSymbols, int numStates){
		
		OpdfIntegerFactory factory = new OpdfIntegerFactory(numSymbols);
		Hmm<ObservationInteger> hmm = new Hmm<ObservationInteger>(numStates,factory);
		
		Random random = new Random();
		double start=0.0001;
		double end=1.0000;
		double tansitionProbabilities[][]=new double[numStates][numStates];
		for (int row=0;row<numStates; row++)
		for (int col = 0; col <= numStates*numStates; col++){
			tansitionProbabilities[row][col]=getRandomRealNumber(start, end, random);
		 }
	
		hmm.setPi(0, 0.95);
		hmm.setPi(1, 0.05);
			
		hmm.setOpdf(0, new OpdfInteger(new double[] { 0.95, 0.05 }));
		hmm.setOpdf(1, new OpdfInteger(					new double[] { 0.20, 0.80 }));
			
		hmm.setAij(0, 1, 0.05);
		hmm.setAij(0, 0, 0.95);
		hmm.setAij(1, 0, 0.10);
		hmm.setAij(1, 1, 0.90);
		return hmm;
	}
	
	/**
	 * 
	 * @param start
	 * @param end
	 * @param aRandom
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
		  
	
	public static void main (String args[]){
		HMM hmm=new HMM();
		hmm.initializeHMM(4, 5);
	}
}

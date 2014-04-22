package org.eclipse.linuxtools.tmf.totalads.algorithms.hiddenmarkovmodel;
/**
 * This class is used to fill properties of an HmmJahmm model by using Gson library from the database
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
class State {
	// The variable names (with their case) must be same as the db names; otherwise, gson library won't populate the class
	private double Pi; 
	private double []Aij;
	private double []Opdf;
	
	public State() {
		
	}
	/**
	 * Gets initial state probability
	 * @return double value
	 */
	public double getInitialProb(){
		return Pi;
	}
	/**
	 * Sets the initial state probability 
	 * @param pi
	 */
	public void setInitialProb(double pi){
		this.Pi=pi;
	}
	/**
	 * Gets transition matrix for a state
	 * @return double value
	 */
	public double[] getTransition(){
		return Aij;
	}
	/**
	 * Sets the transition matrix of a state
	 * @param aij transition matrix of one dimension
	 */
	public void setTransition(double []aij){
		this.Aij=aij;
	}
	/**
	 * Gets emission matrix for a state
	 * @return double value
	 */
	public double[] getEmission(){
		return Opdf;
	}
	/**
	 * Sets the emission matrix for a state
	 * @param oij a matrix of one dimension
	 */
	public void setEmission(double []opdf){
		this.Opdf=opdf;
	}
	/**
	 * Returns the size of a transition matrix
	 * @return size
	 */
	public int getTransitionSize(){
		return Aij.length;
	}
	/**
	 * Returns the size of emission matrix
	 * @return size
	 */
	public int getEmissionSize(){
		return Opdf.length;
	}
	
}

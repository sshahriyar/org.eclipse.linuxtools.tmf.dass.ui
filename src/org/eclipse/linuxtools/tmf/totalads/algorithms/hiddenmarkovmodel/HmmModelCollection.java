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
/**
 * This class represents the fields of the trace collection  for HmmCore in the DB.
 * 
 *  @author <p>Syed Shariyar Murtaza justsshary@hotmail.com</p>
 *
 */
enum HmmModelCollection{
	
	/**
	 * The name of collection itself
	 */
	COLLECTION_NAME("hmm_model"),
	/**
	 * Key field name
	 */
	 KEY("_id"),
	 /**
	  * State properties
	  */
	 MODEL("model"),
	 /**
	  * Initial state probabilities, found within the state document
	  */
	 STATE_INTITIALPROB("Pi"),
	 /**
	  * Transition probabilities, found within the state document
	  */
	 STATE_TRANSITION("Aij"),
	 /**
	  * Emission probabilities, found within the state document
	  */
	 STATE_EMISSION("Opdf");
	/**
	 * Private field to hold the name
	 */
	private String fieldName;
	/**
	 * Constructor
	 * @param fieldName
	 */
	private HmmModelCollection(String fieldName){
		this.fieldName=fieldName;
	}
	/**
	 * Returns the String Value of the FieldName
	 */
	 @Override
	 public String toString() {
	      return fieldName;
	 }

	

	
}
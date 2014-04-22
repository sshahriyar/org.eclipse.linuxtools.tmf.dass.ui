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
 * This class represents the fields of the settings collection for HmmCore in the DB.
 * 
 *  @author <p>Syed Shariyar Murtaza justsshary@hotmail.com</p>
 *
 */
enum SettingsCollection{
	
	/**
	 * The name of collection itself
	 */
	COLLECTION_NAME("settings"),
	/**
	 * Key field name
	 */
	 KEY("_id"),
	 /**
	  * Number of states
	  */
	 NUM_STATES("num_states"),
	 /**
	  * Number of symbols
	  */
	 NUM_SYMBOLS("num_symbols"),
	 /**
	  * Sequence length
	  */
	 SEQ_LENGTH("seq_length"),
	 /**
	  * Probability threshold
	  */
	 PROBABILITY_THRESHOLD("prob_threshold");
	 
	private String fieldName;
	/**
	 * Constructor
	 * @param fieldName
	 */
	private SettingsCollection(String fieldName){
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
/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/

package org.eclipse.linuxtools.tmf.totalads.algorithms.slidingwindow;
/**
 * This class represents the fields of the settings collection in the MongoDB.
 * 
 *  @author <p>Syed Shariyar Murtaza justsshary@hotmail.com</p>
 *
 */
enum SettingsCollection{
	/**
	 * The Key name in the collection
	 */
	KEY("_id"),
	/**
	 * Max win field in the collection
	 */
	MAX_WIN("maxWIN"),
	/**
	 * MAx hamming distance field in the collection
	 */
	MAX_HAM_DIS("maxHamDis"),
	/**
	 * The name of collection itself
	 */
	COLLECTION_NAME("settings");
	
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
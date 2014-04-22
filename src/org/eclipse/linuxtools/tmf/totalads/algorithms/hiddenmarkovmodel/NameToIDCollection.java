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
 * This class represents the fields of the name to id collection for HmmJahmm in the DB.
 * It stores the name of the event with its corresponding integer number
 *  @author <p>Syed Shariyar Murtaza justsshary@hotmail.com</p>
 *
 */
enum NameToIDCollection{
	
	/**
	 * The name of collection itself
	 */
	COLLECTION_NAME("name_to_id"),
	/**
	 * Key field name
	 */
	 KEY("_id"),
	 /**
	  * Mapper name, this field will contain the map of key to ids
	  * it can be converted to a hashmap directly
	  */
	 MAP("map");
	 
	 
	private String fieldName;
	/**
	 * Constructor
	 * @param fieldName
	 */
	private NameToIDCollection(String fieldName){
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
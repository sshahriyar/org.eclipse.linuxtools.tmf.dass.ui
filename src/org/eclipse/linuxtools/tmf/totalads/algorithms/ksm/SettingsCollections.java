/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.algorithms.ksm;

/**
 * This class represents the fields of  a settings collection in the MongoDB.
 * 
 *  @author <p>Syed Shariyar Murtaza justsshary@hotmail.com</p>
 *
 */
enum SettingsCollections{
	/**
	 * The Key name in the collection
	 */
	KEY("_id"),
	/**
	 * Alpha field in the collection
	 */
	ALPHA("alpha"),
	/**
	 * Versions of the kernels
	 */
	KernelVersions("kernel_versions"),
	/**
	 * Update_time field in the collection
	 */
	UPDATE_TIME("update_time");
	
	private String fieldName;
	/**
	 * Constructor
	 * @param fieldName
	 */
	private SettingsCollections(String fieldName){
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
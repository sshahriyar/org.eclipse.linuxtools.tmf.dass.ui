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
 * This class represents the fields of  a trace collection in the MongoDB.
 * In the case of trace collection the fields are the system call tree generated from the trace
 *  @author <p>Syed Shariyar Murtaza justsshary@hotmail.com</p>
 *
 */
enum TraceCollection{
	
	/**
	 * The name of collection itself
	 */
	COLLECTION_NAME("trace_data"),
	/**
	 * name
	 */
	KEY("_id"),
	/**
	 * Tree field
	 */
	TREE("tree");
	
	private String fieldName;
	/**
	 * Constructor
	 * @param fieldName
	 */
	private TraceCollection(String fieldName){
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
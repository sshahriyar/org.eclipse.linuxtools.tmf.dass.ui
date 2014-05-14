/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.ui.properties;
/**
 *  A class representing name-value pairs of Settings array to display in the TableViewer
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 */
class NameVal{
	public String name;
	public String val;
	/**
	 * Creates the NameVal object
	 * @param name
	 * @param val
	 */
	public NameVal(String key, String val){
		this.name=key;
		this.val=val;
	}
	/**
	 * Gets the name
	 * @return name
	 */
	public String  getName(){
		return name;
	}
	/**
	 * Gets the value
	 * @return value
	 */
	public String getVal(){
		return val;
	}
	
}
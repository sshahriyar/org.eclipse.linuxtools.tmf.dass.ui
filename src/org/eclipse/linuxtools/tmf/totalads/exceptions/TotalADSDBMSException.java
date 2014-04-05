/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.exceptions;
/**
 * This class defines custom DBMS exceptions that are thrown when a user does not select the proper configurations 
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com</p>
 *
 */
public class TotalADSDBMSException extends Exception {

	public TotalADSDBMSException() {
		
	}

	public TotalADSDBMSException(String message) {
		super(message);
		
	}

	public TotalADSDBMSException(Throwable cause) {
		super(cause);
		
	}

	public TotalADSDBMSException(String message, Throwable cause) {
		super(message, cause);
		
	}

}

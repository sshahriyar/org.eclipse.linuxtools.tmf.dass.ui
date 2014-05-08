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
 * This class defines custom UI exceptions that are thrown when a user does not select the proper 
 * User Interface settings
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class TotalADSGeneralException extends Exception {

	public TotalADSGeneralException() {
		
	}

	public TotalADSGeneralException(String message) {
		super(message);
		
	}

	public TotalADSGeneralException(Throwable cause) {
		super(cause);
		
	}

	public TotalADSGeneralException(String message, Throwable cause) {
		super(message, cause);
		
	}

}

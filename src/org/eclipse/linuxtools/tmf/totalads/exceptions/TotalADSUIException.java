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
public class TotalADSUIException extends Exception {

	public TotalADSUIException() {
		
	}

	public TotalADSUIException(String message) {
		super(message);
		
	}

	public TotalADSUIException(Throwable cause) {
		super(cause);
		
	}

	public TotalADSUIException(String message, Throwable cause) {
		super(message, cause);
		
	}

}
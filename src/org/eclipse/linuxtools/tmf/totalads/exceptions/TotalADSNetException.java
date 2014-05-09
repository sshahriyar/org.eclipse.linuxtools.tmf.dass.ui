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
 * This class defines custom SSH and remote newotrk communication related exceptions
 * IDBMS configurations 
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com</p>
 *
 */
public class TotalADSNetException extends Exception {

	public TotalADSNetException() {
		
	}

	public TotalADSNetException(String message) {
		super(message);
		
	}

	public TotalADSNetException(Throwable cause) {
		super(cause);
		
	}

	public TotalADSNetException(String message, Throwable cause) {
		super(message, cause);
		
	}

}

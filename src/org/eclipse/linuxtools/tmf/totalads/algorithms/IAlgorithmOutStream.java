/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/

package org.eclipse.linuxtools.tmf.totalads.algorithms;
/**
 * An interface to display the outputs of algorithms
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public interface IAlgorithmOutStream {
	/**
	 * Adds new event to the output stream
	 * @param event Information to display on the output stream
	 */
	
	public void addOutputEvent(String event);


	/**
	 *Adds new line to the output Stream
	 */
	public void addNewLine();
}

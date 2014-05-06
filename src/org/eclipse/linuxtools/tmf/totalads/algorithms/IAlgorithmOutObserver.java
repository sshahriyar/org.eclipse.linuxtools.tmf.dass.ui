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
 * An interface that is required to be implemented by the class that displays the output of the algorithms
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public interface IAlgorithmOutObserver {
	/**
	 * This method gets called from the {@link AlgorithmOutStream} 
	 * @param message The message to be displayed
	 */
	public void updateOutput(String message);
}

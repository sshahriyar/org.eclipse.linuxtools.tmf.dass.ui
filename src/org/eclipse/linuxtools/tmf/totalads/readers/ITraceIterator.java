/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.readers;

import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;

/** Iterator to move through the trace and read events one by one  without loading the whole trace 
 *  in memory
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 **/
public interface ITraceIterator {
	/** Moves Iterator to the next event, and returns true if the iterator can advance or false if the iterator cannot advance 
	 * @throws TotalADSReaderException **/ 
	public boolean advance() throws TotalADSReaderException;
	/** Returns an event for the location of the iterator **/ 
	public String getCurrentEvent();
	/** Closes the iterator stream 
	 * @throws TotalADSReaderException **/
	public void close() throws TotalADSReaderException;

}

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

import java.io.File;

import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfEvent;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;

/** Each trace type parser should implement the functions of this interface and, in addition, a static
 * function registerTraceTypeReader
 * @author  <p>Syed Shariyar Murtaza justsshary@hotmail.com </p>
 */
public interface ITraceTypeReader {
	/** Returns a self created instance of the model**/
	public ITraceTypeReader createInstance();
	/** Gets the name of the model**/
	public String getName();
	/** Returns the acronym of the Trace Reader; should only be three characters Long  */
	public String getAcronym();
	/**
	 * Gets a trace from a file 
	 * @param file file object representing the file
	 * @return Iterator to the trace
	 * @throws TotalADSReaderException
	 */
	public ITraceIterator getTraceIterator(File file) throws TotalADSReaderException;

}

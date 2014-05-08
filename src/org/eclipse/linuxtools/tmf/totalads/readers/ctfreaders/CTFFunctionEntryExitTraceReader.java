/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.readers.ctfreaders;

import java.io.File;

import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfEvent;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
/**
 * This class is not implmented yet.  It is difficult to implement this as this requires mapping of symbol files because lttng generates only addresses
* not the function names. This is just a structure for future
 * @author <p> Syed Shariyar Murtaza justsshary@hotmai.com</p>
 *
 */
public class CTFFunctionEntryExitTraceReader implements ITraceTypeReader {
	/**
	 * Constructor
	 */
	public CTFFunctionEntryExitTraceReader() {
		
	}

	@Override
	public String getName() {
		
		return "CTF Function Entry/Exit Reader";
	}
	@Override
	public ITraceTypeReader createInstance(){
		return new  CTFFunctionEntryExitTraceReader();
	}
	
	/**
     * Returns the acronym of the User space Reader
     */
    public String getAcronym(){
    	
    	return "FUN";
    }
	 public static void registerTraceTypeReader() throws TotalADSGeneralException{
	    	TraceTypeFactory trcTypFactory=TraceTypeFactory.getInstance();
	    	CTFFunctionEntryExitTraceReader userTraceReader=new CTFFunctionEntryExitTraceReader();
	    	trcTypFactory.registerTraceReaderWithFactory(userTraceReader.getName(), userTraceReader);
	    }
	 
	@Override
	public ITraceIterator getTraceIterator(File file) throws TotalADSReaderException {
	
		return null;
	}



}

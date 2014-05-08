/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.readers.textreaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

//import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfEvent;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;
/**
 * This class reads a text file and returns whatever is written on a line.
 * Every line is transformed into a line
 * @author <Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class TextLineTraceReader implements ITraceTypeReader {
	//--------------------------------------------------------------------------- 
	//Inner class: Implemnts the iterator to iterate through the text file
	//---------------------------------------------------------------------------
	private class TextLineIterator implements ITraceIterator{
		private BufferedReader bufferedReader;
		private String event="";
		private Boolean isClose=false;
		private String regEx="";
		public TextLineIterator(BufferedReader  bReader){
			bufferedReader=bReader;
			regEx=""; //[| ]+([a-zA-Z0-9_:-]+)[ ]+.*"; //function call extraction pattern
		}
		/**
		 * Advance
		 * @throws TotalADSReaderException 
		 */
		@Override
		public boolean advance() throws TotalADSReaderException  {
		   boolean isAdvance=false;
		   try {
				do { 
				   	event=bufferedReader.readLine();
					 
					 if (event==null){
						  bufferedReader.close();
						  isClose=true;
						  isAdvance=false;
					 }
					 else{
						 isAdvance=true;
						 if(!regEx.isEmpty())
							 event= event.replaceAll(regEx, "$1");
						 event=event.trim();
					 }
				}while(event!=null && event.isEmpty());// if there are empty lines or there is no match on regex on a line, no need to send an event.
										// keep looping till the end of file.
				 
				
			} catch (IOException e) {
				
				throw new TotalADSReaderException(e.getMessage());
			} 
		   return isAdvance;
		}
		/**
		 * Returns the Current event
		 */
		@Override
		public String getCurrentEvent() {
			
			return event;
		}
		/**
		 * Closes the iterator
		 * @throws TotalADSReaderException 
		 */
		@Override
		public void close() throws TotalADSReaderException {
			try {
				if (!isClose)
					bufferedReader.close();
			} catch (IOException e) {
				
				throw new TotalADSReaderException(e.getMessage());
			}
			
		}
		
		
	}
	//--------------------------------------------------------------------------------
	// Inner class ends
	//--------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public TextLineTraceReader() {
	
	}
	
	@Override
	public ITraceTypeReader createInstance(){
		return new TextLineTraceReader();
	}
	@Override
	public String getName() {
	
		return "Text File (TXT)";
	}

	 /**
     * Returns the acronym of the text reader
     */
    public String getAcronym(){
    	
    	return "TXT";
    }
    /**
     * Returns the trace iterator
     */
	@Override
	public ITraceIterator getTraceIterator(File file) throws TotalADSReaderException {
		
		BufferedReader bufferReader;
		try {
			bufferReader = new BufferedReader(new FileReader(file));
			TextLineIterator textLineIterator=new TextLineIterator(bufferReader);
			return textLineIterator;
			
		} catch (FileNotFoundException e) {
			throw new TotalADSReaderException(e.getMessage());
		}
	}

	/**
	 * Registers Itself with the Trace Type Reader 
	 * @throws TotalADSGeneralException
	 */
	
	 public static void registerTraceTypeReader() throws TotalADSGeneralException{
	    	TraceTypeFactory trcTypFactory=TraceTypeFactory.getInstance();
	    	TextLineTraceReader textFileReader=new TextLineTraceReader();
	    	trcTypFactory.registerTraceReaderWithFactory(textFileReader.getName(), textFileReader);
	    }
}

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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.readers.ctfreaders.CTFEventsTraceReader;
import org.eclipse.linuxtools.tmf.totalads.readers.ctfreaders.CTFFunctionEntryExitTraceReader;
import org.eclipse.linuxtools.tmf.totalads.readers.ctfreaders.CTFLTTngSysCallTraceReader;
import org.eclipse.linuxtools.tmf.totalads.readers.textreaders.TextLineTraceReader;
import org.eclipse.linuxtools.tmf.totalads.readers.textreaders.TextSysIDtoNameTraceReader;
/**
 * This class registers all the trace readers with itself by following a factory pattern.
 * It is a singleton and whenever an instance of any type of trace reader is needed, this class
 * should be called
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */

public class TraceTypeFactory {

	//Data variables
	private static TraceTypeFactory traceTypes=null;
	private HashMap<String,ITraceTypeReader> traceTypeReadersList=null;
	/**
	 * Constructor 
	 */
	private TraceTypeFactory( ){
		traceTypeReadersList=new HashMap<String,ITraceTypeReader>();
	}
	/**
	 * Creates the instance of TraceTypeFactory
	 * @return TraceTypeFactory
	 */
	public static TraceTypeFactory getInstance(){
		if (traceTypes==null)
			traceTypes=new TraceTypeFactory();
		return traceTypes;
	}
	/**
	 * Destroys the instance of factory if already exists
	 * This code is necessary because when Eclipse is running and TotalADS window is closed and reopened, 
	 * the static object is not recreated on the creation of new Object of TotalADS
	 */
	public static void destroyInstance(){
		if (traceTypes!=null)
			traceTypes=null;
	}
	/**
	 * Returns a trace type reader based on the key
	 * @return A reader of a selected type
	 */
	public ITraceTypeReader getTraceReader(String key){
		ITraceTypeReader reader= traceTypeReadersList.get(key);
		return reader.createInstance();
		
	}
	
	/**
	 * Get all trace type readers
	 * @return An array of Trace type readers
	 */
	public ITraceTypeReader[] getAllTraceReaders(){
		ITraceTypeReader [] traceReaders=new ITraceTypeReader[traceTypeReadersList.size()]; 
		int indx=0;
		
		for (Map.Entry<String, ITraceTypeReader> list:traceTypeReadersList.entrySet()){
			traceReaders[indx]=list.getValue();
			indx++;
		}
		
		
		return traceReaders;
		
	}
	/**
	 * Get all trace type reader keys
	 * @return All the keys  of trace type readers 
	 */
	public String[] getAllTraceTypeReaderKeys(){
		String [] keys=new String[traceTypeReadersList.size()]; 
		keys=traceTypeReadersList.keySet().toArray(keys);
		return keys;
		
	}
	/**
	 * Registers a trace reader with the factory
	 * @param key Key for the trace reader
	 * @param traceReader Instance of the trace reader
	 * @throws TotalADSUIException Exception 
	 */
	public void registerTraceReaderWithFactory(String key, ITraceTypeReader traceReader) throws TotalADSUIException{
		if (!key.isEmpty()){
			ITraceTypeReader  reader=traceTypeReadersList.get(key);
			if (reader==null)
				traceTypeReadersList.put(key, traceReader);
			else
				throw new TotalADSUIException("Duplicate Key!");
		}
		else 
			 throw new TotalADSUIException("Key is Empty!");
		
			
	}
	/**
	 * Gets the Kernel or User space reader. Written to be used in the initialization of a trace
	 * @param isKernel Is it a kernel or not
	 * @return The reader
	 */
	public ITraceTypeReader getCTFKernelorUserReader(Boolean isKernel){
		if (isKernel)
			return new CTFLTTngSysCallTraceReader();
		else
			return new CTFFunctionEntryExitTraceReader();
	}
	
	/**
	 * This function initializes the Trace readers. Currently Trace readers are manually registered
	 * in this function but in future reflection will be used to automatically register them
	 * @throws Exception
	 */
	public void initialize() throws TotalADSUIException{
		
	    //Reflections reflections = new Reflections("org.eclipse.linuxtools.tmf.totalads.ui");
	    //java.util.Set<Class<? extends IDetectionAlgorithm>> modules = reflections.getSubTypesOf
		//		 							(org.eclipse.linuxtools.tmf.totalads.ui.IDetectionModels.class);
		// The following code needs to be replaced with reflection in future versions
		CTFLTTngSysCallTraceReader.registerTraceTypeReader();
		//CTFEventsTraceReader.registerTraceTypeReader();
		//CTFFunctionEntryExitTraceReader.registerTraceTypeReader();
		TextLineTraceReader.registerTraceTypeReader();
		TextSysIDtoNameTraceReader.registerTraceTypeReader();
		
	}
	
}

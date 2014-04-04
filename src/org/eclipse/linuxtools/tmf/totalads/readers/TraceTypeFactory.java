package org.eclipse.linuxtools.tmf.totalads.readers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUiException;
import org.eclipse.linuxtools.tmf.totalads.readers.ctfreaders.CTFEventsTraceReader;
import org.eclipse.linuxtools.tmf.totalads.readers.ctfreaders.CTFFunctionEntryExitTraceReader;
import org.eclipse.linuxtools.tmf.totalads.readers.ctfreaders.CTFSysCallTraceReader;
import org.eclipse.linuxtools.tmf.totalads.readers.textreaders.TextLineTraceReader;
import org.eclipse.linuxtools.tmf.totalads.readers.textreaders.TextSysIDtoNameTraceReader;


public class TraceTypeFactory {


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
	 * This code is necessary because when Eclipse is running and TotalADS window is closed and reopened, the static
	 * object is not recreated on the creation of new Object of TotalADS
	 */
	public static void destroyInstance(){
		if (traceTypes!=null)
			traceTypes=null;
	}
	/**
	 * Returns a trace type reader based on the key
	 * @return
	 */
	public ITraceTypeReader getTraceReader(String key){
		ITraceTypeReader reader= traceTypeReadersList.get(key);
		return reader.createInstance();
		
	}
	
	/**
	 * Get all trace type readers
	 * @return
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
	 * @return
	 */
	public String[] getAllTraceTypeReaderKeys(){
		String [] keys=new String[traceTypeReadersList.size()]; 
		keys=traceTypeReadersList.keySet().toArray(keys);
		return keys;
		
	}
	/**
	 * 
	 * @param key Acronym of the trace reader
	 * @param traceReader Instance of the trace reader
	 * @throws TotalADSUiException Exception 
	 */
	public void registerTraceReaderWithFactory(String key, ITraceTypeReader traceReader) throws TotalADSUiException{
		if (!key.isEmpty()){
			ITraceTypeReader  reader=traceTypeReadersList.get(key);
			if (reader==null)
				traceTypeReadersList.put(key, traceReader);
			else
				throw new TotalADSUiException("Duplicate Key!");
		}
		else 
			 throw new TotalADSUiException("Key is Empty!");
		
			
	}
	/**
	 * 
	 * @param isKernel
	 * @return
	 */
	public ITraceTypeReader getCTFKernelorUserReader(Boolean isKernel){
		if (isKernel)
			return new CTFSysCallTraceReader();
		else
			return new CTFFunctionEntryExitTraceReader();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void initialize() throws TotalADSUiException{
		
	    //Reflections reflections = new Reflections("org.eclipse.linuxtools.tmf.totalads.ui");
	    //java.util.Set<Class<? extends IDetectionAlgorithm>> modules = reflections.getSubTypesOf
		//		 							(org.eclipse.linuxtools.tmf.totalads.ui.IDetectionModels.class);
		// The following code needs to be replaced with reflection in future versions
		CTFSysCallTraceReader.registerTraceTypeReader();
		//CTFEventsTraceReader.registerTraceTypeReader();
		//CTFFunctionEntryExitTraceReader.registerTraceTypeReader();
		TextLineTraceReader.registerTraceTypeReader();
		TextSysIDtoNameTraceReader.registerTraceTypeReader();
		
	}
	
}

package org.eclipse.linuxtools.tmf.totalads.ui;

import java.util.HashMap;


public class TraceTypeFactory {


	private static TraceTypeFactory traceTypes=null;
	
	private HashMap<String,ITraceTypeReader> traceTypeReadersList=null;
	/**
	 * 
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
	 * 
	 * @return
	 */
	public ITraceTypeReader getTraceReader(String key){
		ITraceTypeReader reader= traceTypeReadersList.get(key);
		return reader;
		
	}
	/**
	 * 
	 * @return
	 */
	public String[] getAllTraceTypeReaderKeys(){
		String [] keys=new String[traceTypeReadersList.size()]; 
		keys=traceTypeReadersList.keySet().toArray(keys);
		return keys;
		
	}
	/**
	 * 
	 * @param detectionModel
	 * @param modelType
	 */
	public void registerModelWithFactory(String key, ITraceTypeReader traceReader) throws Exception{
		if (!key.isEmpty()){
			ITraceTypeReader  reader=traceTypeReadersList.get(key);
			if (reader==null)
				traceTypeReadersList.put(key, traceReader);
			else
				throw new Exception("Duplicate Key!");
		}
		else 
			 throw new Exception("Key is Empty!");
		
			
	}
	/**
	 * 
	 * @param isKernel
	 * @return
	 */
	public ITraceTypeReader getCTFKernelorUserReader(Boolean isKernel){
		if (isKernel)
			return new CTFKernelTraceReader();
		else
			return new CTFUserTraceReader();
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	public void initialize() throws Exception{
		
	    //Reflections reflections = new Reflections("org.eclipse.linuxtools.tmf.totalads.ui");
	    //java.util.Set<Class<? extends IDetectionModels>> modules = reflections.getSubTypesOf
		//		 							(org.eclipse.linuxtools.tmf.totalads.ui.IDetectionModels.class);
		// The following code needs to be replaced with reflection in future versions
		CTFKernelTraceReader.registerTraceTypeReader();
		CTFUserTraceReader.registerTraceTypeReader();
		TextLineTraceReader.registerTraceTypeReader();
		
	}
	
}

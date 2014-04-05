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

//import org.eclipse.linuxtools.lttng2.kernel.core.*;
//import org.eclipse.linuxtools.ctf.core.trace.CTFReaderException;
//import org.eclipse.linuxtools.ctf.core.trace.CTFTrace;
//import org.eclipse.linuxtools.internal.lttng2.kernel.core.Attributes;
//import org.eclipse.linuxtools.internal.lttng2.kernel.core.LttngStrings;
import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfIterator;
import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfEvent;
import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfTrace;
//import org.eclipse.linuxtools.tmf.core.event.ITmfEventField;
import org.eclipse.linuxtools.tmf.core.exceptions.TmfTraceException;
//import org.eclipse.linuxtools.tmf.core.tests.shared.CtfTmfTestTrace;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSReaderException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;

/**
 * Class to read CTF traces by using {@link CtfTmfTrace} class.
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p> 
 */
public class CTFEventsTraceReader implements ITraceTypeReader   {
	 // ------------------------------------------------------------------------
     // Inner class: Implments an iterator   
	 // ------------------------------------------------------------------------
     private class CTFEventsIterator implements ITraceIterator {   
    	 private CtfIterator traceIterator=null;
    	 private CtfTmfTrace  trace=null;
    	 private Boolean isDispose=false;
    	  /**
    	   * Constructor
    	   * @param tmfTrace
    	   */
    	 public CTFEventsIterator(CtfTmfTrace  tmfTrace){
    		   trace=tmfTrace;
    		   traceIterator=tmfTrace.createIterator();
    	   }
    	  /** Moves Iterator to the next event, and returns true if the iterator can advance or false if the iterator cannot advance **/ 
    	   @Override
    	    public boolean advance(){
    			boolean isAdvance=traceIterator.advance();
    		
    			if (!isAdvance){
    				isDispose=true;
    				trace.dispose();
    			}
    				
    			return isAdvance;
    					
    		}
    		/** Returns the event for the location of the iterator  **/ 
    		@Override
    	    public String getCurrentEvent(){
    			
    			CtfTmfEvent event = traceIterator.getCurrentEvent();
    			String eventName=event.getEventName().trim();
        		System.out.println(eventName);
    			if (eventName.isEmpty())
    				return null;
    			else 
    				return null;
    			
    		}
    	
    		/** Closes the iterator stream **/
    		@Override
    		public void close(){
    			if (!isDispose)
    				trace.dispose();
    		}
      

     }
     
    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Instantiate a new trace reader
     *
     */
    public CTFEventsTraceReader() {
          //this.trace=trace;
          //this.traceBuffer=buffer;
    }
    /**
     * Creates an instance of the reader
     */
   @Override
    public ITraceTypeReader createInstance(){
    	return new CTFEventsTraceReader();
    }
   /**
    * Registers a Trace type reader
    * @throws TotalADSUIException
    */
    
    public static void registerTraceTypeReader() throws TotalADSUIException{
    	TraceTypeFactory trcTypFactory=TraceTypeFactory.getInstance();
    	CTFEventsTraceReader kernelTraceReader=new CTFEventsTraceReader();
    	trcTypFactory.registerTraceReaderWithFactory(kernelTraceReader.getName(), kernelTraceReader);
    }
    /**
     * Returns the name of the model
     */
    @Override
    public String getName(){
    	return "CTF All Events Reader";
    }
    /**
     * Returns the acronym of the Kernel space reader
     */
    public String getAcronym(){
    	
    	return "EVN";
    }
	/**
	 * Return the iterator to go over the trace file
	 * @param file File obkect
	 * @return The Iterator to iterate through a trace
	 */
    public ITraceIterator getTraceIterator(File file) throws TotalADSReaderException{
    	
    	 String filePath=file.getPath();
		 CtfTmfTrace  fTrace = new CtfTmfTrace();
	
		 try {
	            fTrace.initTrace(null, filePath, CtfTmfEvent.class);
	            
	      } catch (TmfTraceException e) {
	            /* Should not happen if tracesExist() passed */
	            throw new RuntimeException(e);
	      }
		 
		 return new CTFEventsIterator(fTrace);
    }
  
   
}


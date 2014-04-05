package org.eclipse.linuxtools.tmf.totalads.readers.ctfreaders;


import java.io.File;

import org.eclipse.linuxtools.lttng2.kernel.core.*;
import org.eclipse.linuxtools.ctf.core.trace.CTFReaderException;
import org.eclipse.linuxtools.ctf.core.trace.CTFTrace;
import org.eclipse.linuxtools.internal.lttng2.kernel.core.Attributes;
import org.eclipse.linuxtools.internal.lttng2.kernel.core.LttngStrings;
import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfIterator;
import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfEvent;
import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfTrace;
import org.eclipse.linuxtools.tmf.core.event.ITmfEventField;
import org.eclipse.linuxtools.tmf.core.exceptions.TmfTraceException;
import org.eclipse.linuxtools.tmf.core.tests.shared.CtfTmfTestTrace;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceIterator;
import org.eclipse.linuxtools.tmf.totalads.readers.ITraceTypeReader;
import org.eclipse.linuxtools.tmf.totalads.readers.TraceTypeFactory;

/**
 * Class to read CTF traces by using CtfTmfTrace class.
 * @author Syed Shariyar Murtaza 
 */
public class CTFEventsTraceReader implements ITraceTypeReader   {
	 // ------------------------------------------------------------------------
     // inner class   
	 // ------------------------------------------------------------------------
     class CTFKerenelIterator implements ITraceIterator {   
    	 CtfIterator traceIterator=null;
    	 CtfTmfTrace  trace=null;
    	 Boolean isDispose=false;
    	  
    	 public CTFKerenelIterator(CtfTmfTrace  tmfTrace){
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

   @Override
    public ITraceTypeReader createInstance(){
    	return new CTFEventsTraceReader();
    }
   /**
    * 
    * @throws Exception
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
	 * @param file
	 * @return
	 */
    public ITraceIterator getTraceIterator(File file){
    	
    	 String filePath=file.getPath();
		 CtfTmfTrace  fTrace = new CtfTmfTrace();
	
		 try {
	            fTrace.initTrace(null, filePath, CtfTmfEvent.class);
	            
	      } catch (TmfTraceException e) {
	            /* Should not happen if tracesExist() passed */
	            throw new RuntimeException(e);
	      }
		 
		 return new CTFKerenelIterator(fTrace);
    }
  

    
/**
 * Reads the trace
 * 
 
    
   private void readTrace(CtfTmfTrace  trace,StringBuilder traceBuffer){
    	
    	CtfIterator traceIterator=trace.createIterator();
    	 
    	while (traceIterator.advance()){
    		
    		CtfTmfEvent event = traceIterator.getCurrentEvent();
    		handleEvents(event, traceBuffer);
    		   		 
    		
    	}
   
    }*/
   /**
    * This function dispatches each event to its appropriate handle
    * @param event
    */			
/*@Override
public void handleEvents(CtfTmfEvent event, StringBuilder traceBuffer) {
	String eventName=event.getEventName();
	
	if (eventName.startsWith(LttngStrings.SYSCALL_PREFIX)){
          //  || eventName.startsWith(LttngStrings.COMPAT_SYSCALL_PREFIX)) {
					handleSysCallEntryEvent(event, traceBuffer);
					
	 }
	/*not needed right now, may be in the future it will be uncommented
	 * else if (eventName.equals(LttngStrings.EXIT_SYSCALL)){
					handleSysExitEvent(event);
	}*/
	
/*}
*/
/**
 * This is an event handler for system call exit event
 * @param event
 
private void handleSysExitEvent(CtfTmfEvent event, StringBuilder traceBuffer) {
	ITmfEventField content = event.getContent();
	ITmfEventField returnVal=content.getField("ret");
	System.out.println("Ret: "+returnVal.getValue()); 
  //  accumulator.add(event.getTimestamp(), field);// whatever internal storage you want to use.
}
*/
/**
 * This is an event handler for System call events 
 * @param event
 
private void handleSysCallEntryEvent(CtfTmfEvent event, StringBuilder traceBuffer) {
	String eventName=event.getEventName();
	//System.out.println(eventName);
	Integer id=MapSysCallIDToName.getSysCallID(eventName.trim());
	if (id==null){
		//throw new Exception("System call not found in the map: "+eventName);
		id=-1;
	}
	traceBuffer.append(id).append("\n");
}
*/

    
}


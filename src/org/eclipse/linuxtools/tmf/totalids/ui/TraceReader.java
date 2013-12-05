package org.eclipse.linuxtools.tmf.totalids.ui;


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

/**
 * Class to read CTF traces by using CtfTmfTrace class.
 * @author Syed Shariyar Murtaza 
 */
public class TraceReader   {
	
           
    CtfTmfTrace trace=null;
    StringBuilder traceBuffer=null;
    // ------------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------------

    /**
     * Instantiate a new trace reader
     *
     * @param trace
     *            The CTF trace
     */
    public TraceReader(CtfTmfTrace trace, StringBuilder buffer) {
          this.trace=trace;
          this.traceBuffer=buffer;
    }

    
/**
 * Reads the trace
 * 
 */
   public void readTrace(){
    	
    	CtfIterator traceIterator=trace.createIterator();
    	 
    	while (traceIterator.advance()){
    		
    		CtfTmfEvent event = traceIterator.getCurrentEvent();
    		handleEvents(event);
    		   		 
    		
    	}
   
    }
   /**
    * This function dispatches each event to its appropriate handle
    * @param event
    */			

private void handleEvents(CtfTmfEvent event){
	String eventName=event.getEventName();
	
	if (eventName.startsWith(LttngStrings.SYSCALL_PREFIX)
            || eventName.startsWith(LttngStrings.COMPAT_SYSCALL_PREFIX)) {
					handleSysCallEntryEvent(event);
					
	 }
	/*not needed right now, may be in the future it will be cuncommented
	 * else if (eventName.equals(LttngStrings.EXIT_SYSCALL)){
					handleSysExitEvent(event);
	}*/
	
}

/**
 * This is an event handler for system call exit event
 * @param event
 */
private void handleSysExitEvent(CtfTmfEvent event){
	ITmfEventField content = event.getContent();
	ITmfEventField returnVal=content.getField("ret");
	System.out.println("Ret: "+returnVal.getValue()); 
  //  accumulator.add(event.getTimestamp(), field);// whatever internal storage you want to use.
}

/**
 * This is an event handler for System call events 
 * @param event
 */
private void handleSysCallEntryEvent(CtfTmfEvent event){
	String eventName=event.getEventName();
	//System.out.println(eventName);
	
	traceBuffer.append(eventName).append("\n");
}

public static void main (String args[]){
	 //StringBuilder trace= new StringBuilder();
	 /*CtfTmfTestTrace testTrace = CtfTmfTestTrace.KERNEL;
	 TraceReader input = new TraceReader(testTrace.getTrace(),trace);
	 input.readTrace();
	 System.out.println(trace.toString());*/
	
	
	 

}


    
}


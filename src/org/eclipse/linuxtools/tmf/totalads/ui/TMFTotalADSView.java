package org.eclipse.linuxtools.tmf.totalads.ui;

/*
8import java.lang.reflect.Field;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.linuxtools.internal.lttng2.kernel.core.LttngStrings;
import org.eclipse.linuxtools.tmf.core.ctfadaptor.CtfTmfEvent;
import org.eclipse.linuxtools.tmf.core.event.ITmfEvent;
import org.eclipse.linuxtools.tmf.core.event.ITmfEventField;
import org.eclipse.linuxtools.tmf.core.event.ITmfEventType;
import org.eclipse.linuxtools.tmf.core.event.TmfEvent;
*/
//import org.eclipse.linuxtools.tmf.core.request.ITmfEventRequest.ExecutionType;
//import org.eclipse.linuxtools.tmf.core.request.TmfEventRequest;
//import org.eclipse.linuxtools.tmf.tests.stubs.request.TmfEventRequestStub;
import org.eclipse.linuxtools.tmf.core.signal.TmfSignalHandler;
//import org.eclipse.linuxtools.tmf.core.signal.TmfTimestampFormatUpdateSignal;
import org.eclipse.linuxtools.tmf.core.signal.TmfTraceSelectedSignal;
//import org.eclipse.linuxtools.tmf.core.timestamp.TmfTimeRange;
//import org.eclipse.linuxtools.tmf.core.timestamp.TmfTimestampFormat;
import org.eclipse.linuxtools.tmf.core.trace.ITmfTrace;
import org.eclipse.linuxtools.tmf.ui.views.TmfView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Display;
//import org.swtchart.Chart;


public class TMFTotalADSView extends TmfView {
	    private static final String VIEW_ID = "org.eclipse.linuxtools.tmf.totalads.ui.ADS01";
	    
	    private ITmfTrace currentTrace;
	    TotalADS comp;
	    
	public TMFTotalADSView() {
		super(VIEW_ID);
		
	}

	@Override
	public void createPartControl(Composite parent) {
		
		 comp=new TotalADS(parent,SWT.NONE);
		
        ITmfTrace trace = getActiveTrace();
        if (trace != null) {
            traceSelected(new TmfTraceSelectedSignal(this, trace));
        }
       
         
       
	}

	@Override
	public void setFocus() {
		
	}
	
	@TmfSignalHandler
    public void traceSelected(final TmfTraceSelectedSignal signal) {
        // Don't populate the view again if we're already showing this trace
        if (currentTrace == signal.getTrace()) {
            return;
        }
        currentTrace = signal.getTrace();
     
        // Create the request to get data from the trace

     /*   TmfEventRequest req = new TmfEventRequest(TmfEvent.class,
                TmfTimeRange.ETERNITY, 0, TmfEventRequest.ALL_DATA,
                ExecutionType.BACKGROUND)
        //TmfEventRequest req = new TmfEventRequest(TmfEvent.class)
        							{
        							StringBuilder traceBuffer= new StringBuilder();
        							Boolean isKernelSpace=true;
        							ITraceTypeReader traceReader=TraceTypeFactory.getInstance().getCTFKernelorUserReader(isKernelSpace);
        							String tracePath="";
        						
        						        @Override
						            public void handleData(ITmfEvent data) {
						            	  super.handleData(data);
						            	  comp.notifyOnTraceSelection( tracePath, traceReader.getName());
						            	  //System.out.println("***"+data.getContent().getName());
						            	  //System.out.println("***"+data.getContent().getName()+ " "+data.getContent().getValue()
						            		//	  + " "+data.getContent().getField(FIELD));
						            	 //ITmfEventType events= data.getType();
						            	 //data.getTrace();
						            	 traceReader.handleEvents((CtfTmfEvent)data, traceBuffer);
						            	 
						            	 if (tracePath.isEmpty()){
						            		 tracePath=data.getTrace().getPath();
						            		
						            	 }
						                        					            	 
						           					                   
						            }
						
						            @Override
						            public void handleSuccess() {
						            	super.handleSuccess();
						               
						              
						            }
						
						            @Override
						            public void handleFailure() {
						                // Request failed, not more data available
						                super.handleFailure();
						            }
						        				          
        		};
        	*/	
      
        ITmfTrace trace = signal.getTrace();
    	// Right now we are not sure how to determine whether a trace is a user space trace or kernel space trace
        // so we are only considering kernel space traces
        Boolean isKernelSpace=true;
		ITraceTypeReader traceReader=TraceTypeFactory.getInstance().getCTFKernelorUserReader(isKernelSpace);

        comp.notifyOnTraceSelection(trace.getPath(), traceReader.getName());
        // trace.sendRequest(req);
    }
	
}

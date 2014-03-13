package org.eclipse.linuxtools.tmf.totalads.ui;

import java.lang.reflect.Field;
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
import org.eclipse.linuxtools.tmf.core.request.ITmfEventRequest.ExecutionType;
import org.eclipse.linuxtools.tmf.core.request.TmfEventRequest;
//import org.eclipse.linuxtools.tmf.tests.stubs.request.TmfEventRequestStub;
import org.eclipse.linuxtools.tmf.core.signal.TmfSignalHandler;
import org.eclipse.linuxtools.tmf.core.signal.TmfTimestampFormatUpdateSignal;
import org.eclipse.linuxtools.tmf.core.signal.TmfTraceSelectedSignal;
import org.eclipse.linuxtools.tmf.core.timestamp.TmfTimeRange;
import org.eclipse.linuxtools.tmf.core.timestamp.TmfTimestampFormat;
import org.eclipse.linuxtools.tmf.core.trace.ITmfTrace;
import org.eclipse.linuxtools.tmf.ui.views.TmfView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.swtchart.Chart;


public class TMFTotalADSView extends TmfView {
	//	private static final String SERIES_NAME = "Series";
	  //  private static final String Y_AXIS_TITLE = "Signal";
	   // private static final String X_AXIS_TITLE = "Time";
	    //private static final String FIELD = "val"; // The name of the field that we want to display on the Y axis
	    private static final String VIEW_ID = "org.eclipse.linuxtools.tmf.totalads.ui.ADS01";
	    private Chart chart;
	    private ITmfTrace currentTrace;
	    TotalADS comp;
	    
	public TMFTotalADSView() {
		super(VIEW_ID);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		//Display display=new Display(parent);
	    //Shell shell =new Shell();
		 comp=new TotalADS(parent,SWT.NONE);
		//comp.layout(true);
		//comp.pack();
		//parent.pack();
		/* Composite c = new Composite(parent, SWT.NONE);
	        GridLayout cLayout = new GridLayout();
	        cLayout.numColumns = 2;
	        cLayout.marginWidth = 0;
	        cLayout.marginHeight = 0;
	        c.setLayout(cLayout);
    */
	/*	chart = new Chart(parent, SWT.BORDER);
        chart.getTitle().setVisible(false);
        chart.getAxisSet().getXAxis(0).getTitle().setText(X_AXIS_TITLE);
        chart.getAxisSet().getYAxis(0).getTitle().setText(Y_AXIS_TITLE);
        chart.getSeriesSet().createSeries(SeriesType.LINE, SERIES_NAME);
        chart.getLegend().setVisible(false);
        chart.getAxisSet().getXAxis(0).getTick().setFormat(new TmfTimestampFormat());*/
        
        ITmfTrace trace = getActiveTrace();
        if (trace != null) {
            traceSelected(new TmfTraceSelectedSignal(this, trace));
        }
       
         
       
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		//chart.setFocus();
		//comp.setFocus();
		//comp.setFocus();
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
	///class
  /*public class TmfChartTimeStampFormat extends SimpleDateFormat {
	        private static final long serialVersionUID = 1L;
	        @Override
	        public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
	            long time = date.getTime();
	            toAppendTo.append(TmfTimestampFormat.getDefaulTimeFormat().format(time));
	            return toAppendTo;
	        }
	    }

	    @TmfSignalHandler
	    public void timestampFormatUpdated(TmfTimestampFormatUpdateSignal signal) {
	        // Called when the time stamp preference is changed
	        chart.getAxisSet().getXAxis(0).getTick().setFormat(new TmfChartTimeStampFormat());
	        chart.redraw();
	    }*/

}

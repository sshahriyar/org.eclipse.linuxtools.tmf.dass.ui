package org.eclipse.linuxtools.tmf.dass.ui;

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
import org.eclipse.linuxtools.tmf.ui.views.uml2sd.DiagramToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.part.ViewPart;
import org.swtchart.Chart;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.Range;

public class AnomalyDetection extends TmfView {
		private static final String SERIES_NAME = "Series";
	    private static final String Y_AXIS_TITLE = "Signal";
	    private static final String X_AXIS_TITLE = "Time";
	    private static final String FIELD = "val"; // The name of the field that we want to display on the Y axis
	    private static final String VIEW_ID = "org.eclipse.linuxtools.tmf.totalids.ui.ADS01";
	    private Chart chart;
	    private ITmfTrace currentTrace;
	    CompDetective comp;
	    
	public AnomalyDetection() {
		super(VIEW_ID);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		//Display display=new Display(parent);
	    //Shell shell =new Shell();
		CompDetective  comp=new CompDetective(parent,SWT.NONE);
		//comp.layout(true);
		comp.pack();
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

        TmfEventRequest req = new TmfEventRequest(TmfEvent.class,
                TmfTimeRange.ETERNITY, 0, TmfEventRequest.ALL_DATA,
                ExecutionType.BACKGROUND)
        //TmfEventRequest req = new TmfEventRequest(TmfEvent.class)
        							{
        							StringBuilder traceBuffer= new StringBuilder();
        							TraceReader tracetoBuffer=new TraceReader();
        							String tracePath="";
						        	

						            @Override
						            public void handleData(ITmfEvent data) {
						            	  super.handleData(data);
						            	  //System.out.println("***"+data.getContent().getName());
						            	  //System.out.println("***"+data.getContent().getName()+ " "+data.getContent().getValue()
						            		//	  + " "+data.getContent().getField(FIELD));
						            	 ITmfEventType events= data.getType();
						            	 data.getTrace();
						            	 tracetoBuffer.handleEvents((CtfTmfEvent)data, traceBuffer);
						            	 
						            	 if (tracePath.isEmpty()){
						            		 tracePath=data.getTrace().getPath();
						            		  System.out.println(data.getTrace().getPath());
						            	 }
						            		  
						            					            	 
						            	/*String []fieldNames=data.getContent().getFieldNames();
						            	for (int i=0; i < fieldNames.length;i++)
						            		System.out.println(fieldNames[i]);*/
						            	  
						            //	  ITmfEventField field = data.getContent().getField(FIELD);
						             
						              //    if (field != null) 
						                //	  System.out.println("*********"+field.getValue());
						             
						                   
						            }
						
						            @Override
						            public void handleSuccess() {
						            	super.handleSuccess();
						               
						                // This part needs to run on the UI thread since it updates the chart SWT control
						                Display.getDefault().asyncExec(new Runnable() {

						                    @Override
						                    public void run() {
						                    	try{
							                    	Controller ctrl=new Controller();
							                    	DBMS conn= new  DBMS();
							                   	    ctrl.addModels(new KernelStateModeling(conn));
							                   	   // ctrl.trainModels(trainDirectory);
							                   	   //ctrl.validateModels(validationDirectory);
							                   	   ctrl.testTraceUsingModels(traceBuffer,tracePath);
							                   	   conn.closeConnection();
							                   	   System.out.println("Done");
							                   	   
						                    	}catch (Exception ex){
						                    		ex.printStackTrace();
						                    	}
						                    	
						                      
						                    }
						                });
						            }
						
						            @Override
						            public void handleFailure() {
						                // Request failed, not more data available
						                super.handleFailure();
						            }
						            
						          
        		};
        		
      
        ITmfTrace trace = signal.getTrace();
        trace.sendRequest(req);
    }
	///class
  public class TmfChartTimeStampFormat extends SimpleDateFormat {
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
	    }

}

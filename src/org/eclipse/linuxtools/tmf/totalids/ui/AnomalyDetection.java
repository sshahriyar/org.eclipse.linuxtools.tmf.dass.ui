package org.eclipse.linuxtools.tmf.totalids.ui;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.linuxtools.tmf.core.event.ITmfEvent;
import org.eclipse.linuxtools.tmf.core.event.ITmfEventField;
import org.eclipse.linuxtools.tmf.core.event.TmfEvent;
import org.eclipse.linuxtools.tmf.core.request.ITmfDataRequest.ExecutionType;
import org.eclipse.linuxtools.tmf.core.request.TmfEventRequest;
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
	    
	public AnomalyDetection() {
		super(VIEW_ID);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		chart = new Chart(parent, SWT.BORDER);
        chart.getTitle().setVisible(false);
        chart.getAxisSet().getXAxis(0).getTitle().setText(X_AXIS_TITLE);
        chart.getAxisSet().getYAxis(0).getTitle().setText(Y_AXIS_TITLE);
        chart.getSeriesSet().createSeries(SeriesType.LINE, SERIES_NAME);
        chart.getLegend().setVisible(false);
        chart.getAxisSet().getXAxis(0).getTick().setFormat(new TmfTimestampFormat());
        ITmfTrace trace = getActiveTrace();
        if (trace != null) {
            traceSelected(new TmfTraceSelectedSignal(this, trace));
        }
       
        String trainDirectory="/home/umroot/lttng-traces/trace1-session-20131121-150203/";
        
       
        try {
        	 Controller ctrl=new Controller();
        	 DBMS dbConnection=new DBMS();
             ctrl.addModels(new KernelStateModeling(dbConnection));
			 ctrl.trainModels(trainDirectory);
			 dbConnection.closeConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		chart.setFocus();
	}
	
	@TmfSignalHandler
    public void traceSelected(final TmfTraceSelectedSignal signal) {
        // Don't populate the view again if we're already showing this trace
       // if (currentTrace == signal.getTrace()) {
        //    return;
        //}
        currentTrace = signal.getTrace();
        System.out.println("**************************Hello");
        // Create the request to get data from the trace

        TmfEventRequest req = new TmfEventRequest(TmfEvent.class,
                TmfTimeRange.ETERNITY, TmfEventRequest.ALL_DATA,
                ExecutionType.BACKGROUND) {
						        	 ArrayList<Double> xValues = new ArrayList<Double>();
						             ArrayList<Double> yValues = new ArrayList<Double>();
						             private double maxY = -Double.MAX_VALUE;
						             private double minY = Double.MAX_VALUE;
						             private double maxX = -Double.MAX_VALUE;
						             private double minX = Double.MAX_VALUE;

						            @Override
						            public void handleData(ITmfEvent data) {
						            	  super.handleData(data);
						            	  //System.out.println("***"+data.getContent().getName());
						            	  System.out.println("***"+data.getContent().getName()+ " "+data.getContent().getValue());
						            	  String []fieldNames=data.getContent().getFieldNames();
						            	  for (int i=0; i<fieldNames.length;i++)
						            		  System.out.println("***"+fieldNames[i].toString());
						            	  
						            	  ITmfEventField field = data.getContent().getField(FIELD);
						                //  field=null;
						                  if (field != null) {
						                	  System.out.println("*********"+field.getValue());
						                      Double yValue = (Double) field.getValue();
						                      minY = Math.min(minY, yValue);
						                      maxY = Math.max(maxY, yValue);
						                      yValues.add(yValue);

						                      double xValue = (double) data.getTimestamp().getValue();
						                      xValues.add(xValue);
						                      minX = Math.min(minX, xValue);
						                      maxX = Math.max(maxX, xValue);
						                  }
						            }
						
						            @Override
						            public void handleSuccess() {
						            	super.handleSuccess();
						                final double x[] = toArray(xValues);
						                final double y[] = toArray(yValues);

						                // This part needs to run on the UI thread since it updates the chart SWT control
						                Display.getDefault().asyncExec(new Runnable() {

						                    @Override
						                    public void run() {
						                        chart.getSeriesSet().getSeries()[0].setXSeries(x);
						                        chart.getSeriesSet().getSeries()[0].setYSeries(y);

						                        // Set the new range
						                        if (!xValues.isEmpty() && !yValues.isEmpty()) {
						                            chart.getAxisSet().getXAxis(0).setRange(new Range(0, x[x.length - 1]));
						                            chart.getAxisSet().getYAxis(0).setRange(new Range(minY, maxY));
						                        } else {
						                            chart.getAxisSet().getXAxis(0).setRange(new Range(0, 1));
						                            chart.getAxisSet().getYAxis(0).setRange(new Range(0, 1));
						                        }
						                        chart.getAxisSet().adjustRange();

						                        chart.redraw();
						                    }
						                });
						            }
						
						            @Override
						            public void handleFailure() {
						                // Request failed, not more data available
						                super.handleFailure();
						            }
						            
						            /**
						             * Convert List<Double> to double[]
						             */
						            private double[] toArray(List<Double> list) {
						                double[] d = new double[list.size()];
						                for (int i = 0; i < list.size(); ++i) {
						                    d[i] = list.get(i);
						                }

						                return d;
						            }
        		};
        		
       /// 		
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

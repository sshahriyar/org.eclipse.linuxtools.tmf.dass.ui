package org.eclipse.linuxtools.tmf.totalads.ui.live;



//import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSDBMSException;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.linuxtools.tmf.totalads.ui.utilities.SWTResourceManager;
import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
//import org.eclipse.swt.layout.FillLayout;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Shell;
import org.swtchart.Chart;
import org.swtchart.IAxis;
//import org.swtchart.IBarSeries;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;
//import org.swtchart.IErrorBar;
import org.swtchart.ISeriesSet;
import org.swtchart.Range;

/**
 * This class creates a chart
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class LiveXYChart {
	private Chart xyChart;
	private PlotSymbolType []plotSymbols;
	private Color []plotColors;
	private final int MAX_SERIES=5;
	
	/**
	 * Constructor
	 * @param compParent
	 */
	public LiveXYChart(Composite compParent) {
	   
	   xyChart = new Chart(compParent, SWT.NONE);
	   //xyChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	   //xyChart.setLayout(new GridLayout(1,false));
	  //xyChart.getTitle().setText("Anomalies At Different Intervals");
	   xyChart.getTitle().setVisible(false); // Keep title invisible to get more space for the chart
	   xyChart.getAxisSet().getXAxis(0).getTitle().setText("Time (mins)");
	   xyChart.getAxisSet().getYAxis(0).getTitle().setText("Anomalies");
	   Double []x={55.0,56.0,57.0,58.0,59.0,60.0,61.0,62.0,63.0,64.0,65.0,66.0,67.0,68.0,69.0,70.0};
	   Double []y={0.0,1.0,1.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
	   
	   		
		setXRange(0, 9,100);
		setYRange(-1, 2);
		
	   
	   plotSymbols=new PlotSymbolType[MAX_SERIES];
	   plotSymbols[0]=PlotSymbolType.CIRCLE;
	   plotSymbols[1]=PlotSymbolType.DIAMOND;
	   plotSymbols[2]=PlotSymbolType.CROSS;	
	   plotSymbols[3]=PlotSymbolType.INVERTED_TRIANGLE;
	   plotSymbols[4]=PlotSymbolType.SQUARE;
	   
	   plotColors=new Color[MAX_SERIES];
	   plotColors[0]=SWTResourceManager.getColor(SWT.COLOR_GREEN);
	   plotColors[1]=SWTResourceManager.getColor(SWT.COLOR_BLUE);
	   plotColors[2]=SWTResourceManager.getColor(SWT.COLOR_RED);
	   plotColors[3]=SWTResourceManager.getColor(SWT.COLOR_MAGENTA);
	   plotColors[4]=SWTResourceManager.getColor(SWT.COLOR_GRAY);
	   
	}
	
	


/**
 * This function initialises different series in a chart. It must be called immediately after the creation of an object
 * @param seriesNames An array of series names
 * 
 */
 public  void inititaliseSeries(final String[] seriesNames) {
	 
	 Display.getDefault().asyncExec(new Runnable() {//Always execute on the main GUI thread
			
			@Override
			public void run() { 
				    for (int j=0; j<seriesNames.length;j++){
				    	
				    	ILineSeries lineSeries = (ILineSeries) xyChart.getSeriesSet().
				    							createSeries(SeriesType.LINE, seriesNames[j]);
				 
				    	lineSeries.setSymbolType(getSymbol(j));
				    	lineSeries.setLineColor(getColor(j));
				    	lineSeries.enableStep(true);
				    }
			}
	 });

}
 /**
  * Sets minimum and maximum on Y axis
  * @param min
  * @param max
  */
 public void setYRange(final int min, final int max){
	  Display.getDefault().syncExec(new Runnable() {//Always execute on the main GUI thread
			@Override
			public void run() {
				
				
				IAxis yAxis= xyChart.getAxisSet().getYAxis(0);
				yAxis.setRange(new Range(min, max));
				yAxis.getTick().setTickMarkStepHint(100);
			//	xyChart.getAxisSet().adjustRange();
			}
	  });
 }
 
 /**
  * Sets minimum and maximum on X axis
  * @param min
  * @param max
  */
 public void setXRange(final int min,final  int max, final int step){
	  Display.getDefault().syncExec(new Runnable() {//Always execute on the main GUI thread
			@Override
			public void run() {
				IAxis xAxis= xyChart.getAxisSet().getXAxis(0);
				xAxis.setRange(new Range(min, max));
				xAxis.getTick().setTickMarkStepHint(step);//xyChart.getAxisSet().adjustRange();
			}
	  });
 }
 /**
  * Returns the symbol for a series out of five symbols
  * @param index Index of five symbols
  * @return PlotSymbolType
  */
 private PlotSymbolType getSymbol(int index){
	 if (index<MAX_SERIES)
		 return plotSymbols[index];
	 else
		 return plotSymbols[0];
 }
 
 /**
  * Returns the color for a series out of five colors
  * @param index Index of five colors
  * @return Color
  */
 private Color getColor(int index){
	 if (index<MAX_SERIES)
		 return plotColors[index];
	 else
		 return plotColors[0];
 }
 
 /**
  * This function allows to add the values to a series that has been intialized earlier using inititalizeSeries
  * function. It throws an exception if series name is not found
  * @param ySeries Array of double values
  * @param seriesName Series name
  * @throws TotalADSUIException
  */
public void addYSeriesValues(double []ySeries, String seriesName) throws TotalADSUIException{
	// create line series
    ILineSeries lineSeries = (ILineSeries) xyChart.getSeriesSet().getSeries(seriesName);
    if (lineSeries==null)
    	throw new  TotalADSUIException("No such series");
    lineSeries.setYSeries(ySeries);
    
}
/**
 * This function allows to add the values to a series that has been intialized earlier using inititalizeSeries
 * function. It throws an exception if series name is not found
 * @param ySeries Array of double values
 * @param seriesName Series name
 * @throws TotalADSUIException
 */
public void addYSeriesValues(Double []ySeries, String seriesName) throws TotalADSUIException{
	//First convert Double to double
	double []yVals=new double[ySeries.length];
	for (int j=0;j<ySeries.length;j++)
		yVals[j]=ySeries[j];
	
	// create line series
   ILineSeries lineSeries = (ILineSeries) xyChart.getSeriesSet().getSeries(seriesName);
   if (lineSeries==null)
   	throw new  TotalADSUIException("No such series");
   lineSeries.setYSeries(yVals);
   
}
/**
 * This function sets the values on X axis that would be displayed for corresponding points on Y axis
 * @param xSeries An array of double values
 * @throws TotalADSUIException 
 */
public void addXSeriesValues(double []xSeries, String seriesName) throws TotalADSUIException{
	// create line series
    //xyChart.getSeriesSet().getSeries()[0].setXSeries(xSeries);
	 ILineSeries lineSeries = (ILineSeries) xyChart.getSeriesSet().getSeries(seriesName);
	 if (lineSeries==null)
	   	throw new  TotalADSUIException("No such series");
	 lineSeries.setXSeries(xSeries);

}
/**
 * This function sets the values on X axis that would be displayed for corresponding points on Y axis
 * @param xSeries An array of double values
 * @throws TotalADSUIException 
 */
public void addXSeriesValues(Double []xSeries,String seriesName) throws TotalADSUIException{
	//First convert Double to double
	double []xVals=new double[xSeries.length];
	for (int j=0;j<xSeries.length;j++)
			xVals[j]=xSeries[j];
	// create line series
   // xyChart.getSeriesSet().getSeries()[0].setXSeries(xVals);
	 ILineSeries lineSeries = (ILineSeries) xyChart.getSeriesSet().getSeries(seriesName);
	 if (lineSeries==null)
	   	throw new  TotalADSUIException("No such series");
	 lineSeries.setXSeries(xVals);

}
/**
 * Draws the chart.  It must be called every time the series is updated
 */
public void drawChart(){

	  Display.getDefault().syncExec(new Runnable() {//Always execute on the main GUI thread
		
		@Override
		public void run() {
			// adjust the axis range
			xyChart.getAxisSet().adjustRange();
			xyChart.redraw();
			
			//nxyChart.getParent().redraw();
			//xyChart.redraw(, y, width, height, all);;
		}
	 });
}
/*
 * Clears the chart
 */
public void clearChart(){
	
	Display.getDefault().syncExec(new Runnable() {//Always execute on the main GUI thread
		
		@Override
		public void run() {
			ISeriesSet seriesSet=xyChart.getSeriesSet();
			ISeries []series= xyChart.getSeriesSet().getSeries();
			
			for (int j=0;j<series.length;j++)
				seriesSet.deleteSeries(series[j].getId());
			
			xyChart.redraw();
			
		}
	});
	
}



}

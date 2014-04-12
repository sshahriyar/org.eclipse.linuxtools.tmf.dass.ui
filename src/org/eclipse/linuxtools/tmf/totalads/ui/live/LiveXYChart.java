package org.eclipse.linuxtools.tmf.totalads.ui.live;



import org.eclipse.linuxtools.tmf.totalads.ui.utilities.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.swtchart.Chart;
import org.swtchart.IBarSeries;
import org.swtchart.ILineSeries;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.ISeries;
import org.swtchart.ISeries.SeriesType;
import org.swtchart.IErrorBar;

/**
 * 
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class LiveXYChart {
	private Chart xyChart;
	public LiveXYChart(Composite compParent) {
		//xyChart=new Chart(compParent, SWT.NONE);
		//xyChart.setLayoutData(gridData);
		createChart(compParent);
		Button b=new Button(compParent, SWT.NONE);
		b.setLayoutData(gridData);
	
		//b.setText("test");
		//b.setLayoutData();
	}
	
	private static final double[] ySeries = { 0.0, 0.38, 0.71, 0.92, 1.0, 0.92,
        0.71, 0.38, 0.0, -0.38, -0.71, -0.92, -1.0, -0.92, -0.71, -0.38 };
	
	private static final double[] ySeries2 = { 0.0, 0.50, 0.83, 0.88, 1.0, 0.92,
        0.66, 0.50, 0.0, -0.25, -0.60, -0.988 -1.0, -0.66, -0.51, -0.58 };

/**
 * The main method.
 * 
 * @param args
 *            the arguments
 */
public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    shell.setText("Line Chart");
    shell.setSize(500, 400);
    shell.setLayout(new FillLayout());

    createChart(shell);

    shell.open();
    while (!shell.isDisposed()) {
        if (!display.readAndDispatch()) {
            display.sleep();
        }
    }
    display.dispose();
}

/**
 * create the chart.
 * 
 * @param parent
 *            The parent composite
 * @return The created chart
 */
static public Chart createChart(Composite parent) {

    // create a chart
    Chart chart = new Chart(parent, SWT.NONE);
   // chart.setLayoutData(grd);
    // set titles
    chart.getTitle().setText("Line Chart");
    chart.getAxisSet().getXAxis(0).getTitle().setText("Snapshot Interval");
    chart.getAxisSet().getYAxis(0).getTitle().setText("Anomalies");

    // create line series
    ILineSeries lineSeries = (ILineSeries) chart.getSeriesSet()
            .createSeries(SeriesType.LINE, "KSM");
    lineSeries.setSymbolType(PlotSymbolType.DIAMOND);
    lineSeries.setYSeries(ySeries);
    
    ILineSeries lineSeries2 = (ILineSeries) chart.getSeriesSet()
            .createSeries(SeriesType.LINE, "SWN");
    lineSeries2.setYSeries(ySeries2);
    lineSeries2.setSymbolType(PlotSymbolType.CIRCLE);
    lineSeries2.setLineColor(SWTResourceManager.getColor(SWT.COLOR_GREEN));
    // adjust the axis range
    chart.getAxisSet().adjustRange();

    return chart;
}
}

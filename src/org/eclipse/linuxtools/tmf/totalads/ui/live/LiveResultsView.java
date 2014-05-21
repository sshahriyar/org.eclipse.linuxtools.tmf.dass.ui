package org.eclipse.linuxtools.tmf.totalads.ui.live;

import org.eclipse.linuxtools.tmf.totalads.ui.results.ResultsAndFeedback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

/**
 * Creates a view for Live Results
 *
 * @author <p>
 *         Syed Shariyar Murtaza jusstshary@hotmail.com
 *         </p>
 *
 */
public class LiveResultsView extends ViewPart {
    /**
     * View ID
     */
    public static final String VIEW_ID = "org.eclipse.linuxtools.tmf.totalads.ui.live.LiveResultsView"; //$NON-NLS-1$
    private LiveXYChart liveXYChart;
    private ResultsAndFeedback results;

    /**
     * Default constructor
     */
    public LiveResultsView() {

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
     * .Composite)
     */
    @Override
    public void createPartControl(Composite compParent) {
        TabFolder tabFolder = new TabFolder(compParent, SWT.NONE);
        TabItem itemChart = new TabItem(tabFolder, SWT.NONE);
        itemChart.setText("Timeline");
        TabItem itemResults = new TabItem(tabFolder, SWT.NONE);
        itemResults.setText("Details");

        Composite compChart = new Composite(tabFolder, SWT.NONE);
        compChart.setLayout(new FillLayout());
        liveXYChart = new LiveXYChart(compChart);
        itemChart.setControl(compChart);

        Composite compResults = new Composite(tabFolder, SWT.NONE);
        compResults.setLayout(new GridLayout(1, false));
        results = new ResultsAndFeedback(compResults, false);
        itemResults.setControl(compResults);

    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    @Override
    public void setFocus() {

    }

    /**
     * Get LiveXYChart object
     *
     * @return LiveXYChart object
     */
    public LiveXYChart getLiveChart() {

        return liveXYChart;
    }

    /**
     * Get ResultsAndFeedback object
     *
     * @return ResultsAndFeedback object
     */
    public ResultsAndFeedback getResults() {
        return results;
    }

}

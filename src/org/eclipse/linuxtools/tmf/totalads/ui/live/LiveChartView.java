package org.eclipse.linuxtools.tmf.totalads.ui.live;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class LiveChartView extends ViewPart {
	public static final String VIEW_ID = "org.eclipse.linuxtools.tmf.totalads.ui.live.LiveChartView";
	
	private LiveXYChart liveXYChart;
	public LiveChartView() {
		 
		
	}

	@Override
	public void createPartControl(Composite compParent) {
		liveXYChart =new LiveXYChart(compParent);

	}

	@Override
	public void setFocus() {
		// TODO 

	}
	public LiveXYChart getLiveChart(){
		
		return liveXYChart; 
	}
}

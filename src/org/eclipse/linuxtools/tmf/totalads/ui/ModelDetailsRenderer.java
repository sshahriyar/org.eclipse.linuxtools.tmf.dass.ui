package org.eclipse.linuxtools.tmf.totalads.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class ModelDetailsRenderer {

	public ModelDetailsRenderer(Composite comptbtmAnalysis){
		
		Text txtAnalysisDetails = new Text(comptbtmAnalysis, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
		txtAnalysisDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		txtAnalysisDetails.setText("\"FS\" : 0.53\n \"MM\" : 0.12\n \"KL\" : 0.18\n \"AC\" : 0.01 \n\"IPC\" : 0\n \"NT\" : 0.01\n \"SC\" : 0\n \"UN\" : 0.18");
		//txtAnalysisDetails.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,1,10));
				
		CLabel lblAnalysisChart = new CLabel(comptbtmAnalysis, SWT.BORDER);
		lblAnalysisChart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		lblAnalysisChart.setImage(SWTResourceManager.getImage("/home/umroot/experiments/workspace/tmf-ads/"
				+ "org.eclipse.linuxtools/lttng/org.eclipse.linuxtools.tmf.totalads.ui/icons/java-twiki-metrpreter2.png"));
	
	}
	
}

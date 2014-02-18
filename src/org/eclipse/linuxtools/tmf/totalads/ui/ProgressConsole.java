package org.eclipse.linuxtools.tmf.totalads.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class ProgressConsole {
public ProgressConsole(Composite comptbtmModeling){
	/**
	 * Progress Console
	 * 		
	 */
		
	
	CLabel lblProgressConsole = new CLabel(comptbtmModeling, SWT.NONE);
	lblProgressConsole.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,true,false,4,1));
	lblProgressConsole.setText("Progress Console");
	lblProgressConsole.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
	
	
	Text txtAnomaliesProgress = new Text(comptbtmModeling, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL);
	txtAnomaliesProgress.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,4,4));
	txtAnomaliesProgress.setBackground(SWTResourceManager.getColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
	txtAnomaliesProgress.setText("Reading Trace Kernel-session-27-13\nTransforming to states\nInserting into the database host-app-01\n.....................\n");
	
	
	
}
}

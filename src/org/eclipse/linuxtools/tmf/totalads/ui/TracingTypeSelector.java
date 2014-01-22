package org.eclipse.linuxtools.tmf.totalads.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

public class TracingTypeSelector {
	
	public TracingTypeSelector(Composite parent){
		/*
		 * Trace Type Selection
		 */
		
		Group grpTraceType = new Group(parent, SWT.NONE);
		grpTraceType.setText("Tracing Mode");
		grpTraceType.setLayoutData(new GridData(SWT.LEFT,SWT.BOTTOM,true,false));
		grpTraceType.setLayout(new GridLayout(1,false));
		
		//GridData traceTypeGridData=new GridData(SWT.FILL,SWT.TOP,true,false);
		
		Button btnTraceTypeLttngkernel = new Button(grpTraceType, SWT.CHECK);
		//btnTraceTypeLttngkernel.setLayoutData(traceTypeGridData);
		btnTraceTypeLttngkernel.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		btnTraceTypeLttngkernel.setText("LTTng-kernel");
		
		Button btnTraceTypeLttngust = new Button(grpTraceType, SWT.CHECK);
		btnTraceTypeLttngust.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));//tracetype
		btnTraceTypeLttngust.setText("LTTng-UST");
		
		Button btnTraceTypeText = new Button(grpTraceType, SWT.CHECK);
		btnTraceTypeText.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		btnTraceTypeText.setText("Text");
		
		Text txtTraceTypeRegularExpression = new Text(grpTraceType, SWT.BORDER);
		txtTraceTypeRegularExpression.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false));
		txtTraceTypeRegularExpression.setForeground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		txtTraceTypeRegularExpression.setText("Enter Regular Expression");
		
	}

}

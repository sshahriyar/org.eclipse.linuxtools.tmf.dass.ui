/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.ui.modeling;

import org.eclipse.linuxtools.tmf.totalads.core.Configuration;
import org.eclipse.linuxtools.tmf.totalads.dbms.IObserver;
import org.eclipse.linuxtools.tmf.totalads.ui.utilities.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * This class creates the status bar and provides the necessary functions to update the status from background threads
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class StatusBar {
	private Composite compStatus;
	private Label lblProgress;
	private Label lblStatus;
	private int colorChooser;
	private int maxColors;
	public StatusBar(Composite compParent) {
		
		maxColors=2;
		compStatus=new Composite(compParent, SWT.NONE);
		compStatus.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		compStatus.setLayout(new GridLayout(2, false));
		
		lblProgress= new Label(compStatus, SWT.NONE);
		lblProgress.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		
		lblStatus= new Label(compStatus, SWT.BORDER);
		lblStatus.setLayoutData(new GridData(SWT.RIGHT,SWT.TOP,true,false,1,1));
		
		// Adding an observer to the connection
		Configuration.connection.addObserver(new IObserver() {
			
			@Override
			public void update() {
				initialState();
				
			}
		});
		
		initialState();
	}
	
	/**
	 * 			
	 * Sets the status bar to the initial state showing the connection status of the database
	 * 
	 */
	public void initialState(){
		colorChooser=0;
		Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				if (Configuration.connection.isConnected()){
					
					lblProgress.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
					lblProgress.setText("Connected to localhost..");
				
					lblStatus.setBackground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
					lblStatus.setText("        ");
				}else {
					lblProgress.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
					lblProgress.setText("Connection to database failed..");
				
					lblStatus.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
					lblStatus.setText("        ");
				}
				
			}
		});
		
	}
	/**
	 * Sets the label to the given message
	 * @param shortMsg Short status message
	 */
	public void setProgress(final String shortMsg){
		
		Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
				 lblProgress.setText(shortMsg);
				 lblProgress.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
				  
				 if (colorChooser%maxColors==0){
					 lblStatus.setBackground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
					 
					 colorChooser=1;
				 }
				 else  if (colorChooser%maxColors==1){
					 lblStatus.setBackground(SWTResourceManager.getColor(SWT.COLOR_CYAN));
					 colorChooser=0;
				 }
				
			}
		});
		
	}
	
	
	
}

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
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;

/**
 * This class creates the status bar and provides the necessary functions to update the status from background threads
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class StatusBar {
	private Composite compStatus;
	private Label lblProgress;
	private Label lblStatus;
	private Button btnConnection;
	private int colorChooser;
	private int maxColors;
	public StatusBar(Composite compParent) {
		
		maxColors=2;
		compStatus=new Composite(compParent, SWT.NONE);
		compStatus.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		compStatus.setLayout(new GridLayout(3, false));
		
		lblProgress= new Label(compStatus, SWT.NONE);
		lblProgress.setLayoutData(new GridData(SWT.FILL,SWT.TOP,true,false,1,1));
		
		btnConnection= new Button(compStatus, SWT.BORDER);
		btnConnection.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
		btnConnection.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,false,1,1));
		btnConnection.setText("Connect");
		
		lblStatus= new Label(compStatus, SWT.BORDER);
		lblStatus.setLayoutData(new GridData(SWT.RIGHT,SWT.CENTER,false,false,1,1));
		lblStatus.setText("       ");
	
		addEventHandlers();
		
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
					btnConnection.setVisible(false);
					
					
					
									
					//lblStatus.setImage(SWTResourceManager.getImage("/home/umroot/experiments/workspace/org.eclipse.linuxtools/lttng/org.eclipse.linuxtools.tmf.totalads/icons/sample.gif"));
				}else {
					lblProgress.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
					lblProgress.setText("Connection to database failed..");
					lblStatus.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
					btnConnection.setVisible(true);
					
					
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
	/**
	 * Function to add event handlers
	 */
	private void addEventHandlers(){
		// Adding an event handler for mouse up event
		btnConnection.addMouseListener(new MouseAdapter() {
		 @Override
			public void mouseUp(MouseEvent e) {
				
				String err=	Configuration.connection.connect(Configuration.host, Configuration.port);
				if (!err.isEmpty()){
					MessageBox msgBox= new MessageBox(org.eclipse.ui.PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
					           ,SWT.ICON_ERROR|SWT.OK);
					msgBox.setMessage(err);
					msgBox.open();
				}
				//initialState();
					
			}
			
		});
		
		// Adding an observer to the connection
		Configuration.connection.addObserver(new IObserver() {
					@Override
					public void update() {
						initialState();
						
					}
		});
		
	
	}
	
}

/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/

package org.eclipse.linuxtools.tmf.totalads.ui.io;


import java.util.ArrayList;

import org.eclipse.linuxtools.tmf.totalads.dbms.IObserver;
import org.eclipse.linuxtools.tmf.totalads.ui.utilities.SWTResourceManager;
import org.eclipse.swt.SWT;
//import org.eclipse.swt.custom.CLabel;
//import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

//import com.sun.corba.se.impl.ior.NewObjectKeyTemplateBase;
/**
 * This class implements the GUI widget for the progress console visible on the LiveMonitor tab
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class TotalADSOutStream {
	private Label lblProgressConsole;
	private Text txtAnomaliesProgress;
	private int MAX_TEXT_SIZE=20000;
	private ArrayList<IObserver> observers;
	
	/**
	 * Constructor
	 */
	public TotalADSOutStream(){
		observers=new ArrayList<IObserver>();
	}
	/**
	 * Constructor
	 * @param compParent Composite widget
	 */
	public TotalADSOutStream(Composite compParent){
	/**
	 * Progress Console
	 * 		
	 */
		lblProgressConsole = new Label(compParent, SWT.NONE);
		lblProgressConsole.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,true,false,4,1));
		lblProgressConsole.setText("Progress Console");
		
			
		
		txtAnomaliesProgress = new Text(compParent, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL |SWT.MULTI);
		txtAnomaliesProgress.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,4,4));
		txtAnomaliesProgress.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtAnomaliesProgress.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		
	}
	/**
	 * Constructor taking grid style as arguments
	 * @param compParent
	 * @param gridDataLabel
	 * @param gridDataConsole
	 */
	public TotalADSOutStream(Composite compParent, GridData gridDataLabel, GridData gridDataConsole){
	/**
	 * Progress Console
	 * 		
	 */

		
		lblProgressConsole = new Label(compParent, SWT.NONE);
		lblProgressConsole.setLayoutData(gridDataLabel);
		lblProgressConsole.setText("Progress Console");
		
			
		
		txtAnomaliesProgress = new Text(compParent, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL |SWT.MULTI);
		txtAnomaliesProgress.setLayoutData(gridDataConsole);
		txtAnomaliesProgress.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		txtAnomaliesProgress.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLUE));
		
	}
	
	/**
	 * checks text size
	 */
	private void checksTextSize(){
		String text=txtAnomaliesProgress.getText();
		
		if (text.length()>=MAX_TEXT_SIZE){
		
				int newLineIndex=txtAnomaliesProgress.getText().indexOf("\n");
				if (newLineIndex==-1)
					newLineIndex=20;//deltefirst 20 characters if there is no new line
				
				txtAnomaliesProgress.setText(text.substring(newLineIndex+1,text.length()));
		}
	}
	/**
	 * Prints text in the text box
	 * @param txt Information to print
	 */
	
	public void printText(final String txt){
		
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				checksTextSize();		
				txtAnomaliesProgress.append(txt);
			
			}
		});
	}
	/**
	 * Prints text in the text box with a new line
	 * @param event Information to display on the output stream
	 */
	
	public void addOutputEvent(String event){
		notifyObservers(event);
		
	}


	/**
	 * Prints new line in the text box
	 */
	public void addNewLine(){
		notifyObservers("\n");
	}
	/**
	 * clears the text box
	
	public void clearText(){
		Display.getDefault().syncExec(new Runnable() {
			
			@Override
			public void run() {
					txtAnomaliesProgress.setText("");
			}
		});
		
	}*/
	/**
	 * Adds an observer of type 
	 * @param observer
	 */

	public void addObserver(IObserver observer){
		observers.add(observer);
		
	}
	/**
	 * Removes an observer of type 
	 * @param observer
	 */

	public void removeObserver(IObserver observer){
		observers.remove(observer);
		
	}
	/**
	 * Notifies all observers of type 
	 */

	private void notifyObservers(String event){
		for (IObserver ob: observers)
			ob.update();
	}
}

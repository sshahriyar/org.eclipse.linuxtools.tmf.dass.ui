/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.ui.models;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSGeneralException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
/**
 * This class automatically generates a form at runtime for training or testing settings of an algorithm
 * by using an options array as a name value pair of elements. Even fields in options array becomes
 * labels and odd fields become values.
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class SettingsForm {

	private Label []lblOption;
	private Text []txtOption;
	private String []modelOptions;



	/**
	 * Constructor
	 * @param options
	 * @param compParent
	 * @throws TotalADSGeneralException 
	 */
	public SettingsForm(String []options, Composite compParent) throws TotalADSGeneralException{
		if (options.length % 2==1)
			 throw new TotalADSGeneralException("Settings must be even: name and value pairs.");
	
		createForm(options, compParent);
	}

	
	/**
	 * Creates the contents of the form
	 * @param options
	 * @param dialogShel
	 */
	private void createForm(String []options, Composite compParent){
		 Composite compSettings=new Composite(compParent, SWT.NONE);
		 compSettings.setLayoutData(new GridData(SWT.FILL,SWT.FILL, true,true)); 
		 compSettings.setLayout(new GridLayout(4,false));
		 
		 modelOptions=options;
		 Integer widgetsCount=options.length/2;
		 lblOption=new Label[widgetsCount];
		 txtOption=new Text[widgetsCount];
		  
		 for (int j=0; j<options.length; j++){
			 int idx=j/2;
			 if (j%2==0){
				 
				 lblOption[idx]=new Label(compSettings, SWT.NONE);
				 lblOption[idx].setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true,false,2,1));
				 lblOption[idx].setText(options[j]);
			 }
			 else{
				 txtOption[idx]=new Text(compSettings, SWT.NONE);
				 txtOption[idx].setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false,2,1));
				 txtOption[idx].setText(options[j]);
			 }
		 }
		
	}
	
	/**
	 * Returns selected options 
	 * @return options as array selected by the user
	 * @throws TotalADSGeneralException 
	 */
	public String[] getSettings() throws TotalADSGeneralException{
		saveSelectedSettings();
		return modelOptions;
	}
	
	/**
	 * Saves settings in an array
	 * @throws TotalADSGeneralException
	 */
	private void saveSelectedSettings() throws TotalADSGeneralException{
		int optionCount=-1;
		   for (int i=0; i<txtOption.length;i++){
			   optionCount=optionCount+2;
			   if (txtOption[i].getText().isEmpty())
				   throw new TotalADSGeneralException("Empty fields are not allowed");
			   else   
				   modelOptions[optionCount]=txtOption[i].getText();
		   }
	}
	
}

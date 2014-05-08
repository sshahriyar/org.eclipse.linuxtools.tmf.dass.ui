/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.ui;
import org.eclipse.linuxtools.tmf.totalads.exceptions.TotalADSUIException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
//import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
/**
 * This class generates a form at runtime for training or testing settings of an algorithm
 * by using an options array as a key value pair of elements. Even fields in options array becomes
 * labels and odd fields become values.
 * @author <p> Syed Shariyar Murtaza justsshary@hotmail.com </p>
 *
 */
public class Settings {
	private Display display;
	private Shell dialogShel;
	private Label []lblOption;
	private Text []txtOption;
	private String []modelOptions;
	private Button btnOK;
	private Button btnCancel;
	//Composite composite;
	/**
	 * Constructor
	 * @param options Array with pair of fields as keys and values
	 * @throws TotalADSUIException
	 */
	public Settings(String []options) throws TotalADSUIException {
		
		if (options.length % 2==1)
			 throw new TotalADSUIException("Options must be even: key and value pairs.");
		
		
		 display = Display.getDefault();
		 dialogShel = new Shell(display, SWT.BORDER | SWT.CLOSE | SWT.APPLICATION_MODAL|SWT.V_SCROLL);
		 createForm(options, dialogShel);
		 dialogShel.setLayout(new GridLayout(1, false));
		 dialogShel.setSize(500, 150);
		 
		 
	}
	/**
	 * Non-dialog constructor
	 * @param options
	 * @param compParent
	 * @throws TotalADSUIException 
	 */
	public Settings(String []options, Composite compParent) throws TotalADSUIException{
		if (options.length % 2==1)
			 throw new TotalADSUIException("Options must be even: key and value pairs.");
	
		createForm(options, compParent);
	}
	/**
	 * Shows the modal form
	 */
	public void showForm(){
		dialogShel.open();
		while (!dialogShel.isDisposed()) {
		    if (!display.readAndDispatch()) {
		        display.sleep();
		    }
		}
		
	}// end function ShowForm
	
	
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
		 //new Label(dialogShel, SWT.NONE);// add two empty labels for first two cells
		 //new Label(dialogShel, SWT.NONE);
		 
		 
		/* btnOK=new Button(dialogShel,SWT.NONE);
		 btnOK.setLayoutData(new GridData(SWT.RIGHT,SWT.BOTTOM,true,false,1,1));
		 btnOK.setText("      OK       ");
		 
		 btnCancel=new Button(dialogShel,SWT.NONE);
		 btnCancel.setLayoutData(new GridData(SWT.RIGHT,SWT.BOTTOM,false,false,1,1));
		 btnCancel.setText("   Cancel   ");
		 addListeners();*/
	}
	
	/**
	 * Returns selected options 
	 * @return options as array selected by the user
	 * @throws TotalADSUIException 
	 */
	public String[] getSettings() throws TotalADSUIException{
		saveSelectedSettings();
		return modelOptions;
	}
	/**
	 * Saves settings in an array
	 * @throws TotalADSUIException
	 */
	private void saveSelectedSettings() throws TotalADSUIException{
		int optionCount=-1;
		   for (int i=0; i<txtOption.length;i++){
			   optionCount=optionCount+2;
			   if (txtOption[i].getText().isEmpty())
				   throw new TotalADSUIException("Empty fields are not allowed");
			   else   
				   modelOptions[optionCount]=txtOption[i].getText();
		   }
	}
	/**
     * Adding listeners to buttons 
     */
	private void addListeners(){
		//Listener for OK button
		btnOK.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseUp(MouseEvent e) {
				   
					//saveSelectedSettings();
					dialogShel.dispose();
				}
	 });
	 
	 // Listener for cancel button
	 btnCancel.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseUp(MouseEvent e) {
					
					//modelOptions=null;
					dialogShel.dispose();
				}
	 });
	
	}
}

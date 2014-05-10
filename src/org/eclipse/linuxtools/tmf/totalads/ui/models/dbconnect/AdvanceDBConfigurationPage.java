/*********************************************************************************************
 * Copyright (c) 2014  Software Behaviour Analysis Lab, Concordia University, Montreal, Canada
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of XYZ License which
 * accompanies this distribution, and is available at xyz.com/license
 *
 * Contributors:
 *    Syed Shariyar Murtaza
 **********************************************************************************************/
package org.eclipse.linuxtools.tmf.totalads.ui.models.dbconnect;


import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
/**
 * This class implements advance configurations  to connect to a database management system
 * @author <p> Syed Shariyar Murtaza </p>
 *
 */
public class AdvanceDBConfigurationPage extends WizardPage {
	//Variables
	private Label lblUser;
	private Text txtUser;
	private Label lblPassword;
	private Text txtPassword;
	private Label lblDatabase;
	private Text txtDatabase;
	
	
	
	
	/**
	 * Constructor
	 * @param parentShell
	 */
	public AdvanceDBConfigurationPage() {
		super("Advance Configuration");
		setTitle("Advance database configurations");
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite compParent) {
	
		Composite compConfigure=new Composite(compParent, SWT.NONE);
		compConfigure.setLayoutData(new GridData(GridData.FILL_BOTH));
		compConfigure.setLayout(new GridLayout(2, false));
		
		lblUser= new Label(compConfigure,SWT.NONE);
		lblUser.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
		lblUser.setText("User Name");
		
		txtUser=new Text(compConfigure, SWT.BORDER);
		txtUser.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
		txtUser.setText("");
		
		lblPassword= new Label(compConfigure,SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
		lblPassword.setText("Password ");
		
		txtPassword=new Text(compConfigure, SWT.BORDER|SWT.PASSWORD);
		txtPassword.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
		txtPassword.setText("");
		
		lblDatabase= new Label(compConfigure,SWT.NONE);
		lblDatabase.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
		lblDatabase.setText("Database ");
		
		txtDatabase=new Text(compConfigure, SWT.BORDER);
		txtDatabase.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
		txtDatabase.setText("");
		
		setControl(compConfigure);
		setPageComplete(true);
		
	}

	
	
	/**
	 * Returns the user name
	 * @return User name
	 */
	public String getUserName(){
		return txtUser.getText();
	}
	
	/**
	 * Returns the password
	 * @return Password
	 */
	public String getPassword(){
		return txtPassword.getText();
	}
	
	/**
	 * Returns the database
	 * @return Database
	 */
	public String getDatabase(){
		return txtDatabase.getText();
	}
	
	
}

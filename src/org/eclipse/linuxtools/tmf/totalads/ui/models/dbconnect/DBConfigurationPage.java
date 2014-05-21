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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
/**
 * This class implements basic configurations to connect to a database management system
 * @author <p> Syed Shariyar Murtaza </p>
 *
 */
public class DBConfigurationPage extends WizardPage {
	//Variables
	private Label lblHost;
	private Text txtHost;
	private Label lblPort;
	private Text txtPort;
	private boolean isEmpty;
	private final String HOST="localhost";
	private final String PORT="27017";
	/**
	 * Constructor
	 * @param parentShell
	 */
	public DBConfigurationPage() {
		super("DB Configuration");
		setTitle("Basic database configurations");
		setDescription("Press Finish to connect or press Next for advance authentication");
		isEmpty=false;
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

		lblHost= new Label(compConfigure,SWT.NONE);
		lblHost.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
		lblHost.setText("Host ");

		txtHost=new Text(compConfigure, SWT.BORDER);
		txtHost.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
		txtHost.setText(HOST);

		lblPort= new Label(compConfigure,SWT.NONE);
		lblPort.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
		lblPort.setText("Port ");

		txtPort=new Text(compConfigure, SWT.BORDER);
		txtPort.setLayoutData(new GridData(SWT.FILL,SWT.BOTTOM,true,false));
		txtPort.setText(PORT);

		setControl(compConfigure);
		setPageComplete(true);
		//eventhandlers
		txtHost.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (txtHost.getText().isEmpty()){
					isEmpty=true;
					setPageComplete(false);
				}else{
					isEmpty=false;
					setPageComplete(true);
				}
			}
		});


		txtPort.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (txtPort.getText().isEmpty()){
					isEmpty=true;
					setPageComplete(false);
				}else{
					isEmpty=false;
					setPageComplete(true);
				}
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {
		if (!isEmpty) {
            return true;
        }
        return false;

	}

	/**
	 * Returns the name of the host
	 * @return
	 */
	public String getHost(){
		return txtHost.getText();
	}

	/**
	 * Returns the name of the port
	 * @return
	 */
	public String getPort(){
		return txtPort.getText();
	}

}
